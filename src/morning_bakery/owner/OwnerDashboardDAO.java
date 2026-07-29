package morning_bakery.owner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Seluruh query agregasi untuk Dashboard Owner. */
public final class OwnerDashboardDAO {

    private static final String DB_URL
            = "jdbc:mysql://127.0.0.1:3306/swiftbite"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final List<String> ORDER_STATUSES = List.of(
            "menunggu",
            "diproses",
            "siap_diantar",
            "menunggu_pembayaran",
            "selesai",
            "dibatalkan");

    private static final List<String> PAYMENT_LABELS = List.of(
            "Tunai",
            "QRIS",
            "GoPay",
            "DANA",
            "OVO",
            "ShopeePay",
            "E-Wallet Lain");

    public OwnerDashboardData loadDashboardData() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate nextMonday = monday.plusWeeks(1);

        try (Connection connection = openConnection()) {
            Summary summary = loadSummary(connection, today);
            Map<String, Integer> statuses = loadOrderStatuses(connection, today);
            Map<DayOfWeek, BigDecimal> weeklyRevenue
                    = loadWeeklyRevenue(connection, monday, nextMonday);
            List<TopMenuItem> topMenus = loadTopMenus(connection, today);
            Map<String, Integer> payments = loadPaymentMethods(connection, today);

            String bestMenu = topMenus.isEmpty() ? "-" : topMenus.get(0).menuName();
            BigDecimal average = summary.completedTransactions() == 0
                    ? BigDecimal.ZERO
                    : summary.revenue().divide(
                            BigDecimal.valueOf(summary.completedTransactions()),
                            0,
                            RoundingMode.HALF_UP);

            return new OwnerDashboardData(
                    summary.revenue(),
                    summary.orderCount(),
                    average,
                    bestMenu,
                    Map.copyOf(statuses),
                    Map.copyOf(weeklyRevenue),
                    List.copyOf(topMenus),
                    Map.copyOf(payments));
        }
    }

    private Summary loadSummary(Connection connection, LocalDate date)
            throws SQLException {
        String sql = "SELECT COUNT(*) AS order_count,"
                + "COALESCE(SUM(CASE WHEN status='selesai' "
                + "AND payment_status='berhasil' THEN total_harga ELSE 0 END),0) revenue,"
                + "COALESCE(SUM(CASE WHEN status='selesai' "
                + "AND payment_status='berhasil' THEN 1 ELSE 0 END),0) completed_count "
                + "FROM orders WHERE DATE(created_at)=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(date));
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return new Summary(
                        safeAmount(result.getBigDecimal("revenue")),
                        result.getInt("order_count"),
                        result.getInt("completed_count"));
            }
        }
    }

    private Map<String, Integer> loadOrderStatuses(
            Connection connection, LocalDate date) throws SQLException {
        LinkedHashMap<String, Integer> values = new LinkedHashMap<>();
        ORDER_STATUSES.forEach(status -> values.put(status, 0));

        String sql = "SELECT LOWER(status) status_name,COUNT(*) total "
                + "FROM orders WHERE DATE(created_at)=? GROUP BY LOWER(status)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(date));
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String status = result.getString("status_name");
                    if (values.containsKey(status)) {
                        values.put(status, result.getInt("total"));
                    }
                }
            }
        }
        return values;
    }

    private Map<DayOfWeek, BigDecimal> loadWeeklyRevenue(
            Connection connection, LocalDate start, LocalDate endExclusive)
            throws SQLException {
        EnumMap<DayOfWeek, BigDecimal> values = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            values.put(day, BigDecimal.ZERO);
        }

        String sql = "SELECT DATE(created_at) order_date,"
                + "COALESCE(SUM(total_harga),0) revenue "
                + "FROM orders WHERE created_at>=? AND created_at<? "
                + "AND status='selesai' AND payment_status='berhasil' "
                + "GROUP BY DATE(created_at)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(start));
            statement.setDate(2, Date.valueOf(endExclusive));
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    LocalDate orderDate = result.getDate("order_date").toLocalDate();
                    values.put(orderDate.getDayOfWeek(),
                            safeAmount(result.getBigDecimal("revenue")));
                }
            }
        }
        return values;
    }

    private List<TopMenuItem> loadTopMenus(
            Connection connection, LocalDate date) throws SQLException {
        List<TopMenuItem> values = new ArrayList<>();
        String sql = "SELECT m.nama_menu,SUM(d.qty) total_sold "
                + "FROM order_details d "
                + "JOIN menus m ON m.id_menu=d.id_menu "
                + "JOIN orders o ON o.id_order=d.id_order "
                + "WHERE DATE(o.created_at)=? AND o.status='selesai' "
                + "AND o.payment_status='berhasil' "
                + "GROUP BY m.id_menu,m.nama_menu "
                + "ORDER BY total_sold DESC,m.nama_menu ASC LIMIT 3";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(date));
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    values.add(new TopMenuItem(
                            result.getString("nama_menu"),
                            result.getInt("total_sold")));
                }
            }
        }
        return values;
    }

    private Map<String, Integer> loadPaymentMethods(
            Connection connection, LocalDate date) throws SQLException {
        LinkedHashMap<String, Integer> values = new LinkedHashMap<>();
        PAYMENT_LABELS.forEach(label -> values.put(label, 0));

        String sql = "SELECT LOWER(COALESCE(metode_pembayaran,'')) payment_method,"
                + "COUNT(*) total FROM orders "
                + "WHERE DATE(created_at)=? AND status='selesai' "
                + "AND payment_status='berhasil' "
                + "GROUP BY LOWER(COALESCE(metode_pembayaran,''))";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(date));
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String label = paymentLabel(result.getString("payment_method"));
                    values.put(label, values.get(label) + result.getInt("total"));
                }
            }
        }
        return values;
    }

    private String paymentLabel(String databaseValue) {
        String value = databaseValue == null
                ? "" : databaseValue.trim().toLowerCase(Locale.ROOT);
        return switch (value) {
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
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private record Summary(
            BigDecimal revenue,
            int orderCount,
            int completedTransactions) {
    }
}
