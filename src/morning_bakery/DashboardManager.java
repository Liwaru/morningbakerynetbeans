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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Dashboard operasional untuk akun Manager.
 */
public class DashboardManager extends JFrame {

    private static final String DB_URL
            = "jdbc:mysql://127.0.0.1:3306/swiftbite"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final Color PAGE = new Color(249, 247, 244);
    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color CREAM = new Color(255, 242, 225);
    private static final Color BROWN_DARK = new Color(58, 28, 19);
    private static final Color BROWN = new Color(91, 48, 29);
    private static final Color BROWN_MID = new Color(132, 79, 47);
    private static final Color BROWN_LIGHT = new Color(166, 105, 67);

    private final long managerId;
    private final String managerName;
    private final JLabel totalMenuValue = statValue();
    private final JLabel totalTableValue = statValue();
    private final JLabel todayOrdersValue = statValue();
    private final JLabel activeUsersValue = statValue();
    private final JLabel lastUpdated = new JLabel("Memuat data...");
    private final JLabel waitingValue = summaryValue();
    private final JLabel processingValue = summaryValue();
    private final JLabel readyValue = summaryValue();
    private final JLabel paymentValue = summaryValue();
    private final JLabel completeValue = summaryValue();
    private final Timer refreshTimer;

    public DashboardManager() {
        this(3L, "manager");
    }

