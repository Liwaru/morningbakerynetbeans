package morning_bakery;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class riwayat_pesanan extends JFrame {

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/swiftbite"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final Color CREAM = new Color(252, 248, 241);
    private static final Color BROWN = new Color(91, 48, 29);
    private static final Color BROWN_DARK = new Color(58, 28, 19);
    private static final Color BROWN_MID = new Color(126, 78, 46);
    private static final Color BROWN_LIGHT = new Color(164, 104, 67);
    private static final Color WHITE = Color.WHITE;

    private final long cashierId;
    private final String cashierName;
    private final NumberFormat rupiah = NumberFormat.getIntegerInstance(Locale.forLanguageTag("id-ID"));
    private final DefaultTableModel historyModel = new DefaultTableModel(
            new String[]{"KODE PESANAN", "MEJA", "ITEM", "PEMBAYARAN", "STATUS", "TOTAL", "WAKTU"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> paymentFilter;
    private JComboBox<String> dateFilter;
    private JTable historyTable;
    private int historyRequestSequence;

    public riwayat_pesanan() {
        this(2L, "cashier");
    }

    public riwayat_pesanan(long cashierId, String cashierName) {
        this.cashierId = cashierId;
        this.cashierName = cashierName == null || cashierName.isBlank() ? "Kasir" : cashierName;
        initComponents();
        configurePage();
        loadHistory();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        designRootPanel = new javax.swing.JPanel();
        designSidebarPanel = new javax.swing.JPanel();
        designLogoLabel = new javax.swing.JLabel();
        designBrandLabel = new javax.swing.JLabel();
        designOrdersButton = new javax.swing.JButton();
        designHistoryButton = new javax.swing.JButton();
        designAccountPanel = new javax.swing.JPanel();
        designInitialLabel = new javax.swing.JLabel();
        designNameLabel = new javax.swing.JLabel();
        designRoleLabel = new javax.swing.JLabel();
        designContentPanel = new javax.swing.JPanel();
        designPageTitle = new javax.swing.JLabel();
        designCardPanel = new javax.swing.JPanel();
        designCardTitle = new javax.swing.JLabel();
        designSubtitle = new javax.swing.JLabel();
        designSearchField = new javax.swing.JTextField();
        designStatusCombo = new javax.swing.JComboBox<>();
        designPaymentCombo = new javax.swing.JComboBox<>();
        designDateCombo = new javax.swing.JComboBox<>();
        designApplyButton = new javax.swing.JButton();
        designResetButton = new javax.swing.JButton();
        designScrollPane = new javax.swing.JScrollPane();
        designTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SwiftBite - Riwayat Transaksi");
        setMinimumSize(new java.awt.Dimension(1100, 680));

        designRootPanel.setBackground(new Color(248, 248, 247));
        designRootPanel.setPreferredSize(new Dimension(1366, 720));
        designSidebarPanel.setBackground(BROWN);
        designSidebarPanel.setPreferredSize(new Dimension(250, 720));
        designLogoLabel.setBackground(WHITE);
        designLogoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        designLogoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        designLogoLabel.setText("S");
        designLogoLabel.setOpaque(true);
        designBrandLabel.setFont(new Font("Segoe UI", Font.BOLD, 23));
        designBrandLabel.setForeground(WHITE);
        designBrandLabel.setText("SwiftBite");
        designOrdersButton.setText("Pesanan");
        designHistoryButton.setText("Riwayat Transaksi");
        designAccountPanel.setBackground(new Color(74, 43, 32));
        designInitialLabel.setForeground(WHITE);
        designInitialLabel.setHorizontalAlignment(SwingConstants.CENTER);
        designInitialLabel.setText("C");
        designNameLabel.setForeground(WHITE);
        designNameLabel.setText("cashier");
        designRoleLabel.setForeground(new Color(242, 218, 197));
        designRoleLabel.setText("Kasir");

        javax.swing.GroupLayout accountLayout = new javax.swing.GroupLayout(designAccountPanel);
        designAccountPanel.setLayout(accountLayout);
        accountLayout.setHorizontalGroup(accountLayout.createSequentialGroup()
                .addGap(12).addComponent(designInitialLabel, 38, 38, 38).addGap(10)
                .addGroup(accountLayout.createParallelGroup().addComponent(designNameLabel).addComponent(designRoleLabel))
                .addContainerGap(70, Short.MAX_VALUE));
        accountLayout.setVerticalGroup(accountLayout.createSequentialGroup().addGap(12)
                .addGroup(accountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(designInitialLabel, 38, 38, 38)
                        .addGroup(accountLayout.createSequentialGroup().addComponent(designNameLabel).addComponent(designRoleLabel)))
                .addContainerGap(12, Short.MAX_VALUE));

        javax.swing.GroupLayout sideLayout = new javax.swing.GroupLayout(designSidebarPanel);
        designSidebarPanel.setLayout(sideLayout);
        sideLayout.setHorizontalGroup(sideLayout.createSequentialGroup().addGap(18)
                .addGroup(sideLayout.createParallelGroup()
                        .addGroup(sideLayout.createSequentialGroup().addComponent(designLogoLabel, 48, 48, 48).addGap(12).addComponent(designBrandLabel))
                        .addComponent(designOrdersButton, 214, 214, 214)
                        .addComponent(designHistoryButton, 214, 214, 214)
                        .addComponent(designAccountPanel, 214, 214, 214)).addGap(18));
        sideLayout.setVerticalGroup(sideLayout.createSequentialGroup().addGap(26)
                .addGroup(sideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(designLogoLabel, 48, 48, 48).addComponent(designBrandLabel))
                .addGap(42).addComponent(designOrdersButton, 52, 52, 52).addGap(10)
                .addComponent(designHistoryButton, 52, 52, 52).addGap(370, 370, Short.MAX_VALUE)
                .addComponent(designAccountPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28));

        designContentPanel.setBackground(new Color(248, 248, 247));
        designPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 38));
        designPageTitle.setText("Riwayat Transaksi");
        designCardPanel.setBackground(BROWN_MID);
        designCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        designCardTitle.setForeground(WHITE);
        designCardTitle.setText("Riwayat Transaksi");
        designSubtitle.setForeground(WHITE);
        designSubtitle.setText("Daftar pesanan yang sudah selesai atau dibatalkan.");
        designSearchField.setText("Cari kode pesanan, menu, atau meja...");
        designStatusCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Semua Status", "Selesai", "Dibatalkan"}));
        designPaymentCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Semua Pembayaran", "Tunai", "QRIS", "E-Wallet"}));
        designDateCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Semua Waktu", "Hari Ini", "7 Hari Terakhir", "30 Hari Terakhir"}));
        designApplyButton.setText("Terapkan");
        designResetButton.setText("Reset");
        designTable.setModel(new DefaultTableModel(new Object[][]{
            {"ORD-202607220001", "Kasir Langsung", "1x Butter Croissant", "TUNAI", "Selesai", "Rp18.000", "22 Jul 2026 20:10"},
            {"ORD-202607220002", "Meja 2", "2x Cafe Latte", "QRIS", "Selesai", "Rp44.000", "22 Jul 2026 19:42"},
            {"ORD-202607220003", "Meja 4", "1x Americano", "TUNAI", "Dibatalkan", "Rp0", "22 Jul 2026 18:15"}
        }, new String[]{"PESANAN", "MEJA", "ITEM", "PEMBAYARAN", "STATUS", "TOTAL", "WAKTU"}));
        designScrollPane.setViewportView(designTable);

        javax.swing.GroupLayout cardLayout = new javax.swing.GroupLayout(designCardPanel);
        designCardPanel.setLayout(cardLayout);
        cardLayout.setHorizontalGroup(cardLayout.createSequentialGroup().addGap(18)
                .addGroup(cardLayout.createParallelGroup()
                        .addComponent(designCardTitle).addComponent(designSubtitle)
                        .addGroup(cardLayout.createSequentialGroup()
                                .addComponent(designSearchField, 260, 260, Short.MAX_VALUE).addGap(10)
                                .addComponent(designStatusCombo, 150, 150, 150).addGap(10)
                                .addComponent(designPaymentCombo, 160, 160, 160).addGap(10)
                                .addComponent(designDateCombo, 160, 160, 160).addGap(10)
                                .addComponent(designApplyButton, 94, 94, 94).addGap(8)
                                .addComponent(designResetButton, 74, 74, 74))
                        .addComponent(designScrollPane)).addGap(18));
        cardLayout.setVerticalGroup(cardLayout.createSequentialGroup().addGap(22)
                .addComponent(designCardTitle).addGap(4).addComponent(designSubtitle).addGap(18)
                .addGroup(cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(designSearchField, 44, 44, 44).addComponent(designStatusCombo, 44, 44, 44)
                        .addComponent(designPaymentCombo, 44, 44, 44).addComponent(designDateCombo, 44, 44, 44)
                        .addComponent(designApplyButton, 44, 44, 44).addComponent(designResetButton, 44, 44, 44))
                .addGap(16).addComponent(designScrollPane, 260, 32767, Short.MAX_VALUE).addGap(18));

        javax.swing.GroupLayout contentLayout = new javax.swing.GroupLayout(designContentPanel);
        designContentPanel.setLayout(contentLayout);
        contentLayout.setHorizontalGroup(contentLayout.createSequentialGroup().addGap(30)
                .addGroup(contentLayout.createParallelGroup().addComponent(designPageTitle).addComponent(designCardPanel))
                .addGap(30));
        contentLayout.setVerticalGroup(contentLayout.createSequentialGroup().addGap(32)
                .addComponent(designPageTitle).addGap(22).addComponent(designCardPanel).addGap(36));

        javax.swing.GroupLayout rootLayout = new javax.swing.GroupLayout(designRootPanel);
        designRootPanel.setLayout(rootLayout);
        rootLayout.setHorizontalGroup(rootLayout.createSequentialGroup().addComponent(designSidebarPanel, 250, 250, 250).addComponent(designContentPanel));
        rootLayout.setVerticalGroup(rootLayout.createParallelGroup().addComponent(designSidebarPanel).addComponent(designContentPanel));
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(designRootPanel));
        layout.setVerticalGroup(layout.createParallelGroup().addComponent(designRootPanel));
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void configurePage() {
        setSize(1366, 760);
        setLocationRelativeTo(null);
        java.net.URL logoUrl = getClass().getResource("/morning_bakery/assets/Swiftbite.png");
        if (logoUrl != null) {
            setIconImage(new ImageIcon(logoUrl).getImage());
        }
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(248, 248, 247));
        root.add(createSidebar(logoUrl), BorderLayout.WEST);
        root.add(createContent(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel createSidebar(java.net.URL logoUrl) {
        GradientPanel sidebar = new GradientPanel(new BorderLayout(), BROWN_LIGHT, BROWN_DARK);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(26, 18, 28, 18));
        JPanel brand = transparent(new FlowLayout(FlowLayout.LEFT, 12, 0));
        JLabel logo = new JLabel("S", SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(48, 48));
        logo.setOpaque(true);
        logo.setBackground(WHITE);
        logo.setForeground(BROWN);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        if (logoUrl != null) {
            Image image = new ImageIcon(logoUrl).getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);
            logo.setText("");
            logo.setIcon(new ImageIcon(image));
        }
        JLabel title = new JLabel("SwiftBite");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        brand.add(logo);
        brand.add(title);
        MouseAdapter dashboardLink = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) { openDashboard(); }
        };
        logo.addMouseListener(dashboardLink);
        title.addMouseListener(dashboardLink);
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        title.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel menu = transparent();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 55)));
        menu.add(Box.createVerticalStrut(22));
        JButton orders = navButton("Pesanan", false);
        orders.addActionListener(event -> { new Pesanan(cashierId, cashierName).setVisible(true); dispose(); });
        JButton history = navButton("Riwayat Transaksi", true);
        menu.add(orders);
        menu.add(Box.createVerticalStrut(10));
        menu.add(history);
        menu.add(Box.createVerticalGlue());

        JPanel account = new JPanel(new BorderLayout(10, 0));
        account.setBackground(new Color(74, 43, 32));
        account.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel initial = new JLabel(cashierName.substring(0, 1).toUpperCase(Locale.ROOT), SwingConstants.CENTER);
        initial.setPreferredSize(new Dimension(38, 38));
        initial.setOpaque(true);
        initial.setBackground(new Color(116, 82, 65));
        initial.setForeground(WHITE);
        initial.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JPanel accountText = transparent();
        accountText.setLayout(new BoxLayout(accountText, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(cashierName);
        name.setForeground(WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel role = new JLabel("Kasir");
        role.setForeground(new Color(242, 218, 197));
        accountText.add(name);
        accountText.add(role);
        account.add(initial, BorderLayout.WEST);
        account.add(accountText, BorderLayout.CENTER);
        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(menu, BorderLayout.CENTER);
        sidebar.add(account, BorderLayout.SOUTH);
        return sidebar;
    }

    private JScrollPane createContent() {
        JPanel content = new JPanel();
        content.setBackground(new Color(248, 248, 247));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(32, 30, 36, 30));
        JLabel pageTitle = new JLabel("Riwayat Transaksi");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 38));
        pageTitle.setForeground(new Color(29, 21, 18));
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(pageTitle);
        content.add(Box.createVerticalStrut(22));
        content.add(createHistoryCard());
        content.add(Box.createVerticalGlue());
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel createHistoryCard() {
        GradientPanel card = new GradientPanel(new BorderLayout(0, 16), BROWN_LIGHT, BROWN_DARK);
        card.setBorder(BorderFactory.createEmptyBorder(22, 18, 18, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 560));
        JPanel header = transparent();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Riwayat Transaksi");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitle = new JLabel("Daftar pesanan yang sudah selesai atau dibatalkan.");
        subtitle.setForeground(new Color(255, 233, 214));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        header.add(Box.createVerticalStrut(18));
        header.add(createFilters());

        historyTable = new JTable(historyModel);
        configureTable();
        JScrollPane tableScroll = new JScrollPane(historyTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(182, 137, 109)));
        tableScroll.getViewport().setBackground(new Color(117, 70, 43));
        card.add(header, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFilters() {
        JPanel filters = transparent(new java.awt.GridBagLayout());
        filters.setAlignmentX(Component.LEFT_ALIGNMENT);
        filters.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.gridy = 0;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        c.insets = new java.awt.Insets(0, 0, 0, 10);
        searchField = new JTextField();
        searchField.setToolTipText("Cari kode pesanan, menu, atau meja");
        searchField.setPreferredSize(new Dimension(270, 44));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.gridx = 0; c.weightx = 1; filters.add(searchField, c);
        statusFilter = combo(new String[]{"Semua Status", "Selesai", "Dibatalkan"}, 150);
        c.gridx = 1; c.weightx = 0; filters.add(statusFilter, c);
        paymentFilter = combo(new String[]{"Semua Pembayaran", "Tunai", "QRIS", "E-Wallet"}, 160);
        c.gridx = 2; filters.add(paymentFilter, c);
        dateFilter = combo(new String[]{"Semua Waktu", "Hari Ini", "7 Hari Terakhir", "30 Hari Terakhir"}, 160);
        c.gridx = 3; filters.add(dateFilter, c);
        JButton apply = lightButton("Terapkan", 94);
        apply.addActionListener(event -> loadHistory());
        c.gridx = 4; filters.add(apply, c);
        JButton reset = lightButton("Reset", 74);
        reset.setBackground(new Color(117, 82, 65));
        reset.setForeground(WHITE);
        reset.addActionListener(event -> resetFilters());
        c.gridx = 5; c.insets = new java.awt.Insets(0, 0, 0, 0); filters.add(reset, c);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { loadHistory(); }
            @Override public void removeUpdate(DocumentEvent event) { loadHistory(); }
            @Override public void changedUpdate(DocumentEvent event) { loadHistory(); }
        });
        statusFilter.addActionListener(event -> loadHistory());
        paymentFilter.addActionListener(event -> loadHistory());
        dateFilter.addActionListener(event -> loadHistory());
        return filters;
    }

    private void configureTable() {
        historyTable.setRowHeight(64);
        historyTable.setShowGrid(false);
        historyTable.setIntercellSpacing(new Dimension(0, 0));
        historyTable.setBackground(new Color(117, 70, 43));
        historyTable.setForeground(WHITE);
        historyTable.setSelectionBackground(new Color(177, 114, 73));
        historyTable.setSelectionForeground(WHITE);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyTable.getTableHeader().setPreferredSize(new Dimension(0, 42));
        historyTable.getTableHeader().setBackground(new Color(139, 100, 78));
        historyTable.getTableHeader().setForeground(WHITE);
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        historyTable.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        renderer.setOpaque(true);
        historyTable.setDefaultRenderer(Object.class, renderer);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(190);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(270);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(150);
    }

    private void loadHistory() {
        if (searchField == null) return;
        String keyword = searchField.getText().trim();
        String status = String.valueOf(statusFilter.getSelectedItem());
        String payment = String.valueOf(paymentFilter.getSelectedItem());
        String period = String.valueOf(dateFilter.getSelectedItem());
        int requestSequence = ++historyRequestSequence;
        new SwingWorker<List<Object[]>, Void>() {
            @Override protected List<Object[]> doInBackground() throws Exception {
                StringBuilder sql = new StringBuilder("SELECT o.kode_pesanan,t.nama_meja,"
                        + "GROUP_CONCAT(CONCAT(d.qty,'x ',COALESCE(d.package_name,m.nama_menu)) ORDER BY d.id_detail_order SEPARATOR ', ') items,"
                        + "o.metode_pembayaran,o.status,o.total_harga,o.created_at FROM orders o "
                        + "JOIN tables t ON t.id_meja=o.id_meja LEFT JOIN order_details d ON d.id_order=o.id_order "
                        + "LEFT JOIN menus m ON m.id_menu=d.id_menu WHERE o.status IN ('selesai','dibatalkan') "
                        + "AND (?='' OR o.kode_pesanan LIKE CONCAT('%',?,'%') OR t.nama_meja LIKE CONCAT('%',?,'%') "
                        + "OR m.nama_menu LIKE CONCAT('%',?,'%')) ");
                List<String> params = new ArrayList<>();
                if (!status.equals("Semua Status")) {
                    sql.append("AND o.status=? ");
                    params.add(status.equals("Selesai") ? "selesai" : "dibatalkan");
                }
                if (!payment.equals("Semua Pembayaran")) {
                    sql.append("AND o.metode_pembayaran=? ");
                    params.add(payment.equals("Tunai") ? "cash" : payment.equals("QRIS") ? "qris" : "ewallet");
                }
                if (period.equals("Hari Ini")) sql.append("AND DATE(o.created_at)=CURDATE() ");
                else if (period.equals("7 Hari Terakhir")) sql.append("AND o.created_at>=NOW()-INTERVAL 7 DAY ");
                else if (period.equals("30 Hari Terakhir")) sql.append("AND o.created_at>=NOW()-INTERVAL 30 DAY ");
                sql.append("GROUP BY o.id_order ORDER BY o.created_at DESC");
                List<Object[]> rows = new ArrayList<>();
                try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                        PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                    statement.setString(1, keyword);
                    statement.setString(2, keyword);
                    statement.setString(3, keyword);
                    statement.setString(4, keyword);
                    int index = 5;
                    for (String param : params) statement.setString(index++, param);
                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            rows.add(new Object[]{result.getString("kode_pesanan"), result.getString("nama_meja"),
                                result.getString("items") == null ? "-" : result.getString("items"),
                                readablePayment(result.getString("metode_pembayaran")), readable(result.getString("status")),
                                formatRupiah(result.getBigDecimal("total_harga")),
                                result.getTimestamp("created_at").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.forLanguageTag("id-ID")))});
                        }
                    }
                }
                return rows;
            }
            @Override protected void done() {
                if (requestSequence != historyRequestSequence) return;
                try {
                    historyModel.setRowCount(0);
                    for (Object[] row : get()) historyModel.addRow(row);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(riwayat_pesanan.this, "Riwayat transaksi tidak dapat dimuat.\nPeriksa koneksi database.", "Database", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void resetFilters() {
        searchField.setText("");
        statusFilter.setSelectedIndex(0);
        paymentFilter.setSelectedIndex(0);
        dateFilter.setSelectedIndex(0);
    }

    private JButton navButton(String text, boolean active) {
        JButton button = new JButton(text);
        Color normal = active ? BROWN_LIGHT : BROWN_MID;
        button.setUI(new BasicButtonUI());
        button.setBackground(normal);
        button.setForeground(WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        button.setPreferredSize(new Dimension(214, 56));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent event) { button.setBackground(new Color(151, 93, 58)); }
            @Override public void mouseExited(MouseEvent event) { button.setBackground(normal); }
        });
        return button;
    }

    private JButton lightButton(String text, int width) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setPreferredSize(new Dimension(width, 44));
        button.setBackground(CREAM);
        button.setForeground(BROWN_DARK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JComboBox<String> combo(String[] values, int width) {
        JComboBox<String> combo = new JComboBox<>(values);
        combo.setPreferredSize(new Dimension(width, 44));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return combo;
    }

    private JPanel transparent() { return transparent(new FlowLayout(FlowLayout.LEFT, 0, 0)); }
    private JPanel transparent(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }
    private String formatRupiah(BigDecimal value) { return "Rp" + rupiah.format(value == null ? BigDecimal.ZERO : value); }
    private String readablePayment(String value) { return "cash".equals(value) ? "TUNAI" : value == null ? "-" : value.toUpperCase(Locale.ROOT); }
    private String readable(String value) {
        if (value == null || value.isBlank()) return "-";
        String[] words = value.replace('_', ' ').split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) result.append(result.isEmpty() ? "" : " ").append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        return result.toString();
    }
    private void openDashboard() { new DashboardKasir(cashierId, cashierName).setVisible(true); dispose(); }

    private static final class GradientPanel extends JPanel {
        private final Color start;
        private final Color end;
        GradientPanel(java.awt.LayoutManager layout, Color start, Color end) { super(layout); this.start = start; this.end = end; setOpaque(false); }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new riwayat_pesanan().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton designApplyButton;
    private javax.swing.JPanel designAccountPanel;
    private javax.swing.JLabel designBrandLabel;
    private javax.swing.JPanel designCardPanel;
    private javax.swing.JLabel designCardTitle;
    private javax.swing.JPanel designContentPanel;
    private javax.swing.JComboBox<String> designDateCombo;
    private javax.swing.JButton designHistoryButton;
    private javax.swing.JLabel designInitialLabel;
    private javax.swing.JLabel designLogoLabel;
    private javax.swing.JLabel designNameLabel;
    private javax.swing.JButton designOrdersButton;
    private javax.swing.JLabel designPageTitle;
    private javax.swing.JComboBox<String> designPaymentCombo;
    private javax.swing.JButton designResetButton;
    private javax.swing.JLabel designRoleLabel;
    private javax.swing.JPanel designRootPanel;
    private javax.swing.JScrollPane designScrollPane;
    private javax.swing.JTextField designSearchField;
    private javax.swing.JPanel designSidebarPanel;
    private javax.swing.JComboBox<String> designStatusCombo;
    private javax.swing.JLabel designSubtitle;
    private javax.swing.JTable designTable;
    // End of variables declaration//GEN-END:variables
}
