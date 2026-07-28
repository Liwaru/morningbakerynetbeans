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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Halaman manajemen user untuk Manager.
 */
public class DataUser extends JFrame {

    private static final String DB_URL
            = "jdbc:mysql://127.0.0.1:3306/swiftbite"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final Color PAGE = new Color(249, 247, 244);
    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color CREAM = new Color(255, 247, 235);
    private static final Color BROWN_DARK = new Color(58, 28, 19);
    private static final Color BROWN = new Color(91, 48, 29);
    private static final Color BROWN_MID = new Color(132, 79, 47);
    private static final Color BROWN_LIGHT = new Color(166, 105, 67);
    private static final Color MUTED = new Color(235, 205, 184);
    private static final int[] USER_COLUMN_WIDTHS
            = {135, 110, 295, 120, 105, 155};
    private static final int USER_ROW_HEIGHT = 74;
    private static final int USER_TABLE_PADDING = 14;
    private static final int USER_HEADER_HEIGHT = 42;

    private final long managerId;
    private final String managerName;
    private final JLabel totalValue = statValue();
    private final JLabel waiterValue = statValue();
    private final JLabel cashierValue = statValue();
    private final JLabel bakerValue = statValue();
    private final JLabel resultDescription = new JLabel("Memuat data user...");
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> roleFilter = new JComboBox<>(new String[]{
        "Semua Role", "Waiter", "Baker", "Cashier",
        "Manager", "Owner", "Stock Staff"
    });
    private final JPanel userRows = new JPanel();
    private final JPanel userTable = new JPanel(new BorderLayout());
    private final RoundedPanel managementPanel = new RoundedPanel(10, BROWN_MID);
    private List<UserData> allUsers = new ArrayList<>();

    public DataUser() {
        this(3L, "manager");
    }

