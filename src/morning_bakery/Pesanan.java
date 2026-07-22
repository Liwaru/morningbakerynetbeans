package morning_bakery;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Pesanan extends JFrame {

    private static final String DB_URL
            = "jdbc:mysql://127.0.0.1:3306/swiftbite"
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
    private final DefaultTableModel ordersModel = readOnlyModel(
            new String[]{"ID", "KODE PESANAN", "MEJA", "STATUS", "PEMBAYARAN", "TOTAL", "WAKTU"});
    private final DefaultTableModel menusModel = readOnlyModel(
            new String[]{"ID", "MENU", "KATEGORI", "STOK", "HARGA_RAW", "HARGA", "PILIH"});
    private final DefaultTableModel cartModel = readOnlyModel(
            new String[]{"ID", "MENU", "QTY", "SUBTOTAL"});
    private final Map<Long, CartItem> cartItems = new LinkedHashMap<>();
    private final Timer refreshTimer;
    private boolean barcodeMode;
    private int menuSearchSequence;
    private boolean scannerUpdatingField;
    private final StringBuilder scannerBuffer = new StringBuilder();
    private long scannerFirstKeyTime;
    private long scannerLastKeyTime;
    private KeyEventDispatcher barcodeDispatcher;

    public Pesanan() {
        this(2L, "cashier");
    }

    public Pesanan(long cashierId, String cashierName) {
        this.cashierId = cashierId;
        this.cashierName = cashierName == null || cashierName.isBlank() ? "Kasir" : cashierName;
        initComponents();
        configurePage();
        refreshTimer = new Timer(30_000, event -> loadOrders(false));
        refreshTimer.start();
        loadOrders(true);
        loadMenus("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootPanel = new javax.swing.JPanel();
        sidebarPanel = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        brandLabel = new javax.swing.JLabel();
        ordersNavButton = new javax.swing.JButton();
        historyNavButton = new javax.swing.JButton();
        accountPanel = new javax.swing.JPanel();
        accountInitialLabel = new javax.swing.JLabel();
        accountNameLabel = new javax.swing.JLabel();
        accountRoleLabel = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        ordersPanel = new javax.swing.JPanel();
        ordersTitleLabel = new javax.swing.JLabel();
        updateLabel = new javax.swing.JLabel();
        scanField = new javax.swing.JTextField();
        scanButton = new javax.swing.JButton();
        ordersScrollPane = new javax.swing.JScrollPane();
        ordersTable = new javax.swing.JTable();
        detailTitleLabel = new javax.swing.JLabel();
        detailScrollPane = new javax.swing.JScrollPane();
        detailTextArea = new javax.swing.JTextArea();
        processButton = new javax.swing.JButton();
        walkInPanel = new javax.swing.JPanel();
        walkInTitleLabel = new javax.swing.JLabel();
        manualModeButton = new javax.swing.JButton();
        barcodeModeButton = new javax.swing.JButton();
        searchMenuField = new javax.swing.JTextField();
        menusScrollPane = new javax.swing.JScrollPane();
        menusTable = new javax.swing.JTable();
        selectedOrderLabel = new javax.swing.JLabel();
        cartScrollPane = new javax.swing.JScrollPane();
        cartTable = new javax.swing.JTable();
        cashRadioButton = new javax.swing.JRadioButton();
        qrisRadioButton = new javax.swing.JRadioButton();
        paymentButtonGroup = new javax.swing.ButtonGroup();
        totalCaptionLabel = new javax.swing.JLabel();
        totalValueLabel = new javax.swing.JLabel();
        addMenuButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SwiftBite - Pesanan Kasir");
        setMinimumSize(new java.awt.Dimension(1100, 680));

        rootPanel.setBackground(new java.awt.Color(248, 248, 247));
        rootPanel.setPreferredSize(new java.awt.Dimension(1366, 720));
        sidebarPanel.setBackground(new java.awt.Color(91, 48, 29));
        sidebarPanel.setPreferredSize(new java.awt.Dimension(250, 720));

        logoLabel.setBackground(new java.awt.Color(255, 255, 255));
        logoLabel.setFont(new java.awt.Font("Segoe UI", 1, 24));
        logoLabel.setForeground(new java.awt.Color(91, 48, 29));
        logoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoLabel.setText("S");
        logoLabel.setOpaque(true);

        brandLabel.setFont(new java.awt.Font("Segoe UI", 1, 23));
        brandLabel.setForeground(new java.awt.Color(255, 255, 255));
        brandLabel.setText("SwiftBite");

        ordersNavButton.setText("Pesanan");
        historyNavButton.setText("Riwayat Transaksi");

        accountPanel.setBackground(new java.awt.Color(74, 43, 32));
        accountInitialLabel.setText("C");
        accountNameLabel.setText("cashier");
        accountRoleLabel.setText("Kasir");

        javax.swing.GroupLayout accountPanelLayout = new javax.swing.GroupLayout(accountPanel);
        accountPanel.setLayout(accountPanelLayout);
        accountPanelLayout.setHorizontalGroup(
            accountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(accountPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(accountInitialLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(accountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(accountNameLabel)
                    .addComponent(accountRoleLabel))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        accountPanelLayout.setVerticalGroup(
            accountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(accountPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(accountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(accountInitialLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(accountPanelLayout.createSequentialGroup()
                        .addComponent(accountNameLabel)
                        .addComponent(accountRoleLabel)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout sidebarPanelLayout = new javax.swing.GroupLayout(sidebarPanel);
        sidebarPanel.setLayout(sidebarPanelLayout);
        sidebarPanelLayout.setHorizontalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ordersNavButton, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(historyNavButton, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(accountPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(sidebarPanelLayout.createSequentialGroup()
                        .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(brandLabel)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        sidebarPanelLayout.setVerticalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(brandLabel))
                .addGap(42, 42, 42)
                .addComponent(ordersNavButton, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(historyNavButton, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 370, Short.MAX_VALUE)
                .addComponent(accountPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        contentPanel.setBackground(new java.awt.Color(248, 248, 247));
        ordersPanel.setBackground(new java.awt.Color(126, 78, 46));
        walkInPanel.setBackground(new java.awt.Color(126, 78, 46));

        ordersTitleLabel.setText("Pesanan QR Masuk");
        updateLabel.setText("Update --:--:--");
        scanField.setToolTipText("Masukkan kode pesanan");
        scanButton.setText("Scan");
        detailTitleLabel.setText("Detail Pesanan");
        processButton.setText("Terima Pesanan & Kirim ke Baker");
        walkInTitleLabel.setText("Pesanan Walk-In");
        manualModeButton.setText("Manual");
        barcodeModeButton.setText("Scan Barcode");
        searchMenuField.setToolTipText("Cari menu bakery");
        selectedOrderLabel.setText("Pesanan Dipilih");
        cartTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"MENU", "QTY", "SUBTOTAL"}));
        cartScrollPane.setViewportView(cartTable);
        paymentButtonGroup.add(cashRadioButton);
        cashRadioButton.setText("[x]  Tunai");
        cashRadioButton.setSelected(true);
        paymentButtonGroup.add(qrisRadioButton);
        qrisRadioButton.setText("[ ]  QRIS");
        totalCaptionLabel.setText("Total");
        totalValueLabel.setText("Rp0");
        addMenuButton.setText("Buat Pesanan");

        ordersTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"KODE PESANAN", "MEJA", "STATUS", "PEMBAYARAN", "TOTAL", "WAKTU"}));
        ordersScrollPane.setViewportView(ordersTable);

        detailTextArea.setColumns(20);
        detailTextArea.setRows(5);
        detailTextArea.setEditable(false);
        detailScrollPane.setViewportView(detailTextArea);

        menusTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"MENU", "KATEGORI", "STOK", "HARGA"}));
        menusScrollPane.setViewportView(menusTable);

        javax.swing.GroupLayout ordersPanelLayout = new javax.swing.GroupLayout(ordersPanel);
        ordersPanel.setLayout(ordersPanelLayout);
        ordersPanelLayout.setHorizontalGroup(
            ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ordersPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ordersScrollPane)
                    .addComponent(detailScrollPane)
                    .addComponent(processButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(ordersPanelLayout.createSequentialGroup()
                        .addComponent(ordersTitleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(updateLabel))
                    .addGroup(ordersPanelLayout.createSequentialGroup()
                        .addComponent(scanField)
                        .addGap(8, 8, 8)
                        .addComponent(scanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ordersPanelLayout.createSequentialGroup()
                        .addComponent(detailTitleLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18))
        );
        ordersPanelLayout.setVerticalGroup(
            ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ordersPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ordersTitleLabel)
                    .addComponent(updateLabel))
                .addGap(14, 14, 14)
                .addGroup(ordersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scanField, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(scanButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14)
                .addComponent(ordersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addGap(14, 14, 14)
                .addComponent(detailTitleLabel)
                .addGap(8, 8, 8)
                .addComponent(detailScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(processButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout walkInPanelLayout = new javax.swing.GroupLayout(walkInPanel);
        walkInPanel.setLayout(walkInPanelLayout);
        walkInPanelLayout.setHorizontalGroup(
            walkInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(walkInPanelLayout.createSequentialGroup()
                    .addGap(18, 18, 18)
                .addGroup(walkInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchMenuField)
                    .addComponent(menusScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .addComponent(cartScrollPane)
                    .addComponent(addMenuButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(walkInPanelLayout.createSequentialGroup()
                        .addComponent(cashRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addGap(8, 8, 8)
                        .addComponent(qrisRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
                    .addGroup(walkInPanelLayout.createSequentialGroup()
                        .addComponent(totalCaptionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(totalValueLabel))
                    .addGroup(walkInPanelLayout.createSequentialGroup()
                        .addComponent(manualModeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addGap(8, 8, 8)
                        .addComponent(barcodeModeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
                    .addGroup(walkInPanelLayout.createSequentialGroup()
                        .addComponent(walkInTitleLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(walkInPanelLayout.createSequentialGroup()
                        .addComponent(selectedOrderLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18))
        );
        walkInPanelLayout.setVerticalGroup(
            walkInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(walkInPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(walkInTitleLabel)
                .addGap(14, 14, 14)
                .addGroup(walkInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(manualModeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(barcodeModeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(searchMenuField, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(menusScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(selectedOrderLabel)
                .addGap(6, 6, 6)
                .addComponent(cartScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(walkInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cashRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(qrisRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(walkInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalCaptionLabel)
                    .addComponent(totalValueLabel))
                .addGap(8, 8, 8)
                .addComponent(addMenuButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(ordersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(walkInPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ordersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(walkInPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout rootPanelLayout = new javax.swing.GroupLayout(rootPanel);
        rootPanel.setLayout(rootPanelLayout);
        rootPanelLayout.setHorizontalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addComponent(sidebarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        rootPanelLayout.setVerticalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sidebarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1366, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void configurePage() {
        setSize(1366, 760);
        setLocationRelativeTo(null);
        styleSidebar();
        loadBrandLogo();
        styleContent();
        configureTables();
        ButtonGroup runtimePaymentGroup = new ButtonGroup();
        runtimePaymentGroup.add(cashRadioButton);
        runtimePaymentGroup.add(qrisRadioButton);
        cashRadioButton.setSelected(true);

        accountNameLabel.setText(cashierName);
        accountInitialLabel.setText(cashierName.substring(0, 1).toUpperCase(Locale.ROOT));
        scanField.addActionListener(event -> loadOrders(true));
        scanButton.addActionListener(event -> loadOrders(true));
        searchMenuField.getDocument().addDocumentListener((SimpleDocumentListener) () -> {
            if (!scannerUpdatingField) {
                loadMenus(searchMenuField.getText().trim());
            }
        });
        searchMenuField.addActionListener(event -> loadMenus(searchMenuField.getText().trim()));
        manualModeButton.addActionListener(event -> setOrderInputMode(false));
        barcodeModeButton.addActionListener(event -> setOrderInputMode(true));
        ordersTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedOrderDetail();
            }
        });
        processButton.addActionListener(event -> processSelectedOrder());
        MouseAdapter backToDashboard = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                new DashboardKasir(cashierId, cashierName).setVisible(true);
                dispose();
            }
        };
        logoLabel.addMouseListener(backToDashboard);
        brandLabel.addMouseListener(backToDashboard);
        historyNavButton.addActionListener(event -> {
            new riwayat_pesanan(cashierId, cashierName).setVisible(true);
            dispose();
        });
        addMenuButton.addActionListener(event -> createManualOrder());
        barcodeDispatcher = this::dispatchBarcodeInput;
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(barcodeDispatcher);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                refreshTimer.stop();
                KeyboardFocusManager.getCurrentKeyboardFocusManager()
                        .removeKeyEventDispatcher(barcodeDispatcher);
            }
        });
    }

    private void styleSidebar() {
        for (javax.swing.JButton button : new javax.swing.JButton[]{ordersNavButton, historyNavButton}) {
            button.setUI(new BasicButtonUI());
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setForeground(WHITE);
            button.setBackground(button == ordersNavButton ? BROWN_LIGHT : BROWN_MID);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        accountInitialLabel.setOpaque(true);
        accountInitialLabel.setBackground(new Color(116, 82, 65));
        accountInitialLabel.setForeground(WHITE);
        accountInitialLabel.setHorizontalAlignment(SwingConstants.CENTER);
        accountInitialLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        accountNameLabel.setForeground(WHITE);
        accountNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        accountRoleLabel.setForeground(new Color(242, 218, 197));
        logoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brandLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void loadBrandLogo() {
        java.net.URL logoUrl = getClass().getResource("/morning_bakery/assets/Swiftbite.png");
        if (logoUrl == null) {
            return;
        }
        ImageIcon original = new ImageIcon(logoUrl);
        Image scaled = original.getImage().getScaledInstance(38, 38, Image.SCALE_SMOOTH);
        logoLabel.setText("");
        logoLabel.setIcon(new ImageIcon(scaled));
        setIconImage(original.getImage());
    }

    private void styleContent() {
        for (javax.swing.JLabel title : new javax.swing.JLabel[]{ordersTitleLabel, walkInTitleLabel}) {
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(WHITE);
        }
        detailTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detailTitleLabel.setForeground(WHITE);
        selectedOrderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectedOrderLabel.setForeground(WHITE);
        totalCaptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalCaptionLabel.setForeground(WHITE);
        totalValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalValueLabel.setForeground(WHITE);
        updateLabel.setForeground(new Color(225, 245, 216));
        scanField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchMenuField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailTextArea.setBackground(new Color(145, 94, 65));
        detailTextArea.setForeground(WHITE);
        detailTextArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        for (javax.swing.JButton button : new javax.swing.JButton[]{
            scanButton, processButton, addMenuButton, manualModeButton, barcodeModeButton}) {
            button.setUI(new BasicButtonUI());
            button.setBackground(CREAM);
            button.setForeground(BROWN_DARK);
            button.setFont(new Font("Segoe UI", Font.BOLD, 13));
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        for (javax.swing.JRadioButton radio : new javax.swing.JRadioButton[]{cashRadioButton, qrisRadioButton}) {
            radio.setUI(new BasicButtonUI());
            radio.setOpaque(true);
            radio.setContentAreaFilled(true);
            radio.setBorderPainted(true);
            radio.setFont(new Font("Segoe UI", Font.BOLD, 13));
            radio.setHorizontalAlignment(SwingConstants.CENTER);
            radio.setFocusPainted(false);
            radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            radio.setBorder(BorderFactory.createLineBorder(new Color(210, 170, 142), 1));
            radio.addItemListener(event -> updatePaymentStyles());
        }
        updatePaymentStyles();
    }

    private void updatePaymentStyles() {
        stylePaymentOption(cashRadioButton, cashRadioButton.isSelected());
        stylePaymentOption(qrisRadioButton, qrisRadioButton.isSelected());
    }

    private void stylePaymentOption(javax.swing.JRadioButton option, boolean selected) {
        option.setBackground(selected ? CREAM : new Color(110, 65, 42));
        option.setForeground(selected ? BROWN_DARK : WHITE);
        option.setText((selected ? "[x]  " : "[ ]  ")
                + (option == cashRadioButton ? "Tunai" : "QRIS"));
    }

    private void configureTables() {
        ordersTable.setModel(ordersModel);
        menusTable.setModel(menusModel);
        cartTable.setModel(cartModel);
        configureTable(ordersTable);
        configureTable(menusTable);
        configureTable(cartTable);
        installSlowScroll(ordersScrollPane);
        installSlowScroll(menusScrollPane);
        ordersTable.removeColumn(ordersTable.getColumnModel().getColumn(0));
        menusTable.removeColumn(menusTable.getColumnModel().getColumn(0));
        menusTable.removeColumn(menusTable.getColumnModel().getColumn(3));
        cartTable.removeColumn(cartTable.getColumnModel().getColumn(0));
        menusTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(210);
        menusTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        menusTable.getColumnModel().getColumn(4).setPreferredWidth(72);
        menusTable.getColumnModel().getColumn(4).setMinWidth(68);
        menusTable.getColumnModel().getColumn(4).setMaxWidth(82);
        DefaultTableCellRenderer quantityControlRenderer = new DefaultTableCellRenderer();
        quantityControlRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        quantityControlRenderer.setBorder(BorderFactory.createEmptyBorder());
        quantityControlRenderer.setOpaque(true);
        menusTable.getColumnModel().getColumn(4).setCellRenderer(quantityControlRenderer);
        DefaultTableCellRenderer cartQuantityRenderer = new DefaultTableCellRenderer();
        cartQuantityRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        cartQuantityRenderer.setOpaque(true);
        cartTable.getColumnModel().getColumn(1).setCellRenderer(cartQuantityRenderer);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(170);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(45);
        menusTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int viewRow = menusTable.rowAtPoint(event.getPoint());
                int viewColumn = menusTable.columnAtPoint(event.getPoint());
                if (viewRow >= 0 && viewColumn == menusTable.getColumnCount() - 1) {
                    java.awt.Rectangle cell = menusTable.getCellRect(viewRow, viewColumn, true);
                    int relativeX = event.getX() - cell.x;
                    int modelRow = menusTable.convertRowIndexToModel(viewRow);
                    if (relativeX < cell.width / 2) {
                        decrementMenuFromCart(modelRow);
                    } else {
                        addMenuToCart(modelRow);
                    }
                }
            }
        });
        cartTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    int viewRow = cartTable.rowAtPoint(event.getPoint());
                    if (viewRow >= 0) {
                        decrementCartItem(cartTable.convertRowIndexToModel(viewRow));
                    }
                }
            }
        });
        setOrderInputMode(false);
    }

    private void setOrderInputMode(boolean useBarcode) {
        barcodeMode = useBarcode;
        scannerBuffer.setLength(0);
        manualModeButton.setBackground(useBarcode ? BROWN_LIGHT : CREAM);
        manualModeButton.setForeground(useBarcode ? WHITE : BROWN_DARK);
        barcodeModeButton.setBackground(useBarcode ? CREAM : BROWN_LIGHT);
        barcodeModeButton.setForeground(useBarcode ? BROWN_DARK : WHITE);
        searchMenuField.setText("");
        searchMenuField.setToolTipText(useBarcode
                ? "Scan atau masukkan barcode menu"
                : "Cari nama menu bakery");
        searchMenuField.requestFocusInWindow();
        loadMenus("");
    }

    private boolean dispatchBarcodeInput(KeyEvent event) {
        if (!isActive() || event.getID() != KeyEvent.KEY_TYPED) {
            return false;
        }

        char character = event.getKeyChar();
        long now = System.nanoTime();
        long gap = scannerLastKeyTime == 0 ? 0 : now - scannerLastKeyTime;
        if (gap > 120_000_000L) {
            scannerBuffer.setLength(0);
            scannerFirstKeyTime = now;
        } else if (scannerBuffer.isEmpty()) {
            scannerFirstKeyTime = now;
        }
        scannerLastKeyTime = now;

        if (character == '\n' || character == '\r') {
            String barcode = scannerBuffer.toString().trim();
            long duration = now - scannerFirstKeyTime;
            boolean fastScannerInput = barcode.length() >= 6
                    && duration <= barcode.length() * 50_000_000L;
            scannerBuffer.setLength(0);

            if (barcodeMode || fastScannerInput) {
                if (!barcodeMode) {
                    setOrderInputMode(true);
                }
                setBarcodeFieldValue(barcode);
                loadMenus(barcode);
                return true;
            }
            return false;
        }

        if (Character.isISOControl(character)) {
            return false;
        }

        scannerBuffer.append(character);
        if (barcodeMode) {
            setBarcodeFieldValue(scannerBuffer.toString());
            return true;
        }
        return false;
    }

    private void setBarcodeFieldValue(String value) {
        scannerUpdatingField = true;
        try {
            searchMenuField.setText(value);
        } finally {
            scannerUpdatingField = false;
        }
    }

    private void installSlowScroll(JScrollPane scrollPane) {
        scrollPane.setWheelScrollingEnabled(false);
        scrollPane.addMouseWheelListener(event -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            int movement = (int) Math.round(event.getPreciseWheelRotation() * 12);
            bar.setValue(bar.getValue() + movement);
            event.consume();
        });
    }

    private void configureTable(JTable table) {
        table.setRowHeight(42);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(new Color(117, 70, 43));
        table.setForeground(WHITE);
        table.setSelectionBackground(new Color(177, 114, 73));
        table.setSelectionForeground(WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setBackground(BROWN_DARK);
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        renderer.setOpaque(true);
        table.setDefaultRenderer(Object.class, renderer);
    }

    private void loadOrders(boolean showError) {
        String code = scanField.getText().trim();
        new SwingWorker<java.util.List<Object[]>, Void>() {
            @Override
            protected java.util.List<Object[]> doInBackground() throws Exception {
                String sql = "SELECT o.id_order,o.kode_pesanan,t.nama_meja,o.status,"
                        + "COALESCE(o.payment_status,'belum_dibayar') payment_status,"
                        + "o.total_harga,o.created_at FROM orders o "
                        + "JOIN tables t ON t.id_meja=o.id_meja "
                        + "WHERE o.status NOT IN ('selesai','dibatalkan') "
                        + "AND (?='' OR o.kode_pesanan LIKE CONCAT('%',?,'%')) "
                        + "ORDER BY o.created_at ASC";
                java.util.List<Object[]> rows = new java.util.ArrayList<>();
                try (Connection connection = openConnection();
                        PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, code);
                    statement.setString(2, code);
                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            rows.add(new Object[]{result.getLong("id_order"), result.getString("kode_pesanan"),
                                result.getString("nama_meja"), readable(result.getString("status")),
                                readable(result.getString("payment_status")), formatRupiah(result.getBigDecimal("total_harga")),
                                result.getTimestamp("created_at").toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))});
                        }
                    }
                }
                return rows;
            }

            @Override
            protected void done() {
                try {
                    replaceRows(ordersModel, get());
                    updateLabel.setText("Update " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                } catch (Exception exception) {
                    if (showError) {
                        showDatabaseError("Daftar pesanan tidak dapat dimuat.");
                    }
                }
            }
        }.execute();
    }

    private void loadMenus(String keyword) {
        boolean searchByBarcode = barcodeMode;
        int requestSequence = ++menuSearchSequence;
        new SwingWorker<java.util.List<Object[]>, Void>() {
            @Override
            protected java.util.List<Object[]> doInBackground() throws Exception {
                String searchClause = searchByBarcode && !keyword.isBlank()
                        ? "m.barcode = ?"
                        : "m.nama_menu LIKE CONCAT('%',?,'%')";
                String sql = "SELECT m.id_menu,m.nama_menu,c.nama_kategori,m.stok,m.harga "
                        + "FROM menus m JOIN categories c ON c.id_kategori=m.id_kategori "
                        + "WHERE m.status='tersedia' AND " + searchClause + " ORDER BY m.nama_menu";
                java.util.List<Object[]> rows = new java.util.ArrayList<>();
                try (Connection connection = openConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, keyword);
                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            BigDecimal price = result.getBigDecimal("harga");
                            rows.add(new Object[]{result.getLong("id_menu"), result.getString("nama_menu"),
                                result.getString("nama_kategori"), result.getInt("stok"), price,
                                formatRupiah(price), "+"});
                        }
                    }
                }
                return rows;
            }

            @Override
            protected void done() {
                if (requestSequence != menuSearchSequence) {
                    return;
                }
                try {
                    java.util.List<Object[]> rows = get();
                    for (Object[] row : rows) {
                        CartItem item = cartItems.get(((Number) row[0]).longValue());
                        row[6] = quantityControlText(item == null ? 0 : item.quantity());
                    }
                    replaceRows(menusModel, rows);
                    if (searchByBarcode && menusModel.getRowCount() == 1) {
                        menusTable.setRowSelectionInterval(0, 0);
                    }
                } catch (Exception ignored) {
                }
            }
        }.execute();
    }

    private void loadSelectedOrderDetail() {
        int viewRow = ordersTable.getSelectedRow();
        if (viewRow < 0) {
            detailTextArea.setText("");
            return;
        }
        long orderId = ((Number) ordersModel.getValueAt(ordersTable.convertRowIndexToModel(viewRow), 0)).longValue();
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                String sql = "SELECT m.nama_menu,d.qty,d.subtotal FROM order_details d "
                        + "JOIN menus m ON m.id_menu=d.id_menu WHERE d.id_order=? ORDER BY d.id_detail_order";
                StringBuilder detail = new StringBuilder();
                try (Connection connection = openConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setLong(1, orderId);
                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            detail.append(result.getInt("qty")).append("x ")
                                    .append(result.getString("nama_menu")).append("  ")
                                    .append(formatRupiah(result.getBigDecimal("subtotal"))).append('\n');
                        }
                    }
                }
                return detail.length() == 0 ? "Belum ada detail item." : detail.toString();
            }

            @Override
            protected void done() {
                try { detailTextArea.setText(get()); } catch (Exception ignored) { }
            }
        }.execute();
    }

    private void processSelectedOrder() {
        int viewRow = ordersTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan terlebih dahulu.");
            return;
        }
        long orderId = ((Number) ordersModel.getValueAt(ordersTable.convertRowIndexToModel(viewRow), 0)).longValue();
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE orders SET status='diproses',updated_at=NOW() WHERE id_order=? AND status='menunggu'")) {
            statement.setLong(1, orderId);
            int changed = statement.executeUpdate();
            JOptionPane.showMessageDialog(this, changed > 0 ? "Pesanan dikirim ke Baker." : "Status pesanan tidak dapat diubah.");
            loadOrders(false);
        } catch (SQLException exception) {
            showDatabaseError("Status pesanan tidak dapat diperbarui.");
        }
    }

    private void addMenuToCart(int modelRow) {
        long menuId = ((Number) menusModel.getValueAt(modelRow, 0)).longValue();
        String menuName = menusModel.getValueAt(modelRow, 1).toString();
        int stock = ((Number) menusModel.getValueAt(modelRow, 3)).intValue();
        BigDecimal price = (BigDecimal) menusModel.getValueAt(modelRow, 4);
        CartItem current = cartItems.get(menuId);
        int newQuantity = current == null ? 1 : current.quantity() + 1;
        if (newQuantity > stock) {
            JOptionPane.showMessageDialog(this, "Jumlah melebihi stok " + menuName + ".");
            return;
        }
        cartItems.put(menuId, new CartItem(menuId, menuName, price, newQuantity, stock));
        refreshCartDisplay();
    }

    private void decrementMenuFromCart(int modelRow) {
        long menuId = ((Number) menusModel.getValueAt(modelRow, 0)).longValue();
        CartItem current = cartItems.get(menuId);
        if (current == null) {
            return;
        }
        if (current.quantity() <= 1) {
            cartItems.remove(menuId);
        } else {
            cartItems.put(menuId, new CartItem(menuId, current.name(), current.price(),
                    current.quantity() - 1, current.stock()));
        }
        refreshCartDisplay();
    }

    private void decrementCartItem(int modelRow) {
        long menuId = ((Number) cartModel.getValueAt(modelRow, 0)).longValue();
        CartItem current = cartItems.get(menuId);
        if (current == null) {
            return;
        }
        if (current.quantity() <= 1) {
            cartItems.remove(menuId);
        } else {
            cartItems.put(menuId, new CartItem(menuId, current.name(), current.price(),
                    current.quantity() - 1, current.stock()));
        }
        refreshCartDisplay();
    }

    private void refreshCartDisplay() {
        cartModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems.values()) {
            BigDecimal subtotal = item.price().multiply(BigDecimal.valueOf(item.quantity()));
            cartModel.addRow(new Object[]{item.menuId(), item.name(), item.quantity(), formatRupiah(subtotal)});
            total = total.add(subtotal);
        }
        totalValueLabel.setText(formatRupiah(total));
        for (int row = 0; row < menusModel.getRowCount(); row++) {
            long menuId = ((Number) menusModel.getValueAt(row, 0)).longValue();
            CartItem item = cartItems.get(menuId);
            menusModel.setValueAt(quantityControlText(item == null ? 0 : item.quantity()), row, 6);
        }
    }

    private String quantityControlText(int quantity) {
        return "-   " + quantity + "   +";
    }

    private void createManualOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih minimal satu menu dengan tombol +.");
            return;
        }

        java.util.List<CartItem> requestedItems = new java.util.ArrayList<>(cartItems.values());
        String paymentMethod = qrisRadioButton.isSelected() ? "qris" : "cash";

        addMenuButton.setEnabled(false);
        addMenuButton.setText("Menyimpan Pesanan...");
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return saveManualOrder(requestedItems, paymentMethod);
            }

            @Override
            protected void done() {
                addMenuButton.setEnabled(true);
                addMenuButton.setText("Buat Pesanan");
                try {
                    String orderCode = get();
                    JOptionPane.showMessageDialog(Pesanan.this,
                            "Pesanan " + orderCode + " berhasil dibuat dan masuk ke daftar pesanan.");
                    cartItems.clear();
                    refreshCartDisplay();
                    searchMenuField.setText("");
                    loadMenus("");
                    loadOrders(false);
                } catch (Exception exception) {
                    String reason = exception.getCause() == null
                            ? exception.getMessage()
                            : exception.getCause().getMessage();
                    JOptionPane.showMessageDialog(Pesanan.this,
                            "Pesanan gagal disimpan.\n" + reason,
                            "Pesanan Manual",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private String saveManualOrder(java.util.List<CartItem> requestedItems, String paymentMethod) throws SQLException {
        String orderCode = "ORD-JAVA-"
                + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase(Locale.ROOT);

        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try {
                long tableId = findCashierTable(connection);
                java.util.List<ManualOrderItem> items = new java.util.ArrayList<>();
                BigDecimal total = BigDecimal.ZERO;

                try (PreparedStatement menuStatement = connection.prepareStatement(
                        "SELECT nama_menu,harga,stok FROM menus WHERE id_menu=? AND status='tersedia' FOR UPDATE")) {
                    for (CartItem requested : requestedItems) {
                        menuStatement.setLong(1, requested.menuId());
                        try (ResultSet result = menuStatement.executeQuery()) {
                            if (!result.next()) {
                                throw new SQLException("Menu tidak tersedia.");
                            }
                            int stock = result.getInt("stok");
                            if (stock < requested.quantity()) {
                                throw new SQLException("Stok " + result.getString("nama_menu") + " tidak mencukupi.");
                            }
                            BigDecimal price = result.getBigDecimal("harga");
                            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(requested.quantity()));
                            items.add(new ManualOrderItem(requested.menuId(), price, requested.quantity(), subtotal));
                            total = total.add(subtotal);
                        }
                    }
                }

                long orderId;
                String orderSql = "INSERT INTO orders "
                        + "(id_meja,kode_pesanan,total_harga,status,metode_pembayaran,notes,payment_status,created_at,updated_at) "
                        + "VALUES (?,?,?,'menunggu',?,'Pesanan manual kasir','belum_dibayar',NOW(),NOW())";
                try (PreparedStatement statement = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setLong(1, tableId);
                    statement.setString(2, orderCode);
                    statement.setBigDecimal(3, total);
                    statement.setString(4, paymentMethod);
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("ID pesanan tidak berhasil dibuat.");
                        }
                        orderId = keys.getLong(1);
                    }
                }

                try (PreparedStatement detailStatement = connection.prepareStatement(
                        "INSERT INTO order_details (id_order,id_menu,qty,harga,subtotal,created_at,updated_at) "
                        + "VALUES (?,?,?,?,?,NOW(),NOW())");
                        PreparedStatement stockStatement = connection.prepareStatement(
                                "UPDATE menus SET status=CASE WHEN stok<=? THEN 'habis' ELSE status END,"
                                + "stok=stok-?,updated_at=NOW() WHERE id_menu=?")) {
                    for (ManualOrderItem item : items) {
                        detailStatement.setLong(1, orderId);
                        detailStatement.setLong(2, item.menuId());
                        detailStatement.setInt(3, item.quantity());
                        detailStatement.setBigDecimal(4, item.price());
                        detailStatement.setBigDecimal(5, item.subtotal());
                        detailStatement.addBatch();
                        stockStatement.setInt(1, item.quantity());
                        stockStatement.setInt(2, item.quantity());
                        stockStatement.setLong(3, item.menuId());
                        stockStatement.addBatch();
                    }
                    detailStatement.executeBatch();
                    stockStatement.executeBatch();
                }

                connection.commit();
                return orderCode;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private long findCashierTable(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id_meja FROM tables WHERE nama_meja='Kasir Langsung' AND status='aktif' LIMIT 1");
                ResultSet result = statement.executeQuery()) {
            if (!result.next()) {
                throw new SQLException("Meja 'Kasir Langsung' tidak ditemukan.");
            }
            return result.getLong("id_meja");
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private String formatRupiah(BigDecimal value) {
        return "Rp" + rupiah.format(value == null ? 0 : value.longValue());
    }

    private static String readable(String value) {
        if (value == null) return "-";
        String text = value.replace('_', ' ');
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private static DefaultTableModel readOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private static void replaceRows(DefaultTableModel model, java.util.List<Object[]> rows) {
        model.setRowCount(0);
        rows.forEach(model::addRow);
    }

    private void showDatabaseError(String message) {
        JOptionPane.showMessageDialog(this, message + "\nPastikan MySQL dan database swiftbite aktif.",
                "Koneksi database", JOptionPane.ERROR_MESSAGE);
    }

    private interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update();
        @Override default void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override default void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override default void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
    }

    private record ManualOrderItem(long menuId, BigDecimal price, int quantity, BigDecimal subtotal) {
    }

    private record CartItem(long menuId, String name, BigDecimal price, int quantity, int stock) {
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new Pesanan().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel accountPanel;
    private javax.swing.JLabel accountInitialLabel;
    private javax.swing.JLabel accountNameLabel;
    private javax.swing.JLabel accountRoleLabel;
    private javax.swing.JButton addMenuButton;
    private javax.swing.JLabel brandLabel;
    private javax.swing.JButton barcodeModeButton;
    private javax.swing.JRadioButton cashRadioButton;
    private javax.swing.JScrollPane cartScrollPane;
    private javax.swing.JTable cartTable;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JScrollPane detailScrollPane;
    private javax.swing.JTextArea detailTextArea;
    private javax.swing.JLabel detailTitleLabel;
    private javax.swing.JButton historyNavButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JButton manualModeButton;
    private javax.swing.JScrollPane menusScrollPane;
    private javax.swing.JTable menusTable;
    private javax.swing.JButton ordersNavButton;
    private javax.swing.JPanel ordersPanel;
    private javax.swing.JScrollPane ordersScrollPane;
    private javax.swing.JTable ordersTable;
    private javax.swing.JLabel ordersTitleLabel;
    private javax.swing.JButton processButton;
    private javax.swing.JRadioButton qrisRadioButton;
    private javax.swing.JPanel rootPanel;
    private javax.swing.JButton scanButton;
    private javax.swing.JTextField scanField;
    private javax.swing.JTextField searchMenuField;
    private javax.swing.JLabel selectedOrderLabel;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JLabel updateLabel;
    private javax.swing.ButtonGroup paymentButtonGroup;
    private javax.swing.JLabel totalCaptionLabel;
    private javax.swing.JLabel totalValueLabel;
    private javax.swing.JPanel walkInPanel;
    private javax.swing.JLabel walkInTitleLabel;
    // End of variables declaration//GEN-END:variables
}
