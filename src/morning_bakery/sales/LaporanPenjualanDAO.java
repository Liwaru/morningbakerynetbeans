package morning_bakery.sales;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Query laporan berdasarkan tabel orders, order_details, menus, dan tables. */
public final class LaporanPenjualanDAO {

    private static final String DB_URL
            = "jdbc:mysql://127.0.0.1:3306/swiftbite"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final List<String> STATUS_KEYS = List.of(
            "menunggu", "diproses", "siap_diantar",
            "menunggu_pembayaran", "selesai", "dibatalkan");
    private static final List<String> PAYMENT_LABELS = List.of(
            "Tunai", "QRIS", "GoPay", "DANA", "OVO",
            "ShopeePay", "E-Wallet Lain");

    public SalesReportData getSalesReport(SalesFilter filter)
            throws SQLException {
        try (Connection connection = openConnection()) {
            return new SalesReportData(
                    getSalesSummary(connection, filter),
                    Map.copyOf(getOrderStatusSummary(connection, filter)),
                    Map.copyOf(getPaymentMethodSummary(connection, filter)),
                    List.copyOf(getTopSellingProducts(connection, filter)),
                    List.copyOf(getSalesChartData(connection, filter)),
                    List.copyOf(getTransactionDetails(connection, filter)));
        }
    }

