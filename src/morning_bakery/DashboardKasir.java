package morning_bakery;

import java.awt.BasicStroke;
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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class DashboardKasir extends JFrame {

    private static final Color CREAM = new Color(252, 248, 241);
    private static final Color WHITE = new Color(255, 255, 255);
    private static final Color BROWN = new Color(91, 48, 29);
    private static final Color BROWN_DARK = new Color(58, 28, 19);
    private static final Color BROWN_LIGHT = new Color(157, 98, 62);
    private static final Color MUTED = new Color(242, 218, 197);
    private static final Color SUCCESS = new Color(36, 151, 91);
    private static final Color DANGER = new Color(239, 77, 86);

    private static final String DB_URL
            = "jdbc:mysql://127.0.0.1:3306/swiftbite"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private final long cashierId;
    private final String cashierName;
    private final NumberFormat rupiahFormat = NumberFormat.getIntegerInstance(Locale.forLanguageTag("id-ID"));

    private final JLabel ordersValue = createValueLabel("0");
    private final JLabel waitingValue = createValueLabel("0");
    private final JLabel processingValue = createValueLabel("0");
    private final JLabel revenueValue = createValueLabel("Rp0");
    private final JLabel attendanceBadge = new JLabel("Belum Absen");
    private final JLabel attendanceDescription = new JLabel("Status absensi hari ini.");
    private final JButton attendanceButton = new JButton("Absen Masuk");
    private final DefaultTableModel summaryModel = new DefaultTableModel(
            new Object[]{"DATA", "JUMLAH"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final Timer refreshTimer;

    public DashboardKasir() {
        this(2L, "cashier");
    }

    public DashboardKasir(long cashierId, String cashierName) {
        this.cashierId = cashierId;
        this.cashierName = cashierName == null || cashierName.isBlank() ? "Kasir" : cashierName;

        initComponents();
        configureDashboard();
        refreshTimer = new Timer(30_000, event -> loadDashboardData(false));
        refreshTimer.start();
        loadDashboardData(true);
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
        designAccountInitialLabel = new javax.swing.JLabel();
        designAccountNameLabel = new javax.swing.JLabel();
        designAccountRoleLabel = new javax.swing.JLabel();
        designContentPanel = new javax.swing.JPanel();
        designHeaderPanel = new javax.swing.JPanel();
        designEyebrowLabel = new javax.swing.JLabel();
        designTitleLabel = new javax.swing.JLabel();
        designSubtitleLabel = new javax.swing.JLabel();
        designStatsPanel = new javax.swing.JPanel();
        designOrdersStatPanel = new javax.swing.JPanel();
        designOrdersStatLabel = new javax.swing.JLabel();
        designOrdersStatValue = new javax.swing.JLabel();
        designWaitingStatPanel = new javax.swing.JPanel();
        designWaitingStatLabel = new javax.swing.JLabel();
        designWaitingStatValue = new javax.swing.JLabel();
        designProcessingStatPanel = new javax.swing.JPanel();
        designProcessingStatLabel = new javax.swing.JLabel();
        designProcessingStatValue = new javax.swing.JLabel();
        designRevenueStatPanel = new javax.swing.JPanel();
        designRevenueStatLabel = new javax.swing.JLabel();
        designRevenueStatValue = new javax.swing.JLabel();
        designAttendancePanel = new javax.swing.JPanel();
        designAttendanceTitleLabel = new javax.swing.JLabel();
        designAttendanceDescriptionLabel = new javax.swing.JLabel();
        designAttendanceStatusLabel = new javax.swing.JLabel();
        designAttendanceButton = new javax.swing.JButton();
        designSummaryPanel = new javax.swing.JPanel();
        designSummaryTitleLabel = new javax.swing.JLabel();
        designSummaryScrollPane = new javax.swing.JScrollPane();
        designSummaryTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SwiftBite - Dashboard Kasir");
        setMinimumSize(new java.awt.Dimension(1080, 680));

        designRootPanel.setBackground(new java.awt.Color(248, 248, 247));

        designSidebarPanel.setBackground(new java.awt.Color(91, 48, 29));
        designSidebarPanel.setPreferredSize(new java.awt.Dimension(250, 768));

        designLogoLabel.setBackground(new java.awt.Color(255, 255, 255));
        designLogoLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        designLogoLabel.setForeground(new java.awt.Color(91, 48, 29));
        designLogoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        designLogoLabel.setText("S");
        designLogoLabel.setOpaque(true);

        designBrandLabel.setFont(new java.awt.Font("Segoe UI", 1, 23)); // NOI18N
        designBrandLabel.setForeground(new java.awt.Color(255, 255, 255));
        designBrandLabel.setText("SwiftBite");

        designOrdersButton.setBackground(new java.awt.Color(126, 78, 46));
        designOrdersButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        designOrdersButton.setForeground(new java.awt.Color(255, 255, 255));
        designOrdersButton.setText("Pesanan");
        designOrdersButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 18, 12, 18));
        designOrdersButton.setFocusPainted(false);
        designOrdersButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        designHistoryButton.setBackground(new java.awt.Color(117, 70, 43));
        designHistoryButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        designHistoryButton.setForeground(new java.awt.Color(255, 255, 255));
        designHistoryButton.setText("Riwayat Transaksi");
        designHistoryButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 18, 12, 18));
        designHistoryButton.setFocusPainted(false);
        designHistoryButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        designAccountPanel.setBackground(new java.awt.Color(74, 43, 32));
        designAccountPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(126, 91, 72)));

        designAccountInitialLabel.setBackground(new java.awt.Color(116, 82, 65));
        designAccountInitialLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        designAccountInitialLabel.setForeground(new java.awt.Color(255, 255, 255));
        designAccountInitialLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        designAccountInitialLabel.setText("C");
        designAccountInitialLabel.setOpaque(true);

        designAccountNameLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        designAccountNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        designAccountNameLabel.setText("cashier");

        designAccountRoleLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        designAccountRoleLabel.setForeground(new java.awt.Color(242, 218, 197));
        designAccountRoleLabel.setText("Kasir");

        javax.swing.GroupLayout designAccountPanelLayout = new javax.swing.GroupLayout(designAccountPanel);
        designAccountPanel.setLayout(designAccountPanelLayout);
        designAccountPanelLayout.setHorizontalGroup(
            designAccountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designAccountPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(designAccountInitialLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(designAccountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(designAccountNameLabel)
                    .addComponent(designAccountRoleLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        designAccountPanelLayout.setVerticalGroup(
            designAccountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designAccountPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(designAccountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(designAccountInitialLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(designAccountPanelLayout.createSequentialGroup()
                        .addComponent(designAccountNameLabel)
                        .addGap(2, 2, 2)
                        .addComponent(designAccountRoleLabel)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout designSidebarPanelLayout = new javax.swing.GroupLayout(designSidebarPanel);
        designSidebarPanel.setLayout(designSidebarPanelLayout);
        designSidebarPanelLayout.setHorizontalGroup(
            designSidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designSidebarPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(designSidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(designOrdersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(designHistoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(designAccountPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(designSidebarPanelLayout.createSequentialGroup()
                        .addComponent(designLogoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(designBrandLabel)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        designSidebarPanelLayout.setVerticalGroup(
            designSidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designSidebarPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(designSidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(designLogoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(designBrandLabel))
                .addGap(42, 42, 42)
                .addComponent(designOrdersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(designHistoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 465, Short.MAX_VALUE)
                .addComponent(designAccountPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        designContentPanel.setBackground(new java.awt.Color(248, 248, 247));

        designHeaderPanel.setBackground(new java.awt.Color(91, 48, 29));

        designEyebrowLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        designEyebrowLabel.setForeground(new java.awt.Color(255, 232, 209));
        designEyebrowLabel.setText("CASHIER OPERASIONAL");

        designTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 40)); // NOI18N
        designTitleLabel.setForeground(new java.awt.Color(255, 255, 255));
        designTitleLabel.setText("Dashboard Kasir");

        designSubtitleLabel.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        designSubtitleLabel.setForeground(new java.awt.Color(255, 235, 218));
        designSubtitleLabel.setText("Pantau ringkasan pesanan, pembayaran, dan pendapatan kasir hari ini.");

        javax.swing.GroupLayout designHeaderPanelLayout = new javax.swing.GroupLayout(designHeaderPanel);
        designHeaderPanel.setLayout(designHeaderPanelLayout);
        designHeaderPanelLayout.setHorizontalGroup(
            designHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designHeaderPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(designHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(designEyebrowLabel)
                    .addComponent(designTitleLabel)
                    .addComponent(designSubtitleLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        designHeaderPanelLayout.setVerticalGroup(
            designHeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designHeaderPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(designEyebrowLabel)
                .addGap(6, 6, 6)
                .addComponent(designTitleLabel)
                .addGap(4, 4, 4)
                .addComponent(designSubtitleLabel)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        designStatsPanel.setBackground(new java.awt.Color(248, 248, 247));
        designStatsPanel.setLayout(new java.awt.GridLayout(1, 4, 14, 0));

        configureDesignStatPanel(designOrdersStatPanel, designOrdersStatLabel, designOrdersStatValue, "Pesanan Hari Ini", "0");
        designStatsPanel.add(designOrdersStatPanel);
        configureDesignStatPanel(designWaitingStatPanel, designWaitingStatLabel, designWaitingStatValue, "Menunggu Pembayaran", "0");
        designStatsPanel.add(designWaitingStatPanel);
        configureDesignStatPanel(designProcessingStatPanel, designProcessingStatLabel, designProcessingStatValue, "Diproses", "0");
        designStatsPanel.add(designProcessingStatPanel);
        configureDesignStatPanel(designRevenueStatPanel, designRevenueStatLabel, designRevenueStatValue, "Pendapatan Hari Ini", "Rp0");
        designStatsPanel.add(designRevenueStatPanel);

        designAttendancePanel.setBackground(new java.awt.Color(101, 56, 35));

        designAttendanceTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        designAttendanceTitleLabel.setForeground(new java.awt.Color(255, 255, 255));
        designAttendanceTitleLabel.setText("Absensi");

        designAttendanceDescriptionLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        designAttendanceDescriptionLabel.setForeground(new java.awt.Color(255, 234, 216));
        designAttendanceDescriptionLabel.setText("Status absensi hari ini belum tercatat.");

        designAttendanceStatusLabel.setBackground(new java.awt.Color(239, 77, 86));
        designAttendanceStatusLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        designAttendanceStatusLabel.setForeground(new java.awt.Color(255, 255, 255));
        designAttendanceStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        designAttendanceStatusLabel.setText("Belum Absen");
        designAttendanceStatusLabel.setOpaque(true);

        designAttendanceButton.setBackground(new java.awt.Color(252, 248, 241));
        designAttendanceButton.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        designAttendanceButton.setForeground(new java.awt.Color(58, 28, 19));
        designAttendanceButton.setText("Absen Masuk");
        designAttendanceButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        designAttendanceButton.setFocusPainted(false);

        javax.swing.GroupLayout designAttendancePanelLayout = new javax.swing.GroupLayout(designAttendancePanel);
        designAttendancePanel.setLayout(designAttendancePanelLayout);
        designAttendancePanelLayout.setHorizontalGroup(
            designAttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designAttendancePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(designAttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(designAttendanceTitleLabel)
                    .addComponent(designAttendanceDescriptionLabel)
                    .addComponent(designAttendanceStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(designAttendanceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        designAttendancePanelLayout.setVerticalGroup(
            designAttendancePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designAttendancePanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(designAttendanceTitleLabel)
                .addGap(7, 7, 7)
                .addComponent(designAttendanceDescriptionLabel)
                .addGap(24, 24, 24)
                .addComponent(designAttendanceStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
            .addGroup(designAttendancePanelLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(designAttendanceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        designSummaryPanel.setBackground(new java.awt.Color(91, 48, 29));

        designSummaryTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 21)); // NOI18N
        designSummaryTitleLabel.setForeground(new java.awt.Color(255, 255, 255));
        designSummaryTitleLabel.setText("Ringkasan Kasir Hari Ini");

        designSummaryTable.setBackground(new java.awt.Color(111, 67, 44));
        designSummaryTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        designSummaryTable.setForeground(new java.awt.Color(255, 255, 255));
        designSummaryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Pesanan Hari Ini", "0"},
                {"Menunggu Pembayaran", "0"},
                {"Diproses", "0"},
                {"Pendapatan Hari Ini", "Rp0"}
            },
            new String [] {"DATA", "JUMLAH"}
        ) {
            boolean[] canEdit = new boolean [] {false, false};
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        designSummaryTable.setRowHeight(44);
        designSummaryTable.setGridColor(new java.awt.Color(145, 104, 81));
        designSummaryScrollPane.setViewportView(designSummaryTable);

        javax.swing.GroupLayout designSummaryPanelLayout = new javax.swing.GroupLayout(designSummaryPanel);
        designSummaryPanel.setLayout(designSummaryPanelLayout);
        designSummaryPanelLayout.setHorizontalGroup(
            designSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designSummaryPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(designSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(designSummaryScrollPane)
                    .addGroup(designSummaryPanelLayout.createSequentialGroup()
                        .addComponent(designSummaryTitleLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );
        designSummaryPanelLayout.setVerticalGroup(
            designSummaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designSummaryPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(designSummaryTitleLabel)
                .addGap(12, 12, 12)
                .addComponent(designSummaryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout designContentPanelLayout = new javax.swing.GroupLayout(designContentPanel);
        designContentPanel.setLayout(designContentPanelLayout);
        designContentPanelLayout.setHorizontalGroup(
            designContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designContentPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(designContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(designHeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(designStatsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(designAttendancePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(designSummaryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );
        designContentPanelLayout.setVerticalGroup(
            designContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designContentPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(designHeaderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(designStatsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(designAttendancePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(designSummaryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout designRootPanelLayout = new javax.swing.GroupLayout(designRootPanel);
        designRootPanel.setLayout(designRootPanelLayout);
        designRootPanelLayout.setHorizontalGroup(
            designRootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(designRootPanelLayout.createSequentialGroup()
                .addComponent(designSidebarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(designContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        designRootPanelLayout.setVerticalGroup(
            designRootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(designSidebarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(designContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(designRootPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(designRootPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void configureDesignStatPanel(
            javax.swing.JPanel panel,
            javax.swing.JLabel title,
            javax.swing.JLabel value,
            String titleText,
            String valueText) {
        panel.setBackground(new java.awt.Color(91, 48, 29));
        title.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(new java.awt.Color(242, 218, 197));
        title.setText(titleText);
        value.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 23));
        value.setForeground(new java.awt.Color(255, 255, 255));
        value.setText(valueText);

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(title)
                    .addComponent(value))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(title)
                .addGap(6, 6, 6)
                .addComponent(value)
                .addContainerGap(12, Short.MAX_VALUE))
        );
    }

    private void configureDashboard() {
        setSize(1366, 768);
        setLocationRelativeTo(null);

        URL logoUrl = getClass().getResource("/morning_bakery/assets/Swiftbite.png");
        if (logoUrl != null) {
            setIconImage(new ImageIcon(logoUrl).getImage());
        }

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(248, 248, 247));
        root.add(createSidebar(logoUrl), BorderLayout.WEST);
        JScrollPane mainScrollPane = createMainContent();
        root.add(mainScrollPane, BorderLayout.CENTER);
        setContentPane(root);

        SwingUtilities.invokeLater(() -> {
            mainScrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));
            mainScrollPane.getVerticalScrollBar().setValue(0);
            root.requestFocusInWindow();
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                refreshTimer.stop();
            }
        });
    }

    private JPanel createSidebar(URL logoUrl) {
        GradientPanel sidebar = new GradientPanel(
                new BorderLayout(), new Color(169, 108, 69), BROWN_DARK, 0);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(BROWN_DARK);
        sidebar.setOpaque(true);
        sidebar.setBorder(BorderFactory.createEmptyBorder(26, 18, 28, 18));

        JPanel brandPanel = transparentPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        JLabel logo = new JLabel("S", SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(48, 48));
        logo.setOpaque(true);
        logo.setBackground(WHITE);
        logo.setForeground(BROWN);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        logo.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        if (logoUrl != null) {
            Image scaled = new ImageIcon(logoUrl).getImage()
                    .getScaledInstance(38, 38, Image.SCALE_SMOOTH);
            logo.setText("");
            logo.setIcon(new ImageIcon(scaled));
        }

        JLabel brand = new JLabel("SwiftBite");
        brand.setForeground(WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 23));
        brandPanel.add(logo);
        brandPanel.add(brand);

        JPanel menuPanel = transparentPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 55)));
        menuPanel.add(Box.createVerticalStrut(22));
        JButton ordersButton = createNavButton("Pesanan", true);
        ordersButton.addActionListener(event -> {
            new Pesanan(cashierId, cashierName).setVisible(true);
            dispose();
        });
        menuPanel.add(ordersButton);
        menuPanel.add(Box.createVerticalStrut(10));
        JButton historyButton = createNavButton("Riwayat Transaksi", false);
        historyButton.addActionListener(event -> {
            new riwayat_pesanan(cashierId, cashierName).setVisible(true);
            dispose();
        });
        menuPanel.add(historyButton);
        menuPanel.add(Box.createVerticalGlue());

        RoundedPanel accountPanel = new RoundedPanel(new BorderLayout(10, 0),
                new Color(255, 255, 255, 20), new Color(255, 255, 255, 50), 8);
        accountPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel initial = new JLabel(cashierName.substring(0, 1).toUpperCase(Locale.ROOT), SwingConstants.CENTER);
        initial.setPreferredSize(new Dimension(38, 38));
        initial.setOpaque(true);
        initial.setBackground(new Color(255, 255, 255, 45));
        initial.setForeground(WHITE);
        initial.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel accountText = transparentPanel();
        accountText.setLayout(new BoxLayout(accountText, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(cashierName);
        nameLabel.setForeground(WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel roleLabel = new JLabel("Kasir");
        roleLabel.setForeground(MUTED);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accountText.add(nameLabel);
        accountText.add(Box.createVerticalStrut(2));
        accountText.add(roleLabel);
        accountPanel.add(initial, BorderLayout.WEST);
        accountPanel.add(accountText, BorderLayout.CENTER);

        sidebar.add(brandPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(accountPanel, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton createNavButton(String text, boolean active) {
        JButton button = new JButton(text);
        Color normalColor = active
                ? new Color(126, 78, 46)
                : new Color(117, 70, 43);
        Color hoverColor = new Color(151, 93, 58);
        Color pressedColor = new Color(83, 45, 29);

        button.setUI(new BasicButtonUI());
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(WHITE);
        button.setBackground(normalColor);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setRolloverEnabled(false);
        button.setBorder(BorderFactory.createEmptyBorder(17, 20, 17, 20));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBackground(normalColor);
            }

            @Override
            public void mousePressed(MouseEvent event) {
                button.setBackground(pressedColor);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                button.setBackground(button.contains(event.getPoint()) ? hoverColor : normalColor);
            }
        });
        return button;
    }

    private JScrollPane createMainContent() {
        JPanel content = new DashboardContentPanel();
        content.setBackground(new Color(248, 248, 247));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(32, 30, 36, 30));

        content.add(createHeader());
        content.add(Box.createVerticalStrut(16));
        content.add(createStatistics());
        content.add(Box.createVerticalStrut(18));
        content.add(createAttendanceCard());
        content.add(Box.createVerticalStrut(18));
        content.add(createSummaryCard());
        content.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getViewport().setBackground(content.getBackground());
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        return scrollPane;
    }

    private JPanel createHeader() {
        RoundedPanel header = new RoundedPanel(new BorderLayout(), BROWN_LIGHT, BROWN_DARK, 8);
        header.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        header.setPreferredSize(new Dimension(0, 152));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 152));

        JPanel text = transparentPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel eyebrow = new JLabel("CASHIER OPERASIONAL");
        eyebrow.setForeground(new Color(255, 232, 209));
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel title = new JLabel("Dashboard Kasir");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        JLabel subtitle = new JLabel("Pantau ringkasan pesanan, pembayaran, dan pendapatan kasir hari ini.");
        subtitle.setForeground(new Color(255, 235, 218));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        text.add(eyebrow);
        text.add(Box.createVerticalStrut(8));
        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(subtitle);
        header.add(text, BorderLayout.CENTER);
        return header;
    }

    private JPanel createStatistics() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(0, 82));
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));
        cards.add(new StatisticCard("Pesanan Hari Ini", ordersValue));
        cards.add(new StatisticCard("Menunggu Pembayaran", waitingValue));
        cards.add(new StatisticCard("Diproses", processingValue));
        cards.add(new StatisticCard("Pendapatan Hari Ini", revenueValue));
        return cards;
    }

    private JPanel createAttendanceCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(24, 0), BROWN_LIGHT, BROWN_DARK, 8);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 30));
        card.setPreferredSize(new Dimension(0, 162));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 162));

        JPanel textPanel = transparentPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Absensi");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        attendanceDescription.setForeground(new Color(255, 234, 216));
        attendanceDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        attendanceBadge.setOpaque(true);
        attendanceBadge.setForeground(WHITE);
        attendanceBadge.setBackground(DANGER);
        attendanceBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        attendanceBadge.setBorder(BorderFactory.createEmptyBorder(6, 11, 6, 11));
        attendanceBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(7));
        textPanel.add(attendanceDescription);
        textPanel.add(Box.createVerticalStrut(26));
        textPanel.add(attendanceBadge);

        attendanceButton.setPreferredSize(new Dimension(180, 42));
        attendanceButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        attendanceButton.setForeground(BROWN_DARK);
        attendanceButton.setBackground(CREAM);
        attendanceButton.setFocusPainted(false);
        attendanceButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        attendanceButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        attendanceButton.addActionListener(event -> recordAttendance());

        JPanel buttonPanel = transparentPanel(new FlowLayout(FlowLayout.RIGHT, 0, 38));
        buttonPanel.add(attendanceButton);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);
        return card;
    }

    private JPanel createSummaryCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 14), BROWN_LIGHT, BROWN_DARK, 8);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(0, 312));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 312));

        JLabel title = new JLabel("Ringkasan Kasir Hari Ini");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));

        JTable table = new JTable(summaryModel);
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(new Color(111, 67, 44));
        table.setBorder(null);
        table.setForeground(WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(255, 255, 255, 25));
        table.setSelectionForeground(WHITE);
        table.setFocusable(false);
        table.setRowSelectionAllowed(false);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 44));
        table.getTableHeader().setBackground(new Color(150, 99, 70));
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        table.getTableHeader().setDefaultRenderer(new SummaryHeaderRenderer());

        table.getColumnModel().getColumn(0).setPreferredWidth(760);
        table.getColumnModel().getColumn(1).setPreferredWidth(240);
        table.getColumnModel().getColumn(0).setCellRenderer(
                new SummaryCellRenderer(SwingConstants.LEFT, Font.PLAIN));
        table.getColumnModel().getColumn(1).setCellRenderer(
                new SummaryCellRenderer(SwingConstants.RIGHT, Font.BOLD));

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroll.setViewportBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        card.add(title, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);
        return card;
    }

    private void loadDashboardData(boolean showError) {
        new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                return fetchDashboardData();
            }

            @Override
            protected void done() {
                try {
                    applyDashboardData(get());
                } catch (Exception exception) {
                    if (showError && isDisplayable()) {
                        JOptionPane.showMessageDialog(DashboardKasir.this,
                                "Data dashboard tidak dapat dimuat. Pastikan MySQL dan database swiftbite aktif.",
                                "Koneksi database",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    private DashboardData fetchDashboardData() throws SQLException {
        String statsSql = "SELECT COUNT(*) AS orders_today, "
                + "COALESCE(SUM(payment_status = 'belum_dibayar'), 0) AS waiting_payment, "
                + "COALESCE(SUM(status = 'diproses'), 0) AS processing, "
                + "COALESCE(SUM(CASE WHEN payment_status = 'berhasil' "
                + "AND status = 'selesai' THEN total_harga ELSE 0 END), 0) AS revenue "
                + "FROM orders WHERE DATE(created_at) = CURDATE()";
        String attendanceSql = "SELECT jam_masuk FROM absensis "
                + "WHERE id_user = ? AND tanggal = CURDATE() LIMIT 1";

        try (Connection connection = openConnection();
                PreparedStatement statsStatement = connection.prepareStatement(statsSql);
                PreparedStatement attendanceStatement = connection.prepareStatement(attendanceSql);
                ResultSet stats = statsStatement.executeQuery()) {
            stats.next();
            attendanceStatement.setLong(1, cashierId);
            try (ResultSet attendance = attendanceStatement.executeQuery()) {
                LocalTime checkInTime = attendance.next() && attendance.getTime("jam_masuk") != null
                        ? attendance.getTime("jam_masuk").toLocalTime()
                        : null;
                return new DashboardData(
                        stats.getInt("orders_today"),
                        stats.getInt("waiting_payment"),
                        stats.getInt("processing"),
                        stats.getBigDecimal("revenue"),
                        checkInTime);
            }
        }
    }

    private void applyDashboardData(DashboardData data) {
        String revenue = formatRupiah(data.revenue());
        ordersValue.setText(String.valueOf(data.ordersToday()));
        waitingValue.setText(String.valueOf(data.waitingPayment()));
        processingValue.setText(String.valueOf(data.processing()));
        revenueValue.setText(revenue);

        summaryModel.setRowCount(0);
        summaryModel.addRow(new Object[]{"Pesanan Hari Ini", data.ordersToday()});
        summaryModel.addRow(new Object[]{"Menunggu Pembayaran", data.waitingPayment()});
        summaryModel.addRow(new Object[]{"Diproses", data.processing()});
        summaryModel.addRow(new Object[]{"Pendapatan Hari Ini", revenue});

        boolean attended = data.checkInTime() != null;
        attendanceBadge.setText(attended ? "Sudah Absen" : "Belum Absen");
        attendanceBadge.setBackground(attended ? SUCCESS : DANGER);
        attendanceDescription.setText(attended
                ? "Absen masuk tercatat pukul " + data.checkInTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "."
                : "Status absensi hari ini belum tercatat.");
        attendanceButton.setText(attended ? "Sudah Absen" : "Absen Masuk");
        attendanceButton.setEnabled(!attended);
        attendanceButton.setCursor(attended
                ? Cursor.getDefaultCursor()
                : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void recordAttendance() {
        attendanceButton.setEnabled(false);
        attendanceButton.setText("Menyimpan...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                String sql = "INSERT INTO absensis "
                        + "(id_user, tanggal, jam_masuk, status, created_at, updated_at) "
                        + "SELECT ?, CURDATE(), CURTIME(), 'hadir', NOW(), NOW() "
                        + "WHERE NOT EXISTS (SELECT 1 FROM absensis WHERE id_user = ? AND tanggal = CURDATE())";
                try (Connection connection = openConnection();
                        PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setLong(1, cashierId);
                    statement.setLong(2, cashierId);
                    return statement.executeUpdate() > 0;
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                    loadDashboardData(false);
                } catch (Exception exception) {
                    attendanceButton.setEnabled(true);
                    attendanceButton.setText("Absen Masuk");
                    JOptionPane.showMessageDialog(DashboardKasir.this,
                            "Absensi tidak dapat disimpan. Silakan periksa koneksi database.",
                            "Absensi gagal",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private String formatRupiah(BigDecimal amount) {
        BigDecimal safeAmount = amount == null ? BigDecimal.ZERO : amount;
        return "Rp" + rupiahFormat.format(safeAmount.longValue());
    }

    private static JLabel createValueLabel(String initialValue) {
        JLabel label = new JLabel(initialValue);
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 23));
        return label;
    }

    private static JPanel transparentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private static JPanel transparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private record DashboardData(
            int ordersToday,
            int waitingPayment,
            int processing,
            BigDecimal revenue,
            LocalTime checkInTime) {
    }

    private static final class SummaryHeaderRenderer extends DefaultTableCellRenderer {

        private SummaryHeaderRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(WHITE);
            setBackground(new Color(150, 99, 70));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            setHorizontalAlignment(column == 1 ? SwingConstants.RIGHT : SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            return this;
        }
    }

    private static final class SummaryCellRenderer extends DefaultTableCellRenderer {

        private final int alignment;
        private final int fontStyle;

        private SummaryCellRenderer(int alignment, int fontStyle) {
            this.alignment = alignment;
            this.fontStyle = fontStyle;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setHorizontalAlignment(alignment);
            setFont(new Font("Segoe UI", fontStyle, 14));
            setForeground(WHITE);
            setBackground(row % 2 == 0
                    ? new Color(122, 75, 48)
                    : new Color(111, 65, 43));
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            return this;
        }
    }

    private static final class StatisticCard extends RoundedPanel {

        private StatisticCard(String title, JLabel value) {
            super(new BorderLayout(), new Color(128, 77, 48), BROWN_DARK, 7);
            setBorder(BorderFactory.createEmptyBorder(15, 16, 13, 16));
            JLabel titleLabel = new JLabel(title);
            titleLabel.setForeground(MUTED);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            add(titleLabel, BorderLayout.NORTH);
            add(value, BorderLayout.SOUTH);
        }
    }

    private static class RoundedPanel extends JPanel {

        private final Color startColor;
        private final Color endColor;
        private final int radius;

        private RoundedPanel(LayoutManager layout, Color startColor, Color endColor, int radius) {
            super(layout);
            this.startColor = startColor;
            this.endColor = endColor;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(255, 255, 255, 24));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }

    private static final class DashboardContentPanel extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(visibleRect.height - 40, 40);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static final class GradientPanel extends RoundedPanel {

        private GradientPanel(LayoutManager layout, Color startColor, Color endColor, int radius) {
            super(layout, startColor, endColor, radius);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new DashboardKasir().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel designAccountPanel;
    private javax.swing.JLabel designAccountInitialLabel;
    private javax.swing.JLabel designAccountNameLabel;
    private javax.swing.JLabel designAccountRoleLabel;
    private javax.swing.JButton designAttendanceButton;
    private javax.swing.JLabel designAttendanceDescriptionLabel;
    private javax.swing.JPanel designAttendancePanel;
    private javax.swing.JLabel designAttendanceStatusLabel;
    private javax.swing.JLabel designAttendanceTitleLabel;
    private javax.swing.JLabel designBrandLabel;
    private javax.swing.JPanel designContentPanel;
    private javax.swing.JLabel designEyebrowLabel;
    private javax.swing.JPanel designHeaderPanel;
    private javax.swing.JButton designHistoryButton;
    private javax.swing.JLabel designLogoLabel;
    private javax.swing.JButton designOrdersButton;
    private javax.swing.JLabel designOrdersStatLabel;
    private javax.swing.JPanel designOrdersStatPanel;
    private javax.swing.JLabel designOrdersStatValue;
    private javax.swing.JLabel designProcessingStatLabel;
    private javax.swing.JPanel designProcessingStatPanel;
    private javax.swing.JLabel designProcessingStatValue;
    private javax.swing.JLabel designRevenueStatLabel;
    private javax.swing.JPanel designRevenueStatPanel;
    private javax.swing.JLabel designRevenueStatValue;
    private javax.swing.JPanel designRootPanel;
    private javax.swing.JPanel designSidebarPanel;
    private javax.swing.JPanel designStatsPanel;
    private javax.swing.JPanel designSummaryPanel;
    private javax.swing.JScrollPane designSummaryScrollPane;
    private javax.swing.JTable designSummaryTable;
    private javax.swing.JLabel designSummaryTitleLabel;
    private javax.swing.JLabel designSubtitleLabel;
    private javax.swing.JLabel designTitleLabel;
    private javax.swing.JLabel designWaitingStatLabel;
    private javax.swing.JPanel designWaitingStatPanel;
    private javax.swing.JLabel designWaitingStatValue;
    // End of variables declaration//GEN-END:variables
}
