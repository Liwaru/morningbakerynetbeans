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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Dashboard utama untuk user Stock Staff (level 6).
 */
public class DashboardStockStaff extends JFrame {

    private static final String DB_URL
            = "jdbc:mysql://127.0.0.1:3306/swiftbite"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final Color BACKGROUND = new Color(250, 246, 240);
    private static final Color CREAM = new Color(252, 248, 241);
    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color BROWN = new Color(91, 48, 29);
    private static final Color BROWN_DARK = new Color(58, 28, 19);
    private static final Color BROWN_MID = new Color(126, 78, 46);
    private static final Color BROWN_LIGHT = new Color(164, 104, 67);
    private static final Color MUTED = new Color(119, 98, 84);
    private static final Color SUCCESS = new Color(91, 130, 82);
    private static final Color WARNING = new Color(197, 133, 55);
    private static final Color DANGER = new Color(176, 73, 57);

    private final long stockStaffId;
    private final String stockStaffName;
    private final JLabel totalProductsValue = statValue();
    private final JLabel lowStockValue = statValue();
    private final JLabel incomingValue = statValue();
    private final JLabel outOfStockValue = statValue();
    private final JLabel attendanceBadge = new JLabel("Belum Absen");
    private final JLabel attendanceDescription = new JLabel("Status absensi hari ini belum tercatat.");
    private final JButton attendanceButton = new JButton("Absen Masuk");
    private final JLabel lastUpdatedLabel = new JLabel("Memuat data...");
    private final DefaultTableModel restockModel = readOnlyModel(
            new String[]{"BARCODE", "NAMA PRODUK", "KATEGORI", "STOK", "STATUS"});
    private final JPanel activityList = new JPanel();
    private final Timer refreshTimer;

    public DashboardStockStaff() {
        this(9L, "stockstaff");
    }

