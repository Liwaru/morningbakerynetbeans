package morning_bakery.owner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Halaman Gaji Owner yang digunakan ulang melalui CardLayout. */
public final class OwnerSalaryPanel extends JPanel {

    private static final String DB_URL
            = "jdbc:mysql://127.0.0.1:3306/swiftbite"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final Color PAGE = new Color(249, 247, 244);
    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color BROWN = new Color(91, 48, 29);
    private static final Color BROWN_LIGHT = new Color(150, 92, 54);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"KARYAWAN", "PERIODE", "GAJI POKOK", "BONUS",
                "POTONGAN", "TOTAL GAJI", "STATUS"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DecimalFormat amountFormat;
    private boolean loading;

    public OwnerSalaryPanel() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(
                Locale.forLanguageTag("id-ID"));
        symbols.setGroupingSeparator('.');
        amountFormat = new DecimalFormat("#,##0", symbols);
        initUI();
        refreshData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 16));
        setBackground(PAGE);
        setBorder(BorderFactory.createEmptyBorder(32, 30, 34, 30));

        RoundedGradientPanel header = new RoundedGradientPanel(
                new BorderLayout(),
                BROWN_LIGHT,
                new Color(56, 27, 19),
                14);
        header.setPreferredSize(new Dimension(0, 150));
        header.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        JPanel headerText = new JPanel(new BorderLayout(0, 5));
        headerText.setOpaque(false);
        JLabel eyebrow = new JLabel("OWNER PAYROLL");
        eyebrow.setForeground(new Color(245, 218, 195));
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel title = new JLabel("Data Gaji");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 39));
        JLabel description = new JLabel(
                "Pantau riwayat gaji, bonus, potongan, dan status pembayaran karyawan.");
        description.setForeground(new Color(255, 235, 218));
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        headerText.add(eyebrow, BorderLayout.NORTH);
        headerText.add(title, BorderLayout.CENTER);
        headerText.add(description, BorderLayout.SOUTH);
        header.add(headerText, BorderLayout.CENTER);

        RoundedGradientPanel tableCard = new RoundedGradientPanel(
                new BorderLayout(0, 14),
                new Color(137, 82, 49),
                new Color(67, 31, 22),
                12);
        tableCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JLabel tableTitle = new JLabel("Riwayat Gaji Karyawan");
        tableTitle.setForeground(WHITE);
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));

        JTable table = new JTable(model);
        table.setRowHeight(48);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setBackground(BROWN);
        table.setForeground(WHITE);
        table.setGridColor(new Color(255, 255, 255, 38));
        table.setSelectionBackground(new Color(155, 94, 58));
        table.setSelectionForeground(WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 44));
        table.getTableHeader().setBackground(new Color(161, 105, 72));
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        center.setBackground(BROWN);
        center.setForeground(WHITE);
        for (int column = 1; column < table.getColumnCount(); column++) {
            table.getColumnModel().getColumn(column).setCellRenderer(center);
        }

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroll.getViewport().setBackground(BROWN);
        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(tableScroll, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    public void refreshData() {
        if (loading) {
            return;
        }
        loading = true;
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> rows = new ArrayList<>();
                String sql = "SELECT u.name,s.periode,s.gaji_pokok,s.bonus,"
                        + "s.potongan,s.total_gaji,s.status "
                        + "FROM salaries s JOIN users u ON u.id_user=s.id_user "
                        + "ORDER BY s.periode DESC,u.name ASC";
                try (Connection connection = DriverManager.getConnection(
                        DB_URL, "root", "");
                        PreparedStatement statement = connection.prepareStatement(sql);
                        ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        rows.add(new Object[]{
                            result.getString("name"),
                            result.getString("periode"),
                            formatRupiah(result.getBigDecimal("gaji_pokok")),
                            formatRupiah(result.getBigDecimal("bonus")),
                            formatRupiah(result.getBigDecimal("potongan")),
                            formatRupiah(result.getBigDecimal("total_gaji")),
                            readableStatus(result.getString("status"))
                        });
                    }
                }
                return rows;
            }

            @Override
            protected void done() {
                loading = false;
                try {
                    List<Object[]> rows = get();
                    model.setRowCount(0);
                    rows.forEach(model::addRow);
                } catch (Exception exception) {
                    if (isShowing()) {
                        JOptionPane.showMessageDialog(
                                OwnerSalaryPanel.this,
                                "Data gaji tidak dapat dimuat.",
                                "Koneksi Database",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    private String formatRupiah(BigDecimal value) {
        BigDecimal safe = value == null ? BigDecimal.ZERO : value;
        return "Rp" + amountFormat.format(safe.longValue());
    }

    private String readableStatus(String status) {
        return "dibayar".equalsIgnoreCase(status)
                ? "Dibayar" : "Belum Dibayar";
    }
}