    public DataUser(long managerId, String managerName) {
        this.managerId = managerId;
        this.managerName = managerName == null || managerName.isBlank()
                ? "manager" : managerName;
        setTitle("SwiftBite - Data User");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1060, 680));
        setSize(1366, 768);
        setLocationRelativeTo(null);
        loadWindowIcon();
        setContentPane(createRoot());
        loadUsers(true);
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
        JLabel logo = new JLabel("", SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(48, 48));
        logo.setOpaque(true);
        logo.setBackground(WHITE);
        URL logoUrl = getClass().getResource("/morning_bakery/assets/Swiftbite.png");
        if (logoUrl != null) {
            Image image = new ImageIcon(logoUrl).getImage()
                    .getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(image));
        }
        JLabel brandName = new JLabel("SwiftBite");
        brandName.setForeground(WHITE);
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brand.add(logo);
        brand.add(brandName);
        MouseAdapter dashboardLink = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                new DashboardManager(managerId, managerName).setVisible(true);
                dispose();
            }
        };
        brand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brandName.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brand.addMouseListener(dashboardLink);
        logo.addMouseListener(dashboardLink);
        brandName.addMouseListener(dashboardLink);

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(42));
        sidebar.add(navButton("Data User", true, null));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navButton("Data Absensi", false,
                event -> {
                    new DataAbsensi(managerId, managerName).setVisible(true);
                    dispose();
                }));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navButton("Stok Produk", false, event -> {
            new KelolaStok(managerId, managerName, "Manager", true).setVisible(true);
            dispose();
        }));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createAccountCard());
        return sidebar;
    }

    private JButton navButton(String text, boolean selected,
            java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMinimumSize(new Dimension(224, 56));
        button.setPreferredSize(new Dimension(224, 56));
        button.setMaximumSize(new Dimension(224, 56));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(0, 18, 0, 18));
        Color normal = selected ? CREAM : new Color(117, 70, 43);
        button.setBackground(normal);
        button.setForeground(selected ? BROWN_DARK : WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setRolloverEnabled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (action != null) {
            button.addActionListener(action);
        }
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (!selected) {
                    button.setBackground(new Color(151, 93, 58));
                }
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
        role.setForeground(MUTED);
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
        JButton logout = smallButton("Logout", WHITE, BROWN_DARK);
        logout.setHorizontalAlignment(SwingConstants.LEFT);
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
        card.addMouseListener(popup);
        initial.addMouseListener(popup);
        name.addMouseListener(popup);
        role.addMouseListener(popup);
        return card;
    }

    private JScrollPane createContent() {
        JPanel content = new JPanel();
        content.setBackground(PAGE);
        content.setBorder(new EmptyBorder(54, 30, 35, 30));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        JPanel header = createHeader();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(header);
        content.add(Box.createVerticalStrut(15));
        JPanel stats = createStatistics();
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(stats);
        content.add(Box.createVerticalStrut(16));
        JPanel management = createManagementPanel();
        management.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(management);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(PAGE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(17);
        return scroll;
    }

    private JPanel createHeader() {
        GradientPanel header = new GradientPanel(BROWN_LIGHT, BROWN_DARK);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(21, 22, 21, 22));
        header.setPreferredSize(new Dimension(900, 176));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 176));
        JLabel eyebrow = new JLabel("MANAGER OPERASIONAL");
        eyebrow.setForeground(new Color(255, 228, 202));
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel title = new JLabel("Data User");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 43));
        JLabel subtitle = new JLabel(
                "<html>Kelola akun pengguna SwiftBite, lihat role yang digunakan, "
                + "dan pantau user yang aktif di sistem<br>bakery.</html>");
        subtitle.setForeground(new Color(255, 235, 218));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        header.add(eyebrow);
        header.add(Box.createVerticalStrut(4));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        return header;
    }

    private JPanel createStatistics() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(900, 120));
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        stats.add(statCard("Total User", totalValue, "Semua akun staf terdaftar"));
        stats.add(statCard("Waiter", waiterValue, "Akun pengantaran pesanan"));
        stats.add(statCard("Cashier", cashierValue, "Akun kasir operasional"));
        stats.add(statCard("Baker", bakerValue, "Akun baker dan bahan"));
        return stats;
    }

    private JPanel statCard(String title, JLabel value, String description) {
        RoundedPanel card = new RoundedPanel(10, BROWN_MID);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 17, 14, 17));
        JLabel label = new JLabel(title);
        label.setForeground(new Color(244, 218, 199));
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel detail = new JLabel(description);
        detail.setForeground(new Color(235, 205, 184));
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        card.add(label);
        card.add(Box.createVerticalStrut(3));
        card.add(value);
        card.add(Box.createVerticalGlue());
        card.add(detail);
        return card;
    }

    private JPanel createManagementPanel() {
        RoundedPanel panel = managementPanel;
        panel.setBorder(new EmptyBorder(20, 18, 20, 18));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(900, 800));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 800));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.setMinimumSize(new Dimension(0, 62));
        heading.setPreferredSize(new Dimension(900, 62));
        heading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Manajemen User");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        resultDescription.setForeground(new Color(255, 222, 198));
        resultDescription.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(resultDescription);
        JButton add = smallButton("Tambah User", BROWN, WHITE);
        add.setPreferredSize(new Dimension(112, 40));
        add.setMinimumSize(new Dimension(112, 40));
        add.setMaximumSize(new Dimension(112, 40));
        add.addActionListener(event -> showAddUserDialog());
        JPanel addHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        addHolder.setOpaque(false);
        addHolder.setPreferredSize(new Dimension(120, 40));
        addHolder.setMinimumSize(new Dimension(120, 40));
        addHolder.setMaximumSize(new Dimension(120, 40));
        addHolder.add(add);
        heading.add(titleBox, BorderLayout.WEST);
        heading.add(addHolder, BorderLayout.EAST);

        RoundedPanel filters = new RoundedPanel(
                8, new Color(255, 255, 255, 22));
        filters.setLayout(new GridBagLayout());
        filters.setBorder(new EmptyBorder(12, 14, 12, 14));
        filters.setMinimumSize(new Dimension(0, 88));
        filters.setPreferredSize(new Dimension(900, 88));
        filters.setMaximumSize(new Dimension(Integer.MAX_VALUE, 102));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(new EmptyBorder(10, 12, 10, 12));
        searchField.setBackground(WHITE);
        searchField.setForeground(BROWN_DARK);
        roleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleFilter.setBackground(WHITE);
        roleFilter.setForeground(BROWN_DARK);
        roleFilter.setBorder(BorderFactory.createLineBorder(
                new Color(218, 174, 136)));
        roleFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean selected,
                    boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, cellHasFocus);
                label.setOpaque(true);
                label.setBorder(new EmptyBorder(7, 9, 7, 9));
                label.setBackground(selected ? BROWN : WHITE);
                label.setForeground(selected ? WHITE : BROWN_DARK);
                return label;
            }
        });
        JButton apply = smallButton("Terapkan", BROWN, WHITE);
        JButton reset = smallButton("Reset", CREAM, BROWN_DARK);
        apply.addActionListener(event -> applyFilter());
        searchField.addActionListener(event -> applyFilter());
        reset.addActionListener(event -> {
            searchField.setText("");
            roleFilter.setSelectedIndex(0);
            applyFilter();
        });
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 0, 5, 10);
        c.gridx = 0;
        c.weightx = .58;
        c.fill = GridBagConstraints.HORIZONTAL;
        filters.add(filterLabel("SEARCH USER"), c);
        c.gridx = 1;
        c.weightx = .24;
        filters.add(filterLabel("FILTER ROLE"), c);
        c.gridy = 1;
        c.gridx = 0;
        c.weightx = .58;
        filters.add(searchField, c);
        c.gridx = 1;
        c.weightx = .24;
        filters.add(roleFilter, c);
        c.gridx = 2;
        c.weightx = .1;
        filters.add(apply, c);
        c.gridx = 3;
        c.weightx = .08;
        c.insets = new Insets(0, 0, 5, 0);
        filters.add(reset, c);

        userTable.setOpaque(false);
        userTable.setMinimumSize(new Dimension(0, 220));
        userTable.setPreferredSize(new Dimension(900, 600));
        userTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
        userTable.setBorder(BorderFactory.createLineBorder(
                new Color(255, 255, 255, 35)));
        userTable.removeAll();
        userTable.add(createTableHeader(), BorderLayout.NORTH);
        userRows.setOpaque(false);
        userRows.setLayout(new BoxLayout(userRows, BoxLayout.Y_AXIS));
        userRows.setBorder(new EmptyBorder(0, 0, 0, 0));
        userTable.add(userRows, BorderLayout.CENTER);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(15));
        panel.add(filters);
        panel.add(Box.createVerticalStrut(15));
        panel.add(userTable);
        return panel;
    }

    private JPanel createTableHeader() {
        JPanel header = createUserGrid(
                new Color(160, 106, 75), USER_HEADER_HEIGHT);
        header.setBackground(new Color(160, 106, 75));
        header.setBorder(new EmptyBorder(
                0, USER_TABLE_PADDING, 0, USER_TABLE_PADDING));
        addUserGridCell(header, headerLabel("NAMA"), 0);
        addUserGridCell(header, headerLabel("USERNAME"), 1);
        addUserGridCell(header, headerLabel("EMAIL"), 2);
        addUserGridCell(header, headerLabel("ROLE"), 3);
        addUserGridCell(header, headerLabel("STATUS"), 4);
        addUserGridCell(header, headerLabel("AKSI"), 5);
        return header;
    }

    private JPanel createUserRow(UserData user, boolean alternate) {
        JPanel row = createUserGrid(alternate
                ? new Color(118, 67, 41) : new Color(106, 58, 35),
                USER_ROW_HEIGHT);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, new Color(255, 255, 255, 40)),
                new EmptyBorder(0, USER_TABLE_PADDING, 0, USER_TABLE_PADDING)));
        JLabel name = new JLabel("<html><b>" + escapeHtml(user.name())
                + "</b><br><span style='font-size:10px'>ID User: "
                + user.id() + "</span></html>");
        name.setForeground(WHITE);
        JLabel username = rowLabel(user.name());
        JLabel email = rowLabel(user.email());
        JLabel role = badge(roleName(user.level()), roleColor(user.level()));
        JLabel status = badge("Aktif", new Color(72, 103, 57));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        actions.setOpaque(false);
        JButton edit = smallButton("Edit", CREAM, BROWN_DARK);
        JButton detail = smallButton("Detail", CREAM, BROWN_DARK);
        edit.setPreferredSize(new Dimension(66, 38));
        detail.setPreferredSize(new Dimension(66, 38));
        edit.setMinimumSize(new Dimension(66, 38));
        detail.setMinimumSize(new Dimension(66, 38));
        edit.addActionListener(event -> editUser(user));
        detail.addActionListener(event -> showUserDetail(user));
        actions.add(edit);
        actions.add(detail);
        addUserGridCell(row, name, 0);
        addUserGridCell(row, username, 1);
        addUserGridCell(row, email, 2);
        addUserGridCell(row, centeredBadge(role), 3);
        addUserGridCell(row, centeredBadge(status), 4);
        addUserGridCell(row, centeredComponent(actions), 5);
        return row;
    }

    private JPanel createUserGrid(Color background, int height) {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(true);
        grid.setBackground(background);
        grid.setMinimumSize(new Dimension(userTableWidth(), height));
        grid.setPreferredSize(new Dimension(userTableWidth(), height));
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return grid;
    }

    private void addUserGridCell(JPanel panel, Component component,
            int gridx) {
        int width = USER_COLUMN_WIDTHS[gridx];
        JPanel cell = new JPanel(new BorderLayout());
        cell.setOpaque(false);
        cell.setMinimumSize(new Dimension(width, 1));
        cell.setPreferredSize(new Dimension(width, 1));
        if (component instanceof JLabel label) {
            label.setVerticalAlignment(SwingConstants.CENTER);
        }
        cell.add(component, BorderLayout.CENTER);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(cell, c);
        if (gridx == USER_COLUMN_WIDTHS.length - 1) {
            GridBagConstraints filler = new GridBagConstraints();
            filler.gridx = USER_COLUMN_WIDTHS.length;
            filler.gridy = 0;
            filler.weightx = 1;
            filler.weighty = 1;
            filler.fill = GridBagConstraints.HORIZONTAL;
            panel.add(Box.createHorizontalGlue(), filler);
        }
    }

    private int userTableWidth() {
        int total = USER_TABLE_PADDING * 2;
        for (int width : USER_COLUMN_WIDTHS) {
            total += width;
        }
        return total;
    }

    private JPanel centeredBadge(JLabel badge) {
        return centeredComponent(badge);
    }

    private JPanel centeredComponent(Component component) {
        JPanel holder = new JPanel(new GridBagLayout());
        holder.setOpaque(false);
        holder.add(component);
        return holder;
    }

    private void loadUsers(boolean showError) {
        resultDescription.setText("Memuat data user...");
        new SwingWorker<List<UserData>, Void>() {
            @Override
            protected List<UserData> doInBackground() throws Exception {
                List<UserData> users = new ArrayList<>();
                try (Connection connection = openConnection();
                        PreparedStatement statement = connection.prepareStatement(
                                "SELECT u.id_user,u.name,u.email,"
                                + "COALESCE(u.level,0) level,u.created_at,u.updated_at,"
                                + "(SELECT FROM_UNIXTIME(MAX(s.last_activity)) "
                                + "FROM sessions s WHERE s.user_id=u.id_user) login_terakhir "
                                + "FROM users u WHERE COALESCE(u.level,0)>0 "
                                + "ORDER BY level,name");
                        ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        users.add(new UserData(result.getLong("id_user"),
                                result.getString("name"),
                                result.getString("email"),
                                result.getInt("level"),
                                result.getTimestamp("created_at"),
                                result.getTimestamp("updated_at"),
                                result.getTimestamp("login_terakhir")));
                    }
                }
                return users;
            }

            @Override
            protected void done() {
                try {
                    allUsers = get();
                    updateStatistics();
                    applyFilter();
                } catch (Exception exception) {
                    resultDescription.setText("Data user gagal dimuat.");
                    if (showError) {
                        JOptionPane.showMessageDialog(DataUser.this,
                                "Data user tidak dapat dimuat.\n"
                                + rootMessage(exception),
                                "Koneksi Database", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    private void updateStatistics() {
        totalValue.setText(String.valueOf(allUsers.size()));
        waiterValue.setText(String.valueOf(countLevel(1)));
        bakerValue.setText(String.valueOf(countLevel(2)));
        cashierValue.setText(String.valueOf(countLevel(3)));
    }

    private long countLevel(int level) {
        return allUsers.stream().filter(user -> user.level() == level).count();
    }

    private void applyFilter() {
        String search = searchField.getText().trim().toLowerCase(Locale.ROOT);
        String selectedRole = (String) roleFilter.getSelectedItem();
        List<UserData> filtered = allUsers.stream()
                .filter(user -> search.isEmpty()
                || user.name().toLowerCase(Locale.ROOT).contains(search)
                || user.email().toLowerCase(Locale.ROOT).contains(search))
                .filter(user -> "Semua Role".equals(selectedRole)
                || roleName(user.level()).equals(selectedRole))
                .toList();
        userRows.removeAll();
        for (int index = 0; index < filtered.size(); index++) {
            userRows.add(createUserRow(filtered.get(index), index % 2 == 1));
        }
        if (filtered.isEmpty()) {
            JLabel empty = new JLabel("Tidak ada user yang sesuai filter.",
                    SwingConstants.CENTER);
            empty.setForeground(WHITE);
            empty.setBorder(new EmptyBorder(22, 10, 22, 10));
            empty.setMinimumSize(new Dimension(0, USER_ROW_HEIGHT));
            empty.setPreferredSize(new Dimension(userTableWidth(), USER_ROW_HEIGHT));
            empty.setMaximumSize(new Dimension(Integer.MAX_VALUE, USER_ROW_HEIGHT));
            userRows.add(empty);
        }
        resizeUserTable(Math.max(1, filtered.size()));
        resultDescription.setText("Menampilkan " + filtered.size()
                + " user berdasarkan filter aktif.");
        userRows.revalidate();
        userRows.repaint();
    }

    private void resizeUserTable(int rowCount) {
        int listHeight = rowCount * USER_ROW_HEIGHT;
        int tableHeight = USER_HEADER_HEIGHT + listHeight;
        int managementHeight = 220 + tableHeight;
        userRows.setMinimumSize(new Dimension(userTableWidth(), listHeight));
        userRows.setPreferredSize(new Dimension(userTableWidth(), listHeight));
        userRows.setMaximumSize(new Dimension(Integer.MAX_VALUE, listHeight));
        userTable.setMinimumSize(new Dimension(0, tableHeight));
        userTable.setPreferredSize(new Dimension(900, tableHeight));
        userTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, tableHeight));
        managementPanel.setMinimumSize(new Dimension(0, managementHeight));
        managementPanel.setPreferredSize(new Dimension(900, managementHeight));
        managementPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, managementHeight));
        managementPanel.revalidate();
    }

    private void showAddUserDialog() {
        JDialog dialog = userDialog("Tambah User",
                "Buat akun baru untuk waiter, baker, atau cashier.");
        JPanel root = (JPanel) dialog.getContentPane();
        JTextField username = userInput();
        JPasswordField password = passwordInput();
        JComboBox<String> role = userRoleCombo(
                new String[]{"Pilih role", "Waiter", "Baker", "Cashier"});
        JPanel form = userForm();
        addUserField(form, "USERNAME", username);
        addUserField(form, "PASSWORD", password);
        addUserField(form, "ROLE", role);

        JButton cancel = smallButton("Batal", CREAM, BROWN_DARK);
        JButton save = smallButton("Simpan User", CREAM, BROWN_DARK);
        cancel.setPreferredSize(new Dimension(82, 44));
        save.setPreferredSize(new Dimension(126, 44));
        cancel.addActionListener(event -> dialog.dispose());
        save.addActionListener(event -> {
            String newUsername = username.getText().trim();
            String newPassword = new String(password.getPassword());
            String newRole = (String) role.getSelectedItem();
            if (newUsername.isEmpty() || newUsername.length() > 15) {
                showValidation(dialog, "Username wajib diisi dan maksimal 15 karakter.");
                return;
            }
            if (newPassword.length() < 6 || newPassword.length() > 20) {
                showValidation(dialog, "Password harus terdiri dari 6–20 karakter.");
                return;
            }
            if ("Pilih role".equals(newRole)) {
                showValidation(dialog, "Pilih role user terlebih dahulu.");
                return;
            }
            save.setEnabled(false);
            save.setText("Menyimpan...");
            saveNewUser(dialog, save, newUsername, newPassword, newRole);
        });
        JPanel actions = userDialogActions(cancel, save);
        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        dialog.setSize(520, 465);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void saveNewUser(JDialog dialog, JButton saveButton,
            String username, String password, String role) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String hashedPassword = hashPassword(password);
                try (Connection connection = openConnection();
                        PreparedStatement statement = connection.prepareStatement(
                                "INSERT INTO users "
                                + "(name,email,password,level,created_at,updated_at) "
                                + "VALUES (?,?,?,?,NOW(),NOW())")) {
                    statement.setString(1, username);
                    statement.setString(2,
                            username.toLowerCase(Locale.ROOT) + "@example.com");
                    statement.setString(3, hashedPassword);
                    statement.setInt(4, roleLevel(role));
                    statement.executeUpdate();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    dialog.dispose();
                    loadUsers(false);
                } catch (Exception exception) {
                    saveButton.setEnabled(true);
                    saveButton.setText("Simpan User");
                    JOptionPane.showMessageDialog(DataUser.this,
                            "User tidak dapat ditambahkan.\n"
                            + rootMessage(exception),
                            "Gagal Menambahkan", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void editUser(UserData user) {
        JDialog dialog = userDialog("Edit User",
                "Perbarui akun " + user.name()
                + ". Kosongkan password jika tidak diubah.");
        JPanel root = (JPanel) dialog.getContentPane();
        JTextField username = userInput();
        username.setText(user.name());
        JPasswordField password = passwordInput();
        JComboBox<String> role = userRoleCombo(new String[]{
            "Waiter", "Baker", "Cashier", "Manager", "Owner", "Stock Staff"
        });
        role.setSelectedItem(roleName(user.level()));
        JPanel form = userForm();
        addUserField(form, "USERNAME", username);
        addUserField(form, "PASSWORD BARU", password);
        addUserField(form, "ROLE", role);

        JButton cancel = smallButton("Batal", CREAM, BROWN_DARK);
        JButton save = smallButton("Simpan Perubahan", CREAM, BROWN_DARK);
        cancel.setPreferredSize(new Dimension(82, 44));
        save.setPreferredSize(new Dimension(158, 44));
        cancel.addActionListener(event -> dialog.dispose());
        save.addActionListener(event -> {
            String newUsername = username.getText().trim();
            String newPassword = new String(password.getPassword());
            String newRole = (String) role.getSelectedItem();
            if (newUsername.isEmpty() || newUsername.length() > 15) {
                showValidation(dialog, "Username wajib diisi dan maksimal 15 karakter.");
                return;
            }
            if (!newPassword.isEmpty()
                    && (newPassword.length() < 6 || newPassword.length() > 20)) {
                showValidation(dialog,
                        "Password baru harus terdiri dari 6–20 karakter.");
                return;
            }
            save.setEnabled(false);
            save.setText("Menyimpan...");
            updateUser(dialog, save, user.id(),
                    newUsername, newPassword, newRole);
        });
        root.add(form, BorderLayout.CENTER);
        root.add(userDialogActions(cancel, save), BorderLayout.SOUTH);
        dialog.setSize(520, 485);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateUser(JDialog dialog, JButton saveButton, long userId,
            String username, String password, String role) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                boolean changePassword = !password.isEmpty();
                String sql = changePassword
                        ? "UPDATE users SET name=?,password=?,level=?,"
                        + "updated_at=NOW() WHERE id_user=?"
                        : "UPDATE users SET name=?,level=?,updated_at=NOW() "
                        + "WHERE id_user=?";
                try (Connection connection = openConnection();
                        PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, username);
                    if (changePassword) {
                        statement.setString(2, hashPassword(password));
                        statement.setInt(3, roleLevel(role));
                        statement.setLong(4, userId);
                    } else {
                        statement.setInt(2, roleLevel(role));
                        statement.setLong(3, userId);
                    }
                    statement.executeUpdate();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    dialog.dispose();
                    loadUsers(false);
                } catch (Exception exception) {
                    saveButton.setEnabled(true);
                    saveButton.setText("Simpan Perubahan");
                    JOptionPane.showMessageDialog(DataUser.this,
                            "User tidak dapat diperbarui.\n"
                            + rootMessage(exception),
                            "Gagal Memperbarui", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void showUserDetail(UserData user) {
        JDialog dialog = userDialog("Detail User",
                "Informasi akun " + user.name() + ".");
        JPanel root = (JPanel) dialog.getContentPane();
        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setBorder(new EmptyBorder(2, 0, 0, 0));
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.add(detailRow("Nama", user.name()));
        details.add(detailRow("Username", user.name()));
        details.add(detailRow("Email", user.email()));
        details.add(detailRow("Role", roleName(user.level())));
        details.add(detailRow("Status", "Aktif"));
        details.add(detailRow("Tanggal Dibuat", formatDate(user.createdAt())));
        details.add(detailRow("Terakhir Diperbarui", formatDate(user.updatedAt())));
        details.add(detailRow("Login Terakhir", formatDate(user.lastLogin())));
        root.add(details, BorderLayout.CENTER);
        dialog.setSize(520, 485);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JDialog userDialog(String titleText, String subtitleText) {
        JDialog dialog = new JDialog(this, titleText, true);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        RoundedPanel root = new RoundedPanel(11, BROWN);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 105, 70)),
                new EmptyBorder(18, 18, 18, 18)));
        root.setLayout(new BorderLayout(0, 15));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel(titleText);
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(subtitle);
        JButton close = smallButton("X", CREAM, BROWN_DARK);
        close.setMinimumSize(new Dimension(40, 40));
        close.setPreferredSize(new Dimension(40, 40));
        close.setMaximumSize(new Dimension(40, 40));
        close.setMargin(new Insets(0, 0, 0, 0));
        close.setBorder(new EmptyBorder(0, 0, 0, 0));
        close.setHorizontalAlignment(SwingConstants.CENTER);
        close.setFont(new Font("Segoe UI", Font.BOLD, 14));
        close.addActionListener(event -> dialog.dispose());
        header.add(text, BorderLayout.CENTER);
        header.add(close, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);
        dialog.setContentPane(root);
        return dialog;
    }

    private JPanel userForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        return form;
    }

    private JTextField userInput() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(WHITE);
        field.setForeground(BROWN_DARK);
        field.setBorder(new EmptyBorder(10, 12, 10, 12));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        return field;
    }

    private JPasswordField passwordInput() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(WHITE);
        field.setForeground(BROWN_DARK);
        field.setBorder(new EmptyBorder(10, 12, 10, 12));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        return field;
    }

    private JComboBox<String> userRoleCombo(String[] roles) {
        JComboBox<String> combo = new JComboBox<>(roles);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(WHITE);
        combo.setForeground(BROWN_DARK);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        combo.setRenderer(roleFilter.getRenderer());
        return combo;
    }

    private void addUserField(JPanel form, String labelText, Component field) {
        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (field instanceof javax.swing.JComponent component) {
            component.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        form.add(label);
        form.add(Box.createVerticalStrut(6));
        form.add(field);
        form.add(Box.createVerticalStrut(14));
    }

    private JPanel userDialogActions(JButton cancel, JButton save) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0));
        actions.setOpaque(false);
        actions.add(cancel);
        actions.add(save);
        return actions;
    }

    private JPanel detailRow(String labelText, String valueText) {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, new Color(255, 255, 255, 45)),
                new EmptyBorder(9, 0, 9, 0)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel value = new JLabel(valueText);
        value.setForeground(WHITE);
        value.setFont(new Font("Segoe UI", Font.BOLD, 13));
        row.add(label);
        row.add(value);
        return row;
    }

    private void showValidation(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message,
                "Data Belum Valid", JOptionPane.WARNING_MESSAGE);
    }

    private int roleLevel(String role) {
        return switch (role) {
            case "Waiter" -> 1;
            case "Baker" -> 2;
            case "Cashier" -> 3;
            case "Manager" -> 4;
            case "Owner" -> 5;
            case "Stock Staff" -> 6;
            default -> throw new IllegalArgumentException("Role tidak valid.");
        };
    }

    private String hashPassword(String password) throws Exception {
        String php = new File("C:\\xampp\\php\\php.exe").isFile()
                ? "C:\\xampp\\php\\php.exe" : "php";
        ProcessBuilder builder = new ProcessBuilder(
                php, "-r",
                "echo password_hash($argv[1], PASSWORD_BCRYPT);",
                password);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        String result;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            result = reader.readLine();
        }
        if (process.waitFor() != 0 || result == null
                || !(result.startsWith("$2y$") || result.startsWith("$2a$"))) {
            throw new SQLException("Password tidak dapat diamankan dengan BCrypt.");
        }
        return result;
    }

    private String formatDate(Timestamp timestamp) {
        if (timestamp == null) {
            return "-";
        }
        return timestamp.toLocalDateTime().format(
                DateTimeFormatter.ofPattern(
                        "dd MMM yyyy HH:mm", Locale.ENGLISH));
    }

    private JButton smallButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(new EmptyBorder(8, 13, 8, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setRolloverEnabled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel filterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        return label;
    }

    private JLabel headerLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return label;
    }

    private JLabel rowLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private JLabel badge(String text, Color background) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(background);
        int brightness = (background.getRed() * 299
                + background.getGreen() * 587
                + background.getBlue() * 114) / 1000;
        label.setForeground(brightness > 155 ? BROWN_DARK : WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setBorder(new EmptyBorder(4, 8, 4, 8));
        return label;
    }

    private static Color roleColor(int level) {
        return switch (level) {
            case 1 -> new Color(164, 104, 67);
            case 2 -> new Color(142, 91, 53);
            case 3 -> new Color(118, 70, 43);
            case 4 -> new Color(203, 157, 111);
            case 5 -> new Color(91, 48, 29);
            case 6 -> new Color(126, 78, 46);
            default -> new Color(106, 64, 42);
        };
    }

    private static String roleName(int level) {
        return switch (level) {
            case 1 -> "Waiter";
            case 2 -> "Baker";
            case 3 -> "Cashier";
            case 4 -> "Manager";
            case 5 -> "Owner";
            case 6 -> "Stock Staff";
            default -> "User";
        };
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

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String rootMessage(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() == null
                ? "Periksa koneksi database." : cause.getMessage();
    }

    private record UserData(long id, String name, String email, int level,
            Timestamp createdAt, Timestamp updatedAt, Timestamp lastLogin) {
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
        SwingUtilities.invokeLater(() -> new DataUser().setVisible(true));
    }
}
