package morning_bakery;

import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.CardLayout;
import morning_bakery.owner.OwnerDashboardPanel;
import morning_bakery.owner.OwnerSalaryPanel;
import morning_bakery.owner.RoundedGradientPanel;
import morning_bakery.sales.LaporanPenjualanPanel;

/** JFrame utama Owner. Semua menu memakai JPanel yang dipertukarkan CardLayout. */
public final class DashboardOwner extends JFrame {

    private static final String REPORT_CARD = "OWNER_DASHBOARD";
    private static final String SALES_CARD = "OWNER_SALES_REPORT";
    private static final String SALARY_CARD = "OWNER_SALARY";
    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color CREAM = new Color(255, 247, 235);
    private static final Color BROWN_DARK = new Color(58, 28, 19);
    private static final Color BROWN = new Color(91, 48, 29);
    private static final Color BROWN_MID = new Color(132, 79, 47);
    private static final Color BROWN_LIGHT = new Color(166, 105, 67);
    private static final Color MUTED = new Color(235, 205, 184);
    private static final Color NAV_NORMAL = new Color(103, 59, 39);
    private static final Color NAV_ACTIVE = new Color(137, 82, 51);

    private final long ownerId;
    private final String ownerName;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final OwnerDashboardPanel dashboardPanel
            = new OwnerDashboardPanel();
    private final LaporanPenjualanPanel salesReportPanel
            = new LaporanPenjualanPanel();
    private final OwnerSalaryPanel salaryPanel = new OwnerSalaryPanel();
    private RoundedGradientPanel sidebar;
    private JButton salesReportButton;
    private JButton financeReportButton;
    private JButton productReportButton;
    private JButton salaryButton;

    public DashboardOwner() {
        this(4L, "owner");
    }

    public DashboardOwner(long ownerId, String ownerName) {
        this.ownerId = ownerId;
        this.ownerName = ownerName == null || ownerName.isBlank()
                ? "owner" : ownerName;
        setTitle("SwiftBite - Dashboard Owner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 680));
        setSize(1366, 768);
        setLocationRelativeTo(null);
        loadWindowIcon();
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(249, 247, 244));
        sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);

        contentPanel.setBackground(new Color(249, 247, 244));
        contentPanel.add(dashboardPanel, REPORT_CARD);
        contentPanel.add(salesReportPanel, SALES_CARD);
        contentPanel.add(salaryPanel, SALARY_CARD);
        root.add(contentPanel, BorderLayout.CENTER);

