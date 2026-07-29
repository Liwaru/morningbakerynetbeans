package morning_bakery.sales;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import morning_bakery.owner.RoundedGradientPanel;

/** Dialog modal rincian produk pada sebuah transaksi. */
public final class TransactionDetailDialog {

    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color MUTED = new Color(241, 211, 190);
    private static final Color BROWN = new Color(91, 48, 29);

    private TransactionDetailDialog() {
    }

    public static void show(
            Window owner,
            TransactionReportItem transaction,
            List<TransactionProductItem> products) {
        JDialog dialog = new JDialog(
                owner, "Detail Transaksi", JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        RoundedGradientPanel root = new RoundedGradientPanel(
                new BorderLayout(0, 14),
                new Color(145, 87, 51),
                new Color(60, 29, 20),
                12);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel header = transparent(new BorderLayout());
        JLabel title = new JLabel("Detail Transaksi");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        JButton close = new JButton("Tutup");
        close.setBackground(new Color(255, 247, 235));
        close.setForeground(new Color(58, 28, 19));
        close.setFont(new Font("Segoe UI", Font.BOLD, 12));
        close.setFocusPainted(false);
        close.addActionListener(event -> dialog.dispose());
        header.add(title, BorderLayout.WEST);
        header.add(close, BorderLayout.EAST);

        JPanel info = transparent(new GridLayout(4, 2, 14, 7));
        addInfo(info, "Kode Transaksi", transaction.transactionCode());
        addInfo(info, "Tanggal", transaction.transactionDate() == null
                ? "-" : transaction.transactionDate().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        addInfo(info, "Pelanggan / Meja", transaction.customerName());
        addInfo(info, "Metode Pembayaran", transaction.paymentMethod());
        addInfo(info, "Status",
                SalesReportExportService.readableStatus(transaction.status()));
        addInfo(info, "Total Produk",
                String.valueOf(transaction.totalProduct()));
        addInfo(info, "Total Pembayaran",
                SalesReportExportService.formatRupiah(
                        transaction.totalAmount()));
        addInfo(info, "Keterangan",
                transaction.notes() == null || transaction.notes().isBlank()
                        ? "-" : transaction.notes());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"PRODUK", "QTY", "HARGA", "SUBTOTAL"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (TransactionProductItem product : products) {
            model.addRow(new Object[]{
                product.productName(),
                product.quantity(),
                SalesReportExportService.formatRupiah(product.unitPrice()),
                SalesReportExportService.formatRupiah(product.subtotal())
            });
        }
        JTable table = new JTable(model);
        table.setRowHeight(38);
        table.setBackground(BROWN);
        table.setForeground(WHITE);
        table.setGridColor(new Color(255, 255, 255, 40));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setBackground(new Color(161, 105, 72));
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 11));
        table.getColumnModel().getColumn(1).setMaxWidth(60);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(
                new Color(255, 255, 255, 45)));
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(BROWN);

        JPanel center = transparent(new BorderLayout(0, 14));
        center.add(info, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.setSize(700, 530);
        dialog.setMinimumSize(new Dimension(620, 460));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private static void addInfo(
            JPanel panel, String labelText, String valueText) {
        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel value = new JLabel(
                valueText == null ? "-" : valueText,
                SwingConstants.RIGHT);
        value.setForeground(WHITE);
        value.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(label);
        panel.add(value);
    }

    private static JPanel transparent(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }
}