    public List<TransactionProductItem> getTransactionProducts(
            long transactionId) throws SQLException {
        String sql = "SELECT COALESCE(d.package_name,m.nama_menu) product_name,"
                + "d.qty,d.harga,d.subtotal FROM order_details d "
                + "JOIN menus m ON m.id_menu=d.id_menu "
                + "WHERE d.id_order=? ORDER BY d.id_detail_order";
        List<TransactionProductItem> products = new ArrayList<>();
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, transactionId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    products.add(new TransactionProductItem(
                            result.getString("product_name"),
                            result.getInt("qty"),
                            safeAmount(result.getBigDecimal("harga")),
                            safeAmount(result.getBigDecimal("subtotal"))));
                }
            }
        }
        return products;
    }

    private SalesSummary getSalesSummary(
            Connection connection, SalesFilter filter) throws SQLException {
        SqlFilter sqlFilter = buildFilter(filter);
        String sql = "SELECT COUNT(*) total_orders,"
                + "COALESCE(SUM(COALESCE(d.total_products,0)),0) total_products,"
                + "COALESCE(SUM(CASE WHEN o.status='selesai' "
                + "AND o.payment_status='berhasil' "
                + "THEN o.total_harga ELSE 0 END),0) total_revenue,"
                + "COALESCE(SUM(CASE WHEN o.status='selesai' "
                + "AND o.payment_status='berhasil' THEN 1 ELSE 0 END),0) "
                + "successful_orders "
                + "FROM orders o JOIN tables t ON t.id_meja=o.id_meja "
                + "LEFT JOIN (SELECT id_order,SUM(qty) total_products "
                + "FROM order_details GROUP BY id_order) d "
                + "ON d.id_order=o.id_order "
                + sqlFilter.clause();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, sqlFilter.parameters());
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                BigDecimal revenue = safeAmount(
                        result.getBigDecimal("total_revenue"));
                int successful = result.getInt("successful_orders");
                BigDecimal average = successful == 0
                        ? BigDecimal.ZERO
                        : revenue.divide(
                                BigDecimal.valueOf(successful),
                                0,
                                RoundingMode.HALF_UP);
                return new SalesSummary(
                        result.getInt("total_orders"),
                        result.getInt("total_products"),
                        revenue,
                        average);
            }
        }
    }

    private Map<String, Integer> getOrderStatusSummary(
            Connection connection, SalesFilter filter) throws SQLException {
        LinkedHashMap<String, Integer> values = new LinkedHashMap<>();
        STATUS_KEYS.forEach(key -> values.put(key, 0));
        SqlFilter sqlFilter = buildFilter(filter);
        String sql = "SELECT LOWER(o.status) status_name,COUNT(*) total "
                + "FROM orders o JOIN tables t ON t.id_meja=o.id_meja "
                + sqlFilter.clause()
                + " GROUP BY LOWER(o.status)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, sqlFilter.parameters());
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String key = result.getString("status_name");
                    if (values.containsKey(key)) {
                        values.put(key, result.getInt("total"));
                    }
                }
            }
        }
        return values;
    }

    private Map<String, Integer> getPaymentMethodSummary(
            Connection connection, SalesFilter filter) throws SQLException {
        LinkedHashMap<String, Integer> values = new LinkedHashMap<>();
        PAYMENT_LABELS.forEach(label -> values.put(label, 0));
        SqlFilter sqlFilter = buildFilter(filter);
        String sql = "SELECT LOWER(COALESCE(o.metode_pembayaran,'')) method,"
                + "COUNT(*) total FROM orders o "
                + "JOIN tables t ON t.id_meja=o.id_meja "
                + sqlFilter.clause()
                + " GROUP BY LOWER(COALESCE(o.metode_pembayaran,''))";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, sqlFilter.parameters());
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String label = paymentLabel(result.getString("method"));
                    values.put(label, values.get(label) + result.getInt("total"));
                }
            }
        }
        return values;
    }

    private List<TopSellingProduct> getTopSellingProducts(
            Connection connection, SalesFilter filter) throws SQLException {
        SqlFilter sqlFilter = buildFilter(filter);
        String sql = "SELECT m.id_menu,m.nama_menu,SUM(d.qty) quantity_sold,"
                + "SUM(d.subtotal) revenue "
                + "FROM orders o JOIN tables t ON t.id_meja=o.id_meja "
                + "JOIN order_details d ON d.id_order=o.id_order "
                + "JOIN menus m ON m.id_menu=d.id_menu "
                + sqlFilter.clause()
                + " AND o.status='selesai' AND o.payment_status='berhasil' "
                + "GROUP BY m.id_menu,m.nama_menu "
                + "ORDER BY quantity_sold DESC,m.nama_menu ASC LIMIT 5";
        List<TopSellingProduct> products = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, sqlFilter.parameters());
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    products.add(new TopSellingProduct(
                            result.getLong("id_menu"),
                            result.getString("nama_menu"),
                            result.getInt("quantity_sold"),
                            safeAmount(result.getBigDecimal("revenue"))));
                }
            }
        }
        return products;
    }

    private List<SalesChartItem> getSalesChartData(
            Connection connection, SalesFilter filter) throws SQLException {
        SqlFilter sqlFilter = buildFilter(filter);
        ChartGrouping grouping = chartGrouping(filter);
        String sql = "SELECT " + grouping.expression() + " chart_key,"
                + "COUNT(*) total_orders,"
                + "SUM(CASE WHEN o.status='selesai' "
                + "AND o.payment_status='berhasil' THEN 1 ELSE 0 END) completed,"
                + "COALESCE(SUM(COALESCE(d.total_products,0)),0) products,"
                + "COALESCE(SUM(CASE WHEN o.status='selesai' "
                + "AND o.payment_status='berhasil' "
                + "THEN o.total_harga ELSE 0 END),0) revenue "
                + "FROM orders o JOIN tables t ON t.id_meja=o.id_meja "
                + "LEFT JOIN (SELECT id_order,SUM(qty) total_products "
                + "FROM order_details GROUP BY id_order) d "
                + "ON d.id_order=o.id_order "
                + sqlFilter.clause()
                + " GROUP BY " + grouping.expression()
                + " ORDER BY " + grouping.expression();

        Map<String, SalesChartItem> queryValues = new LinkedHashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, sqlFilter.parameters());
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String key = result.getString("chart_key");
                    queryValues.put(key, new SalesChartItem(
                            grouping.displayLabel(key),
                            result.getInt("total_orders"),
                            result.getInt("completed"),
                            result.getInt("products"),
                            safeAmount(result.getBigDecimal("revenue"))));
                }
            }
        }

        List<SalesChartItem> values = new ArrayList<>();
        for (String key : grouping.keys()) {
            values.add(queryValues.getOrDefault(
                    key,
                    new SalesChartItem(
                            grouping.displayLabel(key),
                            0, 0, 0, BigDecimal.ZERO)));
        }
        return values;
    }

    private List<TransactionReportItem> getTransactionDetails(
            Connection connection, SalesFilter filter) throws SQLException {
        SqlFilter sqlFilter = buildFilter(filter);
        String sql = "SELECT o.id_order,o.kode_pesanan,o.created_at,"
                + "t.nama_meja customer_name,"
                + "COALESCE(SUM(d.qty),0) total_product,o.total_harga,"
                + "o.metode_pembayaran,o.status,o.notes "
                + "FROM orders o JOIN tables t ON t.id_meja=o.id_meja "
                + "LEFT JOIN order_details d ON d.id_order=o.id_order "
                + sqlFilter.clause()
                + " GROUP BY o.id_order,o.kode_pesanan,o.created_at,"
                + "t.nama_meja,o.total_harga,o.metode_pembayaran,o.status,o.notes "
                + "ORDER BY o.created_at DESC,o.id_order DESC";
        List<TransactionReportItem> transactions = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, sqlFilter.parameters());
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Timestamp timestamp = result.getTimestamp("created_at");
                    transactions.add(new TransactionReportItem(
                            result.getLong("id_order"),
                            result.getString("kode_pesanan"),
                            timestamp == null ? null : timestamp.toLocalDateTime(),
                            result.getString("customer_name"),
                            result.getInt("total_product"),
                            safeAmount(result.getBigDecimal("total_harga")),
                            paymentLabel(result.getString("metode_pembayaran")),
                            result.getString("status"),
                            result.getString("notes")));
                }
            }
        }
        return transactions;
    }

    private SqlFilter buildFilter(SalesFilter filter) {
        StringBuilder clause = new StringBuilder(
                " WHERE DATE(o.created_at) BETWEEN ? AND ?");
        List<Object> parameters = new ArrayList<>();
        parameters.add(filter.startDate());
        parameters.add(filter.endDate());

        if (filter.status() != null && !filter.status().isBlank()) {
            clause.append(" AND o.status=?");
            parameters.add(filter.status());
        }
        if (filter.paymentMethod() != null
                && !filter.paymentMethod().isBlank()) {
            clause.append(" AND o.metode_pembayaran=?");
            parameters.add(filter.paymentMethod());
        }
        if (!filter.keyword().isBlank()) {
            clause.append(" AND (LOWER(o.kode_pesanan) LIKE ? "
                    + "OR LOWER(t.nama_meja) LIKE ? "
                    + "OR CAST(o.id_order AS CHAR) LIKE ?)");
            String keyword = "%" + filter.keyword()
                    .toLowerCase(Locale.ROOT) + "%";
            parameters.add(keyword);
            parameters.add(keyword);
            parameters.add(keyword);
        }
        return new SqlFilter(clause.toString(), parameters);
    }

    private ChartGrouping chartGrouping(SalesFilter filter) {
        String period = filter.periodType();
        if ("Harian".equals(period)) {
            List<String> keys = new ArrayList<>();
            for (int hour = 0; hour < 24; hour++) {
                keys.add(String.format(Locale.ROOT, "%02d", hour));
            }
            return new ChartGrouping(
                    "LPAD(HOUR(o.created_at),2,'0')",
                    keys,
                    key -> key + ":00");
        }
        if ("Tahunan".equals(period)) {
            List<String> keys = new ArrayList<>();
            for (int month = 1; month <= 12; month++) {
                keys.add(String.valueOf(month));
            }
            return new ChartGrouping(
                    "MONTH(o.created_at)",
                    keys,
                    key -> java.time.Month.of(Integer.parseInt(key))
                            .getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.forLanguageTag("id-ID")));
        }

        List<String> keys = new ArrayList<>();
        LocalDate cursor = filter.startDate();
        while (!cursor.isAfter(filter.endDate())) {
            keys.add(cursor.toString());
            cursor = cursor.plusDays(1);
        }
        DateTimeFormatter display = DateTimeFormatter.ofPattern("dd/MM");
        return new ChartGrouping(
                "DATE(o.created_at)",
                keys,
                key -> LocalDate.parse(key).format(display));
    }

    private void bind(PreparedStatement statement, List<Object> parameters)
            throws SQLException {
        for (int index = 0; index < parameters.size(); index++) {
            Object value = parameters.get(index);
            if (value instanceof LocalDate date) {
                statement.setDate(index + 1, Date.valueOf(date));
            } else {
                statement.setObject(index + 1, value);
            }
        }
    }

    private String paymentLabel(String value) {
        String normalized = value == null
                ? "" : value.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "cash", "tunai" -> "Tunai";
            case "qris" -> "QRIS";
            case "gopay", "go_pay" -> "GoPay";
            case "dana" -> "DANA";
            case "ovo" -> "OVO";
            case "shopeepay", "shopee_pay" -> "ShopeePay";
            default -> "E-Wallet Lain";
        };
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, "root", "");
    }

    private static BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private record SqlFilter(String clause, List<Object> parameters) {
    }

    private record ChartGrouping(
            String expression,
            List<String> keys,
            java.util.function.Function<String, String> labelFunction) {

        String displayLabel(String key) {
            return labelFunction.apply(key);
        }
    }
}