    public DashboardStockStaff(long stockStaffId, String stockStaffName) {
        this.stockStaffId = stockStaffId;
        this.stockStaffName = stockStaffName == null || stockStaffName.isBlank()
                ? "stockstaff" : stockStaffName;

        initComponents();
        configureDashboard();
        setSize(1366, 760);
        setLocationRelativeTo(null);
        loadWindowIcon();

        refreshTimer = new Timer(30_000, event -> loadDashboardData(false));
        refreshTimer.start();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                refreshTimer.stop();
            }
        });
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
        setTitle("SwiftBite - Dashboard Stock Staff");
        setMinimumSize(new java.awt.Dimension(1080, 680));

        designRootPanel.setBackground(new java.awt.Color(248, 248, 247));

        designSidebarPanel.setBackground(new java.awt.Color(91, 48, 29));
        designSidebarPanel.setPreferredSize(new java.awt.Dimension(250, 768));

        designLogoLabel.setBackground(new java.awt.Color(255, 255, 255));
        designLogoLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        designLogoLabel.setForeground(new java.awt.Color(91, 48, 29));
        designLogoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        designLogoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/morning_bakery/assets/Swiftbite.png"))); // NOI18N
        designLogoLabel.setText("");
        designLogoLabel.setOpaque(true);

        designBrandLabel.setFont(new java.awt.Font("Segoe UI", 1, 23)); // NOI18N
        designBrandLabel.setForeground(new java.awt.Color(255, 255, 255));
        designBrandLabel.setText("SwiftBite");

        designOrdersButton.setBackground(new java.awt.Color(126, 78, 46));
        designOrdersButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        designOrdersButton.setForeground(new java.awt.Color(255, 255, 255));
        designOrdersButton.setText("Dashboard");
        designOrdersButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 18, 12, 18));
        designOrdersButton.setFocusPainted(false);
        designOrdersButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        designHistoryButton.setBackground(new java.awt.Color(117, 70, 43));
        designHistoryButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        designHistoryButton.setForeground(new java.awt.Color(255, 255, 255));
        designHistoryButton.setText("Kelola Stok");
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
        designAccountNameLabel.setText("stockstaff");

        designAccountRoleLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        designAccountRoleLabel.setForeground(new java.awt.Color(242, 218, 197));
        designAccountRoleLabel.setText("Stock Staff");

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
        designEyebrowLabel.setText("STOCK STAFF OPERASIONAL");

        designTitleLabel.setFont(new java.awt.Font("Segoe UI", 1, 40)); // NOI18N
        designTitleLabel.setForeground(new java.awt.Color(255, 255, 255));
        designTitleLabel.setText("Dashboard Stock Staff");

        designSubtitleLabel.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        designSubtitleLabel.setForeground(new java.awt.Color(255, 235, 218));
        designSubtitleLabel.setText("Pantau persediaan produk dan kebutuhan restok.");

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

        configureDesignStatPanel(designOrdersStatPanel, designOrdersStatLabel, designOrdersStatValue, "Total Produk", "0");
        designStatsPanel.add(designOrdersStatPanel);
        configureDesignStatPanel(designWaitingStatPanel, designWaitingStatLabel, designWaitingStatValue, "Stok Menipis", "0");
        designStatsPanel.add(designWaitingStatPanel);
        configureDesignStatPanel(designProcessingStatPanel, designProcessingStatLabel, designProcessingStatValue, "Barang Masuk Hari Ini", "0");
        designStatsPanel.add(designProcessingStatPanel);
        configureDesignStatPanel(designRevenueStatPanel, designRevenueStatLabel, designRevenueStatValue, "Stok Habis", "0");
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
        designSummaryTitleLabel.setText("Produk yang Perlu Direstok");

        designSummaryTable.setBackground(new java.awt.Color(111, 67, 44));
        designSummaryTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        designSummaryTable.setForeground(new java.awt.Color(255, 255, 255));
        designSummaryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Total Produk", "0"},
                {"Stok Menipis", "0"},
                {"Barang Masuk Hari Ini", "0"},
                {"Stok Habis", "Rp0"}
            },
            new String [] {"BARCODE / PRODUK", "STOK / STATUS"}
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
        designRootPanel.removeAll();
        designRootPanel.setBackground(BACKGROUND);
        designRootPanel.setLayout(new BorderLayout());
        designRootPanel.add(createSidebar(), BorderLayout.WEST);
        designRootPanel.add(createContent(), BorderLayout.CENTER);
        designRootPanel.revalidate();
        designRootPanel.repaint();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new GradientPanel(new Color(169, 108, 69), BROWN_DARK, 0);
        sidebar.setPreferredSize(new Dimension(245, 720));
        sidebar.setBorder(BorderFactory.createEmptyBorder(26, 18, 24, 18));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brand.setOpaque(false);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        JLabel logo = new JLabel("S", SwingConstants.CENTER);
        logo.setOpaque(true);
        logo.setBackground(WHITE);
        logo.setForeground(BROWN);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 23));
        logo.setPreferredSize(new Dimension(48, 48));
        URL logoUrl = getClass().getResource("/morning_bakery/assets/Swiftbite.png");
        if (logoUrl != null) {
            Image image = new ImageIcon(logoUrl).getImage()
                    .getScaledInstance(38, 38, Image.SCALE_SMOOTH);
            logo.setText("");
            logo.setIcon(new ImageIcon(image));
        }
        JLabel brandName = new JLabel("  SwiftBite");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandName.setForeground(WHITE);
        brand.add(logo);
        brand.add(brandName);

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(42));
        sidebar.add(navButton("Dashboard", true));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navButton("Kelola Stok", false));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navButton("Barang Masuk", false));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navButton("Riwayat Stok", false));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createAccountPanel());
        return sidebar;
    }

    private JButton navButton(String text, boolean selected) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setPreferredSize(new Dimension(209, 50));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        button.setBackground(selected ? BROWN_LIGHT : CREAM);
        button.setForeground(selected ? WHITE : BROWN_DARK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createAccountPanel() {
        JPanel account = new JPanel(new BorderLayout(10, 0));
        account.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        account.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        account.setBackground(new Color(74, 43, 32));

        JLabel initial = new JLabel(
                stockStaffName.substring(0, 1).toUpperCase(), SwingConstants.CENTER);
        initial.setOpaque(true);
        initial.setBackground(new Color(116, 82, 65));
        initial.setForeground(WHITE);
        initial.setFont(new Font("Segoe UI", Font.BOLD, 18));
        initial.setPreferredSize(new Dimension(40, 40));

        JPanel identity = new JPanel();
        identity.setOpaque(false);
        identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(stockStaffName);
        name.setForeground(WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel role = new JLabel("Stock Staff");
        role.setForeground(new Color(242, 218, 197));
        role.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        identity.add(Box.createVerticalGlue());
        identity.add(name);
        identity.add(role);
        identity.add(Box.createVerticalGlue());
        account.add(initial, BorderLayout.WEST);
        account.add(identity, BorderLayout.CENTER);
        return account;
    }

    private JScrollPane createContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(26, 30, 30, 30));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        content.add(createHeader(), constraints);

        constraints.gridy++;
        constraints.insets = new Insets(22, 0, 0, 0);
        content.add(createStatistics(), constraints);

        constraints.gridy++;
        constraints.insets = new Insets(20, 0, 0, 0);
        content.add(createAttendanceCard(), constraints);

        constraints.gridy++;
        constraints.insets = new Insets(20, 0, 0, 0);
        content.add(createRestockPanel(), constraints);

        constraints.gridy++;
        constraints.insets = new Insets(20, 0, 0, 0);
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        content.add(createActivityPanel(), constraints);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        return scrollPane;
    }

    private JPanel createHeader() {
        JPanel header = new GradientPanel(BROWN_LIGHT, BROWN_DARK, 10);
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        header.setPreferredSize(new Dimension(0, 145));
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel eyebrow = new JLabel("STOCK STAFF OPERASIONAL");
        eyebrow.setForeground(new Color(255, 232, 209));
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel title = new JLabel("Dashboard Stock Staff");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 38));
        JLabel subtitle = new JLabel("Pantau persediaan produk dan kebutuhan restok.");
        subtitle.setForeground(new Color(255, 235, 218));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titles.add(eyebrow);
        titles.add(Box.createVerticalStrut(3));
        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(subtitle);

        lastUpdatedLabel.setForeground(new Color(255, 235, 218));
        lastUpdatedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        header.add(titles, BorderLayout.WEST);
        header.add(lastUpdatedLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel createStatistics() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);
        cards.add(statCard("Total Produk", totalProductsValue, BROWN_LIGHT));
        cards.add(statCard("Stok Menipis", lowStockValue, WARNING));
        cards.add(statCard("Barang Masuk Hari Ini", incomingValue, new Color(104, 132, 91)));
        cards.add(statCard("Stok Habis", outOfStockValue, DANGER));
        return cards;
    }

    private JPanel statCard(String title, JLabel value, Color accent) {
        RoundedPanel card = new RoundedPanel(10, BROWN_DARK);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        JLabel caption = new JLabel(title);
        caption.setForeground(new Color(248, 224, 204));
        caption.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JPanel marker = new JPanel();
        marker.setBackground(accent);
        marker.setPreferredSize(new Dimension(5, 1));
        value.setForeground(WHITE);
        card.add(marker, BorderLayout.WEST);
        card.add(caption, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JPanel createRestockPanel() {
        JPanel panel = new GradientPanel(BROWN_LIGHT, BROWN_DARK, 10);
        panel.setLayout(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        panel.add(sectionHeader("Produk yang Perlu Direstok",
                "Produk dengan stok 5 atau kurang", true), BorderLayout.NORTH);

        JTable table = new JTable(restockModel);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(WHITE);
        table.setBackground(new Color(111, 67, 44));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(151, 99, 70));
        table.setSelectionForeground(WHITE);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(151, 99, 70));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setBackground(new Color(150, 99, 70));
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer padded = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable renderedTable, Object value, boolean selected,
                    boolean focused, int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        renderedTable, value, selected, focused, row, column);
                if (!selected) {
                    component.setBackground(row % 2 == 0
                            ? new Color(125, 75, 47) : new Color(111, 67, 44));
                    component.setForeground(WHITE);
                }
                return component;
            }
        };
        padded.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        table.setDefaultRenderer(Object.class, padded);
        table.getColumnModel().getColumn(0).setPreferredWidth(140);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(65);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(177, 126, 94)));
        tableScroll.getViewport().setBackground(new Color(111, 67, 44));
        tableScroll.setPreferredSize(new Dimension(700, 190));
        panel.add(tableScroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAttendanceCard() {
        RoundedPanel card = new RoundedPanel(18, BROWN_MID);
        card.setLayout(new BorderLayout(24, 0));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 30));
        card.setPreferredSize(new Dimension(0, 145));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Absensi");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        attendanceDescription.setForeground(new Color(248, 229, 211));
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
        textPanel.add(Box.createVerticalStrut(20));
        textPanel.add(attendanceBadge);

        attendanceButton.setPreferredSize(new Dimension(180, 42));
        attendanceButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        attendanceButton.setForeground(BROWN_DARK);
        attendanceButton.setBackground(CREAM);
        attendanceButton.setFocusPainted(false);
        attendanceButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        attendanceButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        attendanceButton.addActionListener(event -> recordAttendance());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 31));
        buttonPanel.setOpaque(false);
        buttonPanel.add(attendanceButton);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);
        return card;
    }

    private JPanel createActivityPanel() {
        RoundedPanel panel = new RoundedPanel(18, WHITE);
        panel.setLayout(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        panel.add(sectionHeader("Aktivitas Terbaru",
                "Perubahan stok terbaru oleh petugas"), BorderLayout.NORTH);
        activityList.setOpaque(false);
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.add(activityRow("--:--", "Belum ada aktivitas stok."));
        panel.add(activityList, BorderLayout.CENTER);
        return panel;
    }

    private JPanel sectionHeader(String title, String description) {
        return sectionHeader(title, description, false);
    }

    private JPanel sectionHeader(String title, String description, boolean onDarkBackground) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(onDarkBackground ? WHITE : BROWN_DARK);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setForeground(onDarkBackground
                ? new Color(255, 235, 218) : MUTED);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        header.add(titleLabel, BorderLayout.WEST);
        header.add(descriptionLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel activityRow(String time, String description) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(236, 230, 225)));
        JLabel timeLabel = new JLabel(time);
        timeLabel.setPreferredSize(new Dimension(55, 36));
        timeLabel.setForeground(BROWN_LIGHT);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel activityLabel = new JLabel(description);
        activityLabel.setForeground(BROWN_DARK);
        activityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        row.add(timeLabel, BorderLayout.WEST);
        row.add(activityLabel, BorderLayout.CENTER);
        return row;
    }

    private void loadDashboardData(boolean showError) {
        new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                try (Connection connection = openConnection()) {
                    int totalProducts = scalar(connection,
                            "SELECT COUNT(*) FROM menus");
                    int lowStock = scalar(connection,
                            "SELECT COUNT(*) FROM menus WHERE stok BETWEEN 1 AND 5");
                    int incoming = scalar(connection,
                            "SELECT COUNT(*) FROM ingredient_ins WHERE DATE(created_at)=CURDATE()");
                    int outOfStock = scalar(connection,
                            "SELECT COUNT(*) FROM menus WHERE stok<=0 OR status='habis'");
                    LocalTime checkInTime = null;
                    try (PreparedStatement statement = connection.prepareStatement(
                            "SELECT jam_masuk FROM absensis "
                            + "WHERE id_user=? AND tanggal=CURDATE() LIMIT 1")) {
                        statement.setLong(1, stockStaffId);
                        try (ResultSet result = statement.executeQuery()) {
                            if (result.next() && result.getTime("jam_masuk") != null) {
                                checkInTime = result.getTime("jam_masuk").toLocalTime();
                            }
                        }
                    }

                    java.util.List<Object[]> products = new java.util.ArrayList<>();
                    String productSql = "SELECT COALESCE(m.barcode,'-') barcode,m.nama_menu,"
                            + "c.nama_kategori,m.stok,"
                            + "CASE WHEN m.stok<=0 OR m.status='habis' THEN 'Habis' "
                            + "ELSE 'Menipis' END status_stok "
                            + "FROM menus m JOIN categories c ON c.id_kategori=m.id_kategori "
                            + "WHERE m.stok<=5 OR m.status='habis' "
                            + "ORDER BY m.stok ASC,m.nama_menu ASC LIMIT 8";
                    try (PreparedStatement statement = connection.prepareStatement(productSql);
                            ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            products.add(new Object[]{
                                result.getString("barcode"),
                                result.getString("nama_menu"),
                                result.getString("nama_kategori"),
                                result.getInt("stok"),
                                result.getString("status_stok")
                            });
                        }
                    }

                    java.util.List<Activity> activities = new java.util.ArrayList<>();
                    String activitySql = "SELECT activity,created_at FROM activity_logs "
                            + "WHERE LOWER(role) IN ('stockstaff','stock staff') "
                            + "OR LOWER(activity) LIKE '%stok%' "
                            + "ORDER BY created_at DESC LIMIT 5";
                    try (PreparedStatement statement = connection.prepareStatement(activitySql);
                            ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            String time = result.getTimestamp("created_at") == null
                                    ? "--:--"
                                    : result.getTimestamp("created_at").toLocalDateTime()
                                            .format(DateTimeFormatter.ofPattern("HH:mm"));
                            activities.add(new Activity(time, result.getString("activity")));
                        }
                    }
                    return new DashboardData(totalProducts, lowStock, incoming,
                            outOfStock, checkInTime, products, activities);
                }
            }

            @Override
            protected void done() {
                try {
                    applyDashboardData(get());
                } catch (Exception exception) {
                    lastUpdatedLabel.setText("Data gagal dimuat");
                    if (showError) {
                        JOptionPane.showMessageDialog(DashboardStockStaff.this,
                                "Dashboard tidak dapat memuat data.\n"
                                + "Pastikan MySQL dan database swiftbite aktif.",
                                "Koneksi Database", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    private void applyDashboardData(DashboardData data) {
        totalProductsValue.setText(String.valueOf(data.totalProducts()));
        lowStockValue.setText(String.valueOf(data.lowStock()));
        incomingValue.setText(String.valueOf(data.incoming()));
        outOfStockValue.setText(String.valueOf(data.outOfStock()));
        boolean attended = data.checkInTime() != null;
        attendanceBadge.setText(attended ? "Sudah Absen" : "Belum Absen");
        attendanceBadge.setBackground(attended ? SUCCESS : DANGER);
        attendanceDescription.setText(attended
                ? "Absen masuk tercatat pukul "
                    + data.checkInTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "."
                : "Status absensi hari ini belum tercatat.");
        attendanceButton.setText(attended ? "Sudah Absen" : "Absen Masuk");
        attendanceButton.setEnabled(!attended);
        attendanceButton.setCursor(attended
                ? Cursor.getDefaultCursor()
                : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        restockModel.setRowCount(0);
        for (Object[] product : data.products()) {
            restockModel.addRow(product);
        }
        if (data.products().isEmpty()) {
            restockModel.addRow(new Object[]{"-", "Semua stok produk mencukupi", "-", "-", "Aman"});
        }

        activityList.removeAll();
        if (data.activities().isEmpty()) {
            activityList.add(activityRow("--:--", "Belum ada aktivitas stok."));
        } else {
            for (Activity activity : data.activities()) {
                activityList.add(activityRow(activity.time(), activity.description()));
            }
        }
        activityList.revalidate();
        activityList.repaint();
        lastUpdatedLabel.setText("Diperbarui "
                + java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private int scalar(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt(1) : 0;
        }
    }

    private void recordAttendance() {
        attendanceButton.setEnabled(false);
        attendanceButton.setText("Menyimpan...");
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                String sql = "INSERT INTO absensis "
                        + "(id_user,tanggal,jam_masuk,status,created_at,updated_at) "
                        + "SELECT ?,CURDATE(),CURTIME(),'hadir',NOW(),NOW() "
                        + "WHERE NOT EXISTS (SELECT 1 FROM absensis "
                        + "WHERE id_user=? AND tanggal=CURDATE())";
                try (Connection connection = openConnection();
                        PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setLong(1, stockStaffId);
                    statement.setLong(2, stockStaffId);
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
                    JOptionPane.showMessageDialog(DashboardStockStaff.this,
                            "Absensi tidak dapat disimpan. Silakan periksa koneksi database.",
                            "Absensi gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void loadWindowIcon() {
        URL logoUrl = getClass().getResource("/morning_bakery/assets/Swiftbite.png");
        if (logoUrl != null) {
            setIconImage(new ImageIcon(logoUrl).getImage());
        }
    }

    private static JLabel statValue() {
        JLabel label = new JLabel("0");
        label.setForeground(BROWN_DARK);
        label.setFont(new Font("Segoe UI", Font.BOLD, 30));
        label.setBorder(BorderFactory.createEmptyBorder(12, 15, 0, 0));
        return label;
    }

    private static DefaultTableModel readOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private record Activity(String time, String description) {
    }

    private record DashboardData(int totalProducts, int lowStock, int incoming,
            int outOfStock, LocalTime checkInTime, java.util.List<Object[]> products,
            java.util.List<Activity> activities) {
    }

    private static final class RoundedPanel extends JPanel {

        private final int radius;
        private final Color fill;

        RoundedPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(new Color(0, 0, 0, 16));
            graphics2D.fillRoundRect(1, 3, getWidth() - 3, getHeight() - 4, radius, radius);
            graphics2D.setColor(fill);
            graphics2D.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 4, radius, radius);
            graphics2D.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class GradientPanel extends JPanel {

        private final Color startColor;
        private final Color endColor;
        private final int radius;

        GradientPanel(Color startColor, Color endColor, int radius) {
            this.startColor = startColor;
            this.endColor = endColor;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setPaint(new GradientPaint(
                    0, 0, startColor, getWidth(), getHeight(), endColor));
            graphics2D.fillRoundRect(
                    0, 0, getWidth(), getHeight(), radius, radius);
            graphics2D.dispose();
            super.paintComponent(graphics);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new DashboardStockStaff().setVisible(true));
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