    public DashboardManager(long managerId, String managerName) {
        this.managerId = managerId;
        this.managerName = managerName == null || managerName.isBlank()
                ? "manager" : managerName;
        setTitle("SwiftBite - Dashboard Manager");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1060, 680));
        setSize(1366, 768);
        setLocationRelativeTo(null);
        loadWindowIcon();
        setContentPane(createRoot());
        refreshTimer = new Timer(30_000, event -> loadDashboard(false));
        refreshTimer.start();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent event) {
                refreshTimer.stop();
            }
        });
        loadDashboard(true);
    }

    private JPanel createRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE);
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createContent(), BorderLayout.CENTER);
        return root;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new GradientPanel(BROWN_LIGHT, BROWN_DARK);
        sidebar.setPreferredSize(new Dimension(260, 720));
        sidebar.setBorder(new EmptyBorder(26, 18, 24, 18));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        JLabel logo = new JLabel("S", SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(48, 48));
        logo.setOpaque(true);
        logo.setBackground(WHITE);
        URL logoUrl = getClass().getResource("/morning_bakery/assets/Swiftbite.png");
        if (logoUrl != null) {
            Image image = new ImageIcon(logoUrl).getImage()
                    .getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            logo.setText("");
            logo.setIcon(new ImageIcon(image));
        } else {
            logo.setText("S");
            logo.setForeground(BROWN);
            logo.setFont(new Font("Serif", Font.BOLD, 27));
        }
        JLabel brandName = new JLabel("SwiftBite");
        brandName.setForeground(WHITE);
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brand.add(logo);
        brand.add(brandName);
        MouseAdapter refreshLink = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                loadDashboard(false);
            }
        };
        brand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brandName.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brand.addMouseListener(refreshLink);
        logo.addMouseListener(refreshLink);
        brandName.addMouseListener(refreshLink);

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(42));
        JButton users = navButton("Data User");
        users.addActionListener(event -> {
            new DataUser(managerId, managerName).setVisible(true);
            dispose();
        });
        sidebar.add(users);
        sidebar.add(Box.createVerticalStrut(10));
        JButton attendance = navButton("Data Absensi");
        attendance.addActionListener(event -> {
            new DataAbsensi(managerId, managerName).setVisible(true);
            dispose();
        });
        sidebar.add(attendance);
        sidebar.add(Box.createVerticalStrut(10));
        JButton stock = navButton("Stok Produk");
        stock.addActionListener(event -> {
            new KelolaStok(managerId, managerName, "Manager", true).setVisible(true);
            dispose();
        });
        sidebar.add(stock);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createAccountCard());
        return sidebar;
    }

    private JButton navButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMinimumSize(new Dimension(224, 56));
        button.setPreferredSize(new Dimension(224, 56));
        button.setMaximumSize(new Dimension(224, 56));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(0, 18, 0, 18));
        Color normal = new Color(117, 70, 43);
        button.setBackground(normal);
        button.setForeground(WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setRolloverEnabled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setBackground(new Color(151, 93, 58));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                button.setBackground(normal);
            }
        });
        return button;
    }

    private JPanel createAccountCard() {
        RoundedPanel card = new RoundedPanel(9, new Color(255, 255, 255, 30));
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 11, 11));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setPreferredSize(new Dimension(224, 67));
        card.setMaximumSize(new Dimension(224, 67));
        card.setBorder(BorderFactory.createLineBorder(
                new Color(255, 255, 255, 55)));
        JLabel initial = new JLabel(
                managerName.substring(0, 1).toUpperCase(), SwingConstants.CENTER);
        initial.setPreferredSize(new Dimension(40, 40));
        initial.setOpaque(true);
        initial.setBackground(new Color(255, 255, 255, 45));
        initial.setForeground(WHITE);
        initial.setFont(new Font("Segoe UI", Font.BOLD, 17));
        JPanel identity = new JPanel();
        identity.setOpaque(false);
        identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(managerName);
        name.setForeground(WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel role = new JLabel("Manager");
        role.setForeground(new Color(242, 218, 197));
        role.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        identity.add(name);
        identity.add(role);
        card.add(initial);
        card.add(identity);

        JPopupMenu menu = new JPopupMenu();
        menu.setOpaque(false);
        menu.setBorder(new EmptyBorder(3, 3, 3, 3));
        RoundedPanel logoutPanel = new RoundedPanel(8, WHITE);
        logoutPanel.setLayout(new BorderLayout());
        logoutPanel.setPreferredSize(new Dimension(218, 42));
        JButton logout = new JButton("Logout");
        logout.setUI(new BasicButtonUI());
        logout.setHorizontalAlignment(SwingConstants.LEFT);
        logout.setBorder(new EmptyBorder(8, 18, 8, 18));
        logout.setBackground(WHITE);
        logout.setForeground(BROWN_DARK);
        logout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logout.setFocusPainted(false);
        logout.setBorderPainted(false);
        logout.setRolloverEnabled(false);
        logout.setContentAreaFilled(true);
        logout.setOpaque(true);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                logout.setBackground(new Color(245, 235, 225));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                logout.setBackground(WHITE);
            }
        });
        logout.addActionListener(event -> {
            menu.setVisible(false);
            dispose();
            new login().setVisible(true);
        });
        logoutPanel.add(logout, BorderLayout.CENTER);
        menu.add(logoutPanel);
        MouseAdapter popup = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                menu.show(card, 0, -menu.getPreferredSize().height - 4);
            }
        };
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        initial.addMouseListener(popup);
        name.addMouseListener(popup);
        role.addMouseListener(popup);
        card.addMouseListener(popup);
        return card;
    }

    private JScrollPane createContent() {
        JPanel content = new JPanel();
        content.setBackground(PAGE);
        content.setBorder(new EmptyBorder(32, 30, 32, 30));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        JPanel header = createHeader();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(header);
        content.add(Box.createVerticalStrut(15));
        JPanel stats = createStatistics();
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(stats);
        content.add(Box.createVerticalStrut(18));
        JPanel summary = createSummary();
        summary.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(summary);
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(PAGE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel createHeader() {
        GradientPanel header = new GradientPanel(BROWN_LIGHT, BROWN_DARK);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(22, 22, 22, 22));
        header.setPreferredSize(new Dimension(900, 177));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 177));
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel eyebrow = new JLabel("MANAGER OPERASIONAL");
        eyebrow.setForeground(new Color(255, 228, 202));
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel title = new JLabel("Dashboard Manager");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 44));
        JLabel subtitle = new JLabel(
                "<html>Pantau kondisi operasional SwiftBite Morning Bakery, "
                + "mulai dari menu, meja QR, user aktif,<br>"
                + "sampai status pesanan hari ini.</html>");
        subtitle.setForeground(new Color(255, 235, 218));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titles.add(eyebrow);
        titles.add(Box.createVerticalStrut(5));
        titles.add(title);
        titles.add(Box.createVerticalStrut(5));
        titles.add(subtitle);
        lastUpdated.setForeground(new Color(255, 235, 218));
        lastUpdated.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        header.add(titles, BorderLayout.WEST);
        return header;
    }

    private JPanel createStatistics() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(900, 90));
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        stats.add(statCard("Total Menu", totalMenuValue));
        stats.add(statCard("Total Meja QR", totalTableValue));
        stats.add(statCard("Pesanan Hari Ini", todayOrdersValue));
        stats.add(statCard("User Aktif", activeUsersValue));
        return stats;
    }

    private JPanel statCard(String title, JLabel value) {
        RoundedPanel card = new RoundedPanel(10, BROWN);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 17, 13, 17));
        JLabel label = new JLabel(title);
        label.setForeground(new Color(244, 218, 199));
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card.add(label);
        card.add(Box.createVerticalStrut(4));
        card.add(value);
        return card;
    }

    private JPanel createSummary() {
        RoundedPanel panel = new RoundedPanel(10, BROWN_MID);
        panel.setBorder(new EmptyBorder(20, 19, 20, 19));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(900, 380));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
        JLabel title = new JLabel("Ringkasan Pesanan Hari Ini");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(new Color(160, 106, 75));
        tableHeader.setBorder(new EmptyBorder(12, 14, 12, 14));
        JLabel status = new JLabel("STATUS");
        status.setForeground(WHITE);
        status.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel amount = new JLabel("JUMLAH");
        amount.setForeground(WHITE);
        amount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHeader.add(status, BorderLayout.WEST);
        tableHeader.add(amount, BorderLayout.EAST);
        panel.add(title);
        panel.add(Box.createVerticalStrut(14));
        panel.add(tableHeader);
        panel.add(summaryRow("●  Menunggu", new Color(255, 211, 63), waitingValue));
        panel.add(summaryRow("●  Diproses", new Color(92, 167, 255), processingValue));
        panel.add(summaryRow("●  Siap Diantar", new Color(92, 167, 255), readyValue));
        panel.add(summaryRow("●  Menunggu Pembayaran",
                new Color(255, 211, 63), paymentValue));
        panel.add(summaryRow("●  Selesai", new Color(107, 203, 108), completeValue));
        return panel;
    }

    private JPanel summaryRow(String text, Color color, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, new Color(255, 255, 255, 45)),
                new EmptyBorder(12, 14, 12, 14)));
        JLabel label = new JLabel(text);
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private void loadDashboard(boolean showError) {
        new SwingWorker<ManagerData, Void>() {
            @Override
            protected ManagerData doInBackground() throws Exception {
                try (Connection connection = openConnection()) {
                    int menu = scalar(connection, "SELECT COUNT(*) FROM menus");
                    int tables = scalar(connection, "SELECT COUNT(*) FROM tables");
                    int orders = scalar(connection,
                            "SELECT COUNT(*) FROM orders WHERE DATE(created_at)=CURDATE()");
                    int users = scalar(connection, "SELECT COUNT(*) FROM users");
                    int waiting = statusCount(connection, "menunggu");
                    int processing = statusCount(connection, "diproses");
                    int ready = statusCount(connection, "siap_diantar");
                    int payment = statusCount(connection, "menunggu_pembayaran");
                    int complete = statusCount(connection, "selesai");
                    return new ManagerData(menu, tables, orders, users,
                            waiting, processing, ready, payment, complete);
                }
            }

            @Override
            protected void done() {
                try {
                    applyData(get());
                } catch (Exception exception) {
                    lastUpdated.setText("Data gagal dimuat");
                    if (showError) {
                        JOptionPane.showMessageDialog(DashboardManager.this,
                                "Dashboard Manager tidak dapat memuat data.\n"
                                + "Pastikan MySQL dan database swiftbite aktif.",
                                "Koneksi Database", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    private int statusCount(Connection connection, String status)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM orders "
                + "WHERE status=? AND DATE(created_at)=CURDATE()")) {
            statement.setString(1, status);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt(1) : 0;
            }
        }
    }

    private int scalar(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            return result.next() ? result.getInt(1) : 0;
        }
    }

    private void applyData(ManagerData data) {
        totalMenuValue.setText(String.valueOf(data.menu()));
        totalTableValue.setText(String.valueOf(data.tables()));
        todayOrdersValue.setText(String.valueOf(data.orders()));
        activeUsersValue.setText(String.valueOf(data.users()));
        waitingValue.setText(String.valueOf(data.waiting()));
        processingValue.setText(String.valueOf(data.processing()));
        readyValue.setText(String.valueOf(data.ready()));
        paymentValue.setText(String.valueOf(data.payment()));
        completeValue.setText(String.valueOf(data.complete()));
        lastUpdated.setText("Diperbarui "
                + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private void unavailable(String page) {
        JOptionPane.showMessageDialog(this,
                "Halaman " + page + " belum dibuat pada project desktop.",
                "Navigasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void loadWindowIcon() {
        URL logoUrl = getClass().getResource(
                "/morning_bakery/assets/Swiftbite-icon.png");
        if (logoUrl != null) {
            setIconImage(new ImageIcon(logoUrl).getImage());
        }
    }

    private static JLabel statValue() {
        JLabel label = new JLabel("0");
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        return label;
    }

    private static JLabel summaryValue() {
        JLabel label = new JLabel("0");
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private record ManagerData(int menu, int tables, int orders, int users,
            int waiting, int processing, int ready, int payment, int complete) {
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
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(fill);
            graphics2D.fillRoundRect(
                    0, 0, getWidth(), getHeight(), radius, radius);
            graphics2D.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class GradientPanel extends JPanel {

        private final Color start;
        private final Color end;

        GradientPanel(Color start, Color end) {
            this.start = start;
            this.end = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setPaint(new GradientPaint(
                    0, 0, start, getWidth(), getHeight(), end));
            graphics2D.fillRect(0, 0, getWidth(), getHeight());
            graphics2D.dispose();
            super.paintComponent(graphics);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new DashboardManager().setVisible(true));
    }
}