        setContentPane(root);
        showDashboard();
    }

    private RoundedGradientPanel createSidebar() {
        RoundedGradientPanel panel = new RoundedGradientPanel(
                new BorderLayout(),
                BROWN_LIGHT,
                BROWN_DARK,
                0);
        panel.setPreferredSize(new Dimension(260, 720));
        panel.setBorder(new EmptyBorder(26, 18, 24, 18));

        JPanel brand = transparentPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel brandLogo = new JLabel("", SwingConstants.CENTER);
        brandLogo.setPreferredSize(new Dimension(48, 48));
        brandLogo.setOpaque(true);
        brandLogo.setBackground(WHITE);
        URL logoUrl = getClass().getResource(
                "/morning_bakery/assets/Swiftbite.png");
        if (logoUrl != null) {
            Image image = new ImageIcon(logoUrl).getImage()
                    .getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            brandLogo.setIcon(new ImageIcon(image));
        }
        JLabel brandName = new JLabel("SwiftBite");
        brandName.setForeground(WHITE);
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brand.add(brandLogo);
        brand.add(brandName);
        brand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseAdapter dashboardLink = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                showDashboard();
            }
        };
        brand.addMouseListener(dashboardLink);
        brandLogo.addMouseListener(dashboardLink);
        brandName.addMouseListener(dashboardLink);

        JPanel menu = transparentPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(22, 0, 0, 0));
        salesReportButton = createNavButton(
                "Laporan Penjualan", SidebarIcon.SALES);
        financeReportButton = createNavButton(
                "Laporan Keuangan", SidebarIcon.FINANCE);
        productReportButton = createNavButton(
                "Laporan Produk", SidebarIcon.PRODUCT);
        salaryButton = createNavButton("Gaji", SidebarIcon.SALARY);
        salesReportButton.addActionListener(event -> showSalesReport());
        financeReportButton.addActionListener(
                event -> showReport(financeReportButton));
        productReportButton.addActionListener(
                event -> showReport(productReportButton));
        salaryButton.addActionListener(event -> showSalary());
        menu.add(salesReportButton);
        menu.add(Box.createVerticalStrut(10));
        menu.add(financeReportButton);
        menu.add(Box.createVerticalStrut(10));
        menu.add(productReportButton);
        menu.add(Box.createVerticalStrut(10));
        menu.add(salaryButton);
        menu.add(Box.createVerticalGlue());

        JPanel account = createAccountCard();
        panel.add(brand, BorderLayout.NORTH);
        panel.add(menu, BorderLayout.CENTER);
        panel.add(account, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createNavButton(String text, int iconType) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMinimumSize(new Dimension(0, 56));
        button.setPreferredSize(new Dimension(224, 56));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIcon(new SidebarIcon(iconType));
        button.setIconTextGap(12);
        button.setBorder(new EmptyBorder(0, 11, 0, 11));
        button.setBackground(NAV_NORMAL);
        button.setForeground(WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setRolloverEnabled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createAccountCard() {
        RoundedGradientPanel card = new RoundedGradientPanel(
                new BorderLayout(10, 0),
                new Color(255, 255, 255, 34),
                new Color(255, 255, 255, 20),
                10);
        card.setPreferredSize(new Dimension(224, 67));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 55)),
                new EmptyBorder(10, 11, 10, 11)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel initial = new JLabel(
                ownerName.substring(0, 1).toUpperCase(),
                SwingConstants.CENTER);
        initial.setPreferredSize(new Dimension(40, 40));
        initial.setOpaque(true);
        initial.setBackground(new Color(255, 255, 255, 45));
        initial.setForeground(WHITE);
        initial.setFont(new Font("Segoe UI", Font.BOLD, 17));

        JPanel accountIdentity = transparentPanel();
        accountIdentity.setLayout(new BoxLayout(accountIdentity, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(ownerName);
        name.setForeground(WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel role = new JLabel("Owner");
        role.setForeground(MUTED);
        role.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accountIdentity.add(name);
        accountIdentity.add(role);
        card.add(initial, BorderLayout.WEST);
        card.add(accountIdentity, BorderLayout.CENTER);

        JPopupMenu popup = new JPopupMenu();
        popup.setLayout(new BorderLayout());
        popup.setPreferredSize(new Dimension(224, 50));
        popup.setBorder(BorderFactory.createLineBorder(
                new Color(255, 255, 255, 75)));
        JButton logout = new JButton("Logout");
        logout.setUI(new BasicButtonUI());
        logout.setBackground(CREAM);
        logout.setForeground(BROWN_DARK);
        logout.setHorizontalAlignment(SwingConstants.LEFT);
        logout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logout.setBorder(new EmptyBorder(0, 16, 0, 16));
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(event -> {
            popup.setVisible(false);
            dispose();
            new login().setVisible(true);
        });
        popup.add(logout, BorderLayout.CENTER);

        MouseAdapter popupListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (popup.isVisible()) {
                    popup.setVisible(false);
                } else {
                    popup.show(card, 0,
                            -popup.getPreferredSize().height - 5);
                }
            }
        };
        card.addMouseListener(popupListener);
        initial.addMouseListener(popupListener);
        accountIdentity.addMouseListener(popupListener);
        return card;
    }

    private void showReport(JButton activeButton) {
        cardLayout.show(contentPanel, REPORT_CARD);
        setActiveMenu(activeButton);
        dashboardPanel.refreshDashboard();
    }

    private void showDashboard() {
        cardLayout.show(contentPanel, REPORT_CARD);
        setActiveMenu(null);
        dashboardPanel.refreshDashboard();
    }

    private void showSalesReport() {
        cardLayout.show(contentPanel, SALES_CARD);
        setActiveMenu(salesReportButton);
        salesReportPanel.refreshReport();
    }

    private void showSalary() {
        cardLayout.show(contentPanel, SALARY_CARD);
        setActiveMenu(salaryButton);
        salaryPanel.refreshData();
    }

    private void setActiveMenu(JButton activeButton) {
        for (JButton button : new JButton[]{
            salesReportButton, financeReportButton, productReportButton,
            salaryButton
        }) {
            boolean active = button == activeButton;
            button.setBackground(active ? NAV_ACTIVE : NAV_NORMAL);
            button.setForeground(WHITE);
        }
    }

    private void loadWindowIcon() {
        URL url = getClass().getResource(
                "/morning_bakery/assets/Swiftbite-icon.png");
        if (url != null) {
            setIconImage(new ImageIcon(url).getImage());
        }
    }

    private static JPanel transparentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private static JPanel transparentPanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private static final class SidebarIcon implements Icon {

        private static final int SALES = 0;
        private static final int FINANCE = 1;
        private static final int PRODUCT = 2;
        private static final int SALARY = 3;
        private final int type;

        private SidebarIcon(int type) {
            this.type = type;
        }

        @Override
        public int getIconWidth() {
            return 34;
        }

        @Override
        public int getIconHeight() {
            return 34;
        }

        @Override
        public void paintIcon(
                Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(255, 245, 232));
            g.setStroke(new BasicStroke(
                    1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawRoundRect(x + 1, y + 1, 31, 31, 7, 7);

            switch (type) {
                case SALES -> {
                    g.drawLine(x + 9, y + 24, x + 9, y + 17);
                    g.drawLine(x + 14, y + 24, x + 14, y + 12);
                    g.drawLine(x + 19, y + 24, x + 19, y + 15);
                    g.drawLine(x + 24, y + 24, x + 24, y + 9);
                    g.drawLine(x + 7, y + 25, x + 26, y + 25);
                }
                case FINANCE -> {
                    g.drawRoundRect(x + 8, y + 12, 18, 13, 2, 2);
                    g.drawLine(x + 8, y + 16, x + 26, y + 16);
                    g.drawRect(x + 14, y + 9, 6, 3);
                    g.drawLine(x + 15, y + 20, x + 19, y + 20);
                }
                case PRODUCT -> {
                    g.drawRoundRect(x + 10, y + 7, 14, 20, 2, 2);
                    g.drawLine(x + 13, y + 12, x + 21, y + 12);
                    g.drawLine(x + 13, y + 16, x + 21, y + 16);
                    g.drawLine(x + 13, y + 20, x + 19, y + 20);
                }
                default -> {
                    g.drawRoundRect(x + 8, y + 12, 18, 13, 2, 2);
                    g.drawRect(x + 13, y + 9, 8, 3);
                    g.drawLine(x + 12, y + 17, x + 22, y + 17);
                    g.drawLine(x + 12, y + 21, x + 18, y + 21);
                }
            }
            g.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(
                () -> new DashboardOwner().setVisible(true));
    }
}
