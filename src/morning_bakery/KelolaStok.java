package morning_bakery;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import javax.imageio.ImageIO;

/**
 * Halaman pengelolaan stok produk SwiftBite.
 *
 * <p>Seluruh komponen dibuat dengan Swing sehingga class ini dapat langsung
 * dijalankan dari NetBeans tanpa library UI tambahan.</p>
 */
public class KelolaStok extends JFrame {

    private static final String DB_URL
            = "jdbc:mysql://127.0.0.1:3306/swiftbite"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final Color PAGE = new Color(249, 247, 244);
    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color CREAM = new Color(255, 247, 235);
    private static final Color BROWN_DARK = new Color(59, 29, 19);
    private static final Color BROWN = new Color(103, 55, 32);
    private static final Color BROWN_MID = new Color(133, 82, 48);
    private static final Color BROWN_LIGHT = new Color(166, 105, 67);
    private static final Color TEXT = new Color(44, 26, 18);
    private static final Color MUTED = new Color(124, 86, 63);
    private static final Color BORDER = new Color(225, 177, 130);
    private static final Color SAFE_BG = new Color(226, 252, 216);
    private static final Color SAFE_FG = new Color(28, 121, 25);
    private static final Color LOW_BG = new Color(255, 239, 196);
    private static final Color LOW_FG = new Color(163, 102, 11);
    private static final Color EMPTY_BG = new Color(255, 222, 216);
    private static final Color EMPTY_FG = new Color(177, 45, 32);

    private final long stockStaffId;
    private final String accountName;
    private final String accountRole;
    private final boolean managerMode;
    private final JLabel totalValue = statisticValue();
    private final JLabel safeValue = statisticValue();
    private final JLabel lowValue = statisticValue();
    private final JLabel emptyValue = statisticValue();
    private final JPanel foodCards = cardsStrip();
    private final JPanel drinkCards = cardsStrip();
    private final JLabel foodCount = countBadge();
    private final JLabel drinkCount = countBadge();
    private final JLabel loadingLabel = new JLabel("Memuat produk...", SwingConstants.CENTER);

    public KelolaStok() {
        this(9L, "stockstaff", "Stock Staff", false);
    }

    public KelolaStok(String accountName) {
        this(9L, accountName, "Stock Staff", false);
    }

    public KelolaStok(String accountName, String accountRole) {
        this(9L, accountName, accountRole, false);
    }

    public KelolaStok(long stockStaffId, String accountName, String accountRole) {
        this(stockStaffId, accountName, accountRole, false);
    }

    public KelolaStok(long stockStaffId, String accountName,
            String accountRole, boolean managerMode) {
        this.stockStaffId = stockStaffId;
        this.accountName = accountName == null || accountName.isBlank()
                ? "stockstaff" : accountName;
        this.accountRole = accountRole == null || accountRole.isBlank()
                ? "Stock Staff" : accountRole;
        this.managerMode = managerMode;
        setTitle("SwiftBite - Kelola Stok");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1060, 680));
        setSize(1366, 768);
        setLocationRelativeTo(null);
        loadWindowIcon();
        setContentPane(createRoot());
        loadProducts();
    }

    private JPanel createRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE);
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createContent(), BorderLayout.CENTER);
        return root;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new GradientPanel(BROWN_LIGHT, BROWN_DARK, 0);
        sidebar.setPreferredSize(new Dimension(245, 720));
        sidebar.setBorder(new EmptyBorder(26, 18, 24, 18));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brand.setOpaque(false);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel logo = new JLabel("S", SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(48, 48));
        logo.setOpaque(true);
        logo.setBackground(WHITE);
        logo.setForeground(BROWN);
        logo.setFont(new Font("Serif", Font.BOLD, 27));
        URL logoUrl = getClass().getResource("/morning_bakery/assets/Swiftbite.png");
        if (logoUrl != null) {
            Image image = new ImageIcon(logoUrl).getImage()
                    .getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            logo.setText("");
            logo.setIcon(new ImageIcon(image));
        }
        JLabel brandName = new JLabel("  SwiftBite");
        brandName.setForeground(WHITE);
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brand.add(logo);
        brand.add(brandName);

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(42));
        if (managerMode) {
            JButton users = navButton("Data User", false);
            users.addActionListener(event -> {
                new DataUser(stockStaffId, accountName).setVisible(true);
                dispose();
            });
            sidebar.add(users);
            sidebar.add(Box.createVerticalStrut(10));
            JButton attendance = navButton("Data Absensi", false);
            attendance.addActionListener(event -> {
                new DataAbsensi(stockStaffId, accountName).setVisible(true);
                dispose();
            });
            sidebar.add(attendance);
            sidebar.add(Box.createVerticalStrut(10));
        }
        sidebar.add(navButton(managerMode ? "Stok Produk" : "Kelola Stok", true));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createAccountCard());

        MouseAdapter dashboardLink = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (managerMode) {
                    new DashboardManager(stockStaffId, accountName).setVisible(true);
                } else {
                    new DashboardStockStaff(
                            stockStaffId, accountName).setVisible(true);
                }
                dispose();
            }
        };
        brand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brandName.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        brand.addMouseListener(dashboardLink);
        logo.addMouseListener(dashboardLink);
        brandName.addMouseListener(dashboardLink);
        return sidebar;
    }

    private void showUnavailablePage(String page) {
        JOptionPane.showMessageDialog(this,
                "Halaman " + page + " belum dibuat pada project desktop.",
                "Navigasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton navButton(String text, boolean selected) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMinimumSize(new Dimension(209, 50));
        button.setPreferredSize(new Dimension(209, 50));
        button.setMaximumSize(new Dimension(209, 50));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(0, 18, 0, 18));
        Color normal = selected
                ? new Color(126, 78, 46) : new Color(117, 70, 43);
        button.setBackground(normal);
        button.setForeground(WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
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
        RoundedPanel card = new RoundedPanel(10, new Color(255, 255, 255, 28));
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 11, 11));
        card.setMaximumSize(new Dimension(209, 66));
        card.setPreferredSize(new Dimension(209, 66));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 45)));

        JLabel initial = new JLabel(
                accountName.substring(0, 1).toUpperCase(Locale.ROOT),
                SwingConstants.CENTER);
        initial.setPreferredSize(new Dimension(40, 40));
        initial.setOpaque(true);
        initial.setBackground(new Color(255, 255, 255, 50));
        initial.setForeground(WHITE);
        initial.setFont(new Font("Segoe UI", Font.BOLD, 17));

        JPanel identity = new JPanel();
        identity.setOpaque(false);
        identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(accountName);
        name.setForeground(WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel role = new JLabel(accountRole);
        role.setForeground(new Color(242, 218, 197));
        role.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        identity.add(name);
        identity.add(role);
        card.add(initial);
        card.add(identity);

        JPopupMenu accountMenu = new JPopupMenu();
        accountMenu.setOpaque(false);
        accountMenu.setBorder(new EmptyBorder(3, 3, 3, 3));
        RoundedPanel logoutPanel = new RoundedPanel(8, WHITE);
        logoutPanel.setLayout(new BorderLayout());
        logoutPanel.setPreferredSize(new Dimension(203, 42));
        JButton logoutButton = new JButton("Logout");
        logoutButton.setUI(new BasicButtonUI());
        logoutButton.setForeground(BROWN_DARK);
        logoutButton.setBackground(WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setBorder(new EmptyBorder(8, 18, 8, 18));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                logoutButton.setBackground(new Color(245, 235, 225));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                logoutButton.setBackground(WHITE);
            }
        });
        logoutButton.addActionListener(event -> {
            accountMenu.setVisible(false);
            dispose();
            new login().setVisible(true);
        });
        logoutPanel.add(logoutButton, BorderLayout.CENTER);
        accountMenu.add(logoutPanel);

        MouseAdapter accountMenuLink = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                accountMenu.show(card, 0,
                        -accountMenu.getPreferredSize().height - 4);
            }
        };
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        initial.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        name.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        role.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(accountMenuLink);
        initial.addMouseListener(accountMenuLink);
        name.addMouseListener(accountMenuLink);
        role.addMouseListener(accountMenuLink);
        return card;
    }

    private JScrollPane createContent() {
        JPanel content = new JPanel();
        content.setBackground(PAGE);
        content.setBorder(new EmptyBorder(54, 30, 35, 34));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel header = createHeader();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(header);
        content.add(Box.createVerticalStrut(15));

        JPanel statistics = createStatistics();
        statistics.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(statistics);
        content.add(Box.createVerticalStrut(15));

        loadingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadingLabel.setForeground(MUTED);
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loadingLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        content.add(loadingLabel);

        JPanel food = createProductSection(
                "Makanan", "Kelola stok produk makanan dan bakery.",
                foodCount, foodCards);
        food.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(food);
        content.add(Box.createVerticalStrut(18));

        JPanel drinks = createProductSection(
                "Minuman", "Kelola stok produk minuman.",
                drinkCount, drinkCards);
        drinks.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(drinks);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        return scrollPane;
    }

    private JPanel createHeader() {
        JPanel header = new GradientPanel(BROWN_LIGHT, BROWN_DARK, 12);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(21, 22, 21, 22));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 152));
        header.setPreferredSize(new Dimension(900, 152));

        JLabel eyebrow = new JLabel(managerMode
                ? "MANAGER OPERASIONAL" : "STOCK STAFF OPERASIONAL");
        eyebrow.setForeground(new Color(255, 228, 202));
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel title = new JLabel("Stok Produk");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 43));
        JLabel subtitle = new JLabel(
                "Pantau dan kelola jumlah stok makanan dan minuman SwiftBite Morning Bakery.");
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
        JPanel statistics = new JPanel(new GridLayout(1, 4, 14, 0));
        statistics.setOpaque(false);
        statistics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        statistics.setPreferredSize(new Dimension(900, 120));
        statistics.add(statisticCard("Total Produk", totalValue,
                "Produk yang memiliki stok"));
        statistics.add(statisticCard("Stok Aman", safeValue,
                "Lebih dari 5 pcs"));
        statistics.add(statisticCard("Stok Menipis", lowValue,
                "1 sampai 5 pcs"));
        statistics.add(statisticCard("Stok Habis", emptyValue,
                "0 pcs"));
        return statistics;
    }

    private JPanel statisticCard(String title, JLabel value, String description) {
        RoundedPanel card = new RoundedPanel(10, BROWN);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(17, 18, 14, 18));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(246, 222, 203));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setForeground(new Color(226, 199, 181));
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(value);
        card.add(Box.createVerticalGlue());
        card.add(descriptionLabel);
        return card;
    }

    private JPanel createProductSection(String title, String description,
            JLabel count, JPanel cards) {
        RoundedPanel section = new RoundedPanel(12, BROWN_MID);
        section.setLayout(new BorderLayout(0, 16));
        section.setBorder(new EmptyBorder(19, 18, 18, 18));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));
        section.setPreferredSize(new Dimension(900, 450));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        titleRow.add(titleLabel);
        titleRow.add(Box.createHorizontalStrut(10));
        titleRow.add(count);
        titleRow.add(Box.createHorizontalStrut(10));
        JButton addMenu = dialogButton("+ Tambah Menu", CREAM, TEXT);
        addMenu.setPreferredSize(new Dimension(125, 28));
        addMenu.setFont(new Font("Segoe UI", Font.BOLD, 11));
        addMenu.addActionListener(event -> showAddMenuDialog(title));
        titleRow.add(addMenu);
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionLabel.setForeground(new Color(255, 231, 211));
        descriptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        heading.add(titleRow);
        heading.add(Box.createVerticalStrut(5));
        heading.add(descriptionLabel);

        JScrollPane cardScroll = new JScrollPane(cards);
        cardScroll.setBorder(null);
        cardScroll.setOpaque(false);
        cardScroll.getViewport().setOpaque(false);
        cardScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        cardScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cardScroll.getHorizontalScrollBar().setUnitIncrement(210);

        JButton previous = carouselButton("‹");
        previous.addActionListener(event -> moveCarousel(cardScroll, -225));
        JButton next = carouselButton("›");
        next.addActionListener(event -> moveCarousel(cardScroll, 225));
        JPanel previousHolder = carouselButtonHolder(previous);
        JPanel nextHolder = carouselButtonHolder(next);
        JPanel carousel = new JPanel(new BorderLayout(10, 0));
        carousel.setOpaque(false);
        carousel.add(previousHolder, BorderLayout.WEST);
        carousel.add(cardScroll, BorderLayout.CENTER);
        carousel.add(nextHolder, BorderLayout.EAST);

        section.add(heading, BorderLayout.NORTH);
        section.add(carousel, BorderLayout.CENTER);
        return section;
    }

    private JButton carouselButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setPreferredSize(new Dimension(48, 48));
        button.setMinimumSize(new Dimension(48, 48));
        button.setMaximumSize(new Dimension(48, 48));
        button.setFont(new Font("Segoe UI", Font.BOLD, 26));
        button.setForeground(TEXT);
        button.setBackground(CREAM);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel carouselButtonHolder(JButton button) {
        JPanel holder = new JPanel(new GridBagLayout());
        holder.setOpaque(false);
        holder.setPreferredSize(new Dimension(48, 320));
        holder.add(button);
        return holder;
    }

    private void moveCarousel(JScrollPane scrollPane, int amount) {
        int current = scrollPane.getHorizontalScrollBar().getValue();
        scrollPane.getHorizontalScrollBar().setValue(current + amount);
    }

    private JPanel createProductCard(Product product) {
        RoundedPanel card = new RoundedPanel(10, WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(11, 11, 10, 11));
        card.setPreferredSize(new Dimension(210, 320));
        card.setMinimumSize(new Dimension(210, 320));
        card.setMaximumSize(new Dimension(210, 320));

        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(238, 226, 213));
        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(188, 142));
        imageLabel.setMinimumSize(new Dimension(188, 142));
        imageLabel.setMaximumSize(new Dimension(188, 142));
        ImageIcon icon = productImage(product);
        if (icon != null) {
            imageLabel.setIcon(icon);
        } else {
            imageLabel.setText("<html><div style='text-align:center;'>"
                    + escapeHtml(product.name()) + "</div></html>");
            imageLabel.setForeground(BROWN_DARK);
            imageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            imageLabel.setBorder(new EmptyBorder(12, 12, 12, 12));
        }

        JLabel nameLabel = new JLabel(ellipsis(product.name(), 23));
        nameLabel.setToolTipText(product.name());
        nameLabel.setForeground(TEXT);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel stockBox = new JPanel(new BorderLayout(4, 2));
        stockBox.setOpaque(true);
        stockBox.setBackground(CREAM);
        stockBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(6, 9, 6, 9)));
        stockBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        stockBox.setPreferredSize(new Dimension(188, 65));
        stockBox.setMinimumSize(new Dimension(188, 65));
        stockBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        JLabel caption = new JLabel("STOK SAAT INI");
        caption.setForeground(MUTED);
        caption.setFont(new Font("Segoe UI", Font.BOLD, 10));
        JLabel amount = new JLabel(product.stock() + " pcs");
        amount.setForeground(TEXT);
        amount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel status = statusBadge(product.stock());
        stockBox.add(caption, BorderLayout.NORTH);
        stockBox.add(amount, BorderLayout.CENTER);
        stockBox.add(status, BorderLayout.EAST);

        JButton manage = new JButton("Kelola Stok");
        manage.setUI(new BasicButtonUI());
        manage.setAlignmentX(Component.LEFT_ALIGNMENT);
        manage.setPreferredSize(new Dimension(188, 43));
        manage.setMinimumSize(new Dimension(188, 43));
        manage.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        manage.setBackground(CREAM);
        manage.setForeground(TEXT);
        manage.setFont(new Font("Segoe UI", Font.BOLD, 13));
        manage.setBorder(BorderFactory.createLineBorder(BORDER));
        manage.setFocusPainted(false);
        manage.setRolloverEnabled(false);
        manage.setContentAreaFilled(true);
        manage.setOpaque(true);
        manage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        manage.addActionListener(event -> showStockDialog(product));

        card.add(imageLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(9));
        card.add(stockBox);
        card.add(Box.createVerticalStrut(10));
        card.add(manage);
        return card;
    }

    private JLabel statusBadge(int stock) {
        String text = stock <= 0 ? "Habis" : stock <= 5 ? "Menipis" : "Aman";
        Color background = stock <= 0 ? EMPTY_BG : stock <= 5 ? LOW_BG : SAFE_BG;
        Color foreground = stock <= 0 ? EMPTY_FG : stock <= 5 ? LOW_FG : SAFE_FG;
        JLabel badge = new JLabel(text, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(background);
        badge.setForeground(foreground);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setBorder(new EmptyBorder(3, 7, 3, 7));
        return badge;
    }

    private void showStockDialog(Product product) {
        JDialog dialog = new JDialog(this, "Kelola Stok", true);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        RoundedPanel root = new RoundedPanel(12, BROWN);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(154, 99, 64)),
                new EmptyBorder(20, 20, 20, 20)));
        root.setLayout(new BorderLayout(0, 16));

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        JPanel headingText = new JPanel();
        headingText.setOpaque(false);
        headingText.setLayout(new BoxLayout(headingText, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Kelola Stok");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        JLabel subtitle = new JLabel("Perbarui stok untuk " + product.name() + ".");
        subtitle.setForeground(new Color(244, 217, 197));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        headingText.add(title);
        headingText.add(Box.createVerticalStrut(3));
        headingText.add(subtitle);
        JButton close = dialogButton("×", CREAM, TEXT);
        close.setPreferredSize(new Dimension(38, 38));
        close.setFont(new Font("Segoe UI", Font.BOLD, 20));
        close.addActionListener(event -> dialog.dispose());
        header.add(headingText, BorderLayout.CENTER);
        header.add(close, BorderLayout.EAST);

        RoundedPanel currentStock = new RoundedPanel(
                8, new Color(255, 255, 255, 22));
        currentStock.setLayout(new BorderLayout());
        currentStock.setBorder(new EmptyBorder(9, 12, 9, 12));
        JLabel currentCaption = new JLabel("STOK SAAT INI");
        currentCaption.setForeground(new Color(242, 211, 190));
        currentCaption.setFont(new Font("Segoe UI", Font.BOLD, 11));
        JLabel currentAmount = new JLabel(product.stock() + " pcs");
        currentAmount.setForeground(WHITE);
        currentAmount.setFont(new Font("Segoe UI", Font.BOLD, 17));
        currentStock.add(currentCaption, BorderLayout.WEST);
        currentStock.add(currentAmount, BorderLayout.EAST);

        JButton manualTab = dialogButton("Manual", CREAM, TEXT);
        JButton barcodeTab = dialogButton("Lewat Barcode",
                new Color(126, 78, 50), WHITE);
        manualTab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        barcodeTab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JPanel tabs = new JPanel(new GridLayout(1, 2, 10, 0));
        tabs.setOpaque(false);
        tabs.setMinimumSize(new Dimension(460, 48));
        tabs.setPreferredSize(new Dimension(460, 48));
        tabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        tabs.add(manualTab);
        tabs.add(barcodeTab);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(header);
        top.add(Box.createVerticalStrut(14));
        top.add(currentStock);
        top.add(Box.createVerticalStrut(14));
        top.add(tabs);

        CardLayout cardLayout = new CardLayout();
        JPanel modes = new JPanel(cardLayout);
        modes.setOpaque(false);
        modes.add(createManualStockPanel(dialog, product), "manual");
        modes.add(createBarcodeStockPanel(dialog), "barcode");
        manualTab.addActionListener(event -> {
            cardLayout.show(modes, "manual");
            selectDialogTab(manualTab, barcodeTab);
        });
        barcodeTab.addActionListener(event -> {
            cardLayout.show(modes, "barcode");
            selectDialogTab(barcodeTab, manualTab);
            focusBarcodeField(modes);
        });

        root.add(top, BorderLayout.NORTH);
        root.add(modes, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.setSize(520, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createManualStockPanel(JDialog dialog, Product product) {
        JPanel panel = new JPanel();
        panel.setName("manualPanel");
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel changeLabel = dialogFieldLabel("JENIS PERUBAHAN");
        JButton addButton = dialogButton("+   TAMBAH STOK", CREAM, TEXT);
        JButton reduceButton = dialogButton("−   KURANGI STOK",
                new Color(126, 78, 50), WHITE);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reduceButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boolean[] adding = {true};
        addButton.addActionListener(event -> {
            adding[0] = true;
            selectDialogTab(addButton, reduceButton);
        });
        reduceButton.addActionListener(event -> {
            adding[0] = false;
            selectDialogTab(reduceButton, addButton);
        });
        JPanel changeButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        changeButtons.setOpaque(false);
        changeButtons.setMinimumSize(new Dimension(460, 48));
        changeButtons.setPreferredSize(new Dimension(460, 48));
        changeButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        changeButtons.add(addButton);
        changeButtons.add(reduceButton);

        JLabel amountLabel = dialogFieldLabel("JUMLAH PERUBAHAN");
        JSpinner amount = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        amount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        amount.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        amount.setPreferredSize(new Dimension(460, 48));

        JLabel noteLabel = dialogFieldLabel("KETERANGAN (OPSIONAL)");
        JTextArea note = new JTextArea(2, 20);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        note.setForeground(TEXT);
        note.setBackground(WHITE);
        note.setBorder(new EmptyBorder(11, 12, 11, 12));
        JScrollPane noteScroll = new JScrollPane(note);
        noteScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        noteScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JButton cancel = dialogButton("Batal", WHITE, TEXT);
        cancel.addActionListener(event -> dialog.dispose());
        JButton save = dialogButton("Simpan Stok", CREAM, TEXT);
        save.addActionListener(event -> {
            int quantity = (Integer) amount.getValue();
            int delta = adding[0] ? quantity : -quantity;
            saveManualChange(dialog, save, product, delta, note.getText().trim());
        });
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        cancel.setPreferredSize(new Dimension(128, 46));
        save.setPreferredSize(new Dimension(138, 46));
        actions.add(cancel);
        actions.add(save);

        panel.add(Box.createVerticalStrut(1));
        panel.add(changeLabel);
        panel.add(Box.createVerticalStrut(7));
        panel.add(changeButtons);
        panel.add(Box.createVerticalStrut(14));
        panel.add(amountLabel);
        panel.add(Box.createVerticalStrut(7));
        panel.add(amount);
        panel.add(Box.createVerticalStrut(14));
        panel.add(noteLabel);
        panel.add(Box.createVerticalStrut(7));
        panel.add(noteScroll);
        panel.add(Box.createVerticalGlue());
        panel.add(actions);
        return panel;
    }

    private JPanel createBarcodeStockPanel(JDialog dialog) {
        JPanel panel = new JPanel();
        panel.setName("barcodePanel");
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel scanLabel = dialogFieldLabel("SCAN / INPUT BARCODE");
        JTextField barcodeField = new JTextField();
        barcodeField.setName("barcodeField");
        barcodeField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        barcodeField.setForeground(TEXT);
        barcodeField.setBackground(WHITE);
        barcodeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 12, 10, 12)));
        barcodeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        RoundedPanel statusPanel = new RoundedPanel(
                8, new Color(255, 255, 255, 22));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(new EmptyBorder(10, 12, 10, 12));
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        JLabel statusCaption = new JLabel("STATUS");
        statusCaption.setForeground(new Color(242, 211, 190));
        statusCaption.setFont(new Font("Segoe UI", Font.BOLD, 10));
        JLabel statusText = new JLabel("Belum ada barcode discan.");
        statusText.setForeground(WHITE);
        statusText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusPanel.add(statusCaption);
        statusPanel.add(Box.createVerticalStrut(3));
        statusPanel.add(statusText);

        DefaultTableModel scanModel = new DefaultTableModel(
                new String[]{"PRODUK", "PERUBAHAN", "STOK"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable history = new JTable(scanModel);
        history.setRowHeight(31);
        history.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        history.setForeground(WHITE);
        history.setBackground(new Color(255, 255, 255, 13));
        history.setGridColor(new Color(255, 255, 255, 28));
        history.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 10));
        history.getTableHeader().setForeground(WHITE);
        history.getTableHeader().setBackground(new Color(126, 91, 72));
        JScrollPane historyScroll = new JScrollPane(history);
        historyScroll.setBorder(null);
        historyScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        historyScroll.getViewport().setBackground(new Color(91, 48, 29));

        barcodeField.addActionListener(event -> processBarcodeScan(
                barcodeField, statusText, scanModel));

        JButton close = dialogButton("Tutup", CREAM, TEXT);
        close.setPreferredSize(new Dimension(128, 46));
        close.addActionListener(event -> {
            dialog.dispose();
            loadProducts();
        });
        JPanel closeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closeRow.setOpaque(false);
        closeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        closeRow.add(close);

        panel.add(Box.createVerticalStrut(1));
        panel.add(scanLabel);
        panel.add(Box.createVerticalStrut(7));
        panel.add(barcodeField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(statusPanel);
        panel.add(Box.createVerticalStrut(13));
        panel.add(dialogFieldLabel("RIWAYAT SCAN"));
        panel.add(Box.createVerticalStrut(7));
        panel.add(historyScroll);
        panel.add(Box.createVerticalGlue());
        panel.add(closeRow);
        return panel;
    }

    private void focusBarcodeField(JPanel container) {
        SwingUtilities.invokeLater(() -> {
            Component field = findNamedComponent(container, "barcodeField");
            if (field != null) {
                field.requestFocusInWindow();
            }
        });
    }

    private Component findNamedComponent(Component component, String name) {
        if (name.equals(component.getName())) {
            return component;
        }
        if (component instanceof java.awt.Container container) {
            for (Component child : container.getComponents()) {
                Component found = findNamedComponent(child, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private void saveManualChange(JDialog dialog, JButton saveButton,
            Product product, int delta, String note) {
        saveButton.setEnabled(false);
        saveButton.setText("Menyimpan...");
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                if (product.id() <= 0) {
                    throw new SQLException("Produk contoh tidak tersimpan di database.");
                }
                try (Connection connection = openConnection()) {
                    connection.setAutoCommit(false);
                    try {
                        int current;
                        try (PreparedStatement select = connection.prepareStatement(
                                "SELECT stok FROM menus WHERE id_menu=? FOR UPDATE")) {
                            select.setLong(1, product.id());
                            try (ResultSet result = select.executeQuery()) {
                                if (!result.next()) {
                                    throw new SQLException("Produk tidak ditemukan.");
                                }
                                current = result.getInt("stok");
                            }
                        }
                        int newStock = Math.max(0, current + delta);
                        try (PreparedStatement update = connection.prepareStatement(
                                "UPDATE menus SET stok=?,status=?,updated_at=NOW() "
                                + "WHERE id_menu=?")) {
                            update.setInt(1, newStock);
                            update.setString(2, newStock == 0 ? "habis" : "tersedia");
                            update.setLong(3, product.id());
                            update.executeUpdate();
                        }
                        connection.commit();
                        return newStock;
                    } catch (Exception exception) {
                        connection.rollback();
                        throw exception;
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    int newStock = get();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(KelolaStok.this,
                            "Stok " + product.name() + " sekarang "
                            + newStock + " pcs."
                            + (note.isBlank() ? "" : "\nKeterangan: " + note),
                            "Stok Tersimpan", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } catch (Exception exception) {
                    saveButton.setEnabled(true);
                    saveButton.setText("Simpan Stok");
                    JOptionPane.showMessageDialog(KelolaStok.this,
                            "Stok tidak dapat disimpan.\n"
                            + rootMessage(exception),
                            "Gagal Menyimpan", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void processBarcodeScan(JTextField barcodeField,
            JLabel statusText, DefaultTableModel scanModel) {
        String barcode = barcodeField.getText().trim();
        if (barcode.isEmpty()) {
            statusText.setText("Masukkan atau scan barcode terlebih dahulu.");
            return;
        }
        barcodeField.setEnabled(false);
        statusText.setText("Memproses barcode " + barcode + "...");
        new SwingWorker<ScanResult, Void>() {
            @Override
            protected ScanResult doInBackground() throws Exception {
                try (Connection connection = openConnection()) {
                    connection.setAutoCommit(false);
                    try {
                        long id;
                        String name;
                        int currentStock;
                        try (PreparedStatement select = connection.prepareStatement(
                                "SELECT id_menu,nama_menu,stok FROM menus "
                                + "WHERE barcode=? FOR UPDATE")) {
                            select.setString(1, barcode);
                            try (ResultSet result = select.executeQuery()) {
                                if (!result.next()) {
                                    throw new SQLException(
                                            "Barcode tidak ditemukan: " + barcode);
                                }
                                id = result.getLong("id_menu");
                                name = result.getString("nama_menu");
                                currentStock = result.getInt("stok");
                            }
                        }
                        int newStock = currentStock + 1;
                        try (PreparedStatement update = connection.prepareStatement(
                                "UPDATE menus SET stok=?,status='tersedia',"
                                + "updated_at=NOW() WHERE id_menu=?")) {
                            update.setInt(1, newStock);
                            update.setLong(2, id);
                            update.executeUpdate();
                        }
                        connection.commit();
                        return new ScanResult(name, barcode, newStock);
                    } catch (Exception exception) {
                        connection.rollback();
                        throw exception;
                    }
                }
            }

            @Override
            protected void done() {
                barcodeField.setEnabled(true);
                try {
                    ScanResult result = get();
                    statusText.setText("Berhasil: " + result.name()
                            + " bertambah 1 pcs.");
                    scanModel.insertRow(0, new Object[]{
                        result.name(), "+1", result.stock() + " pcs"
                    });
                    barcodeField.setText(result.barcode());
                    barcodeField.selectAll();
                    loadProducts();
                } catch (Exception exception) {
                    statusText.setText(rootMessage(exception));
                    barcodeField.selectAll();
                }
                barcodeField.requestFocusInWindow();
            }
        }.execute();
    }

    private JLabel dialogFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(244, 217, 197));
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton dialogButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createLineBorder(
                new Color(198, 151, 113)));
        button.setFocusPainted(false);
        button.setRolloverEnabled(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void selectDialogTab(JButton selected, JButton unselected) {
        selected.setBackground(CREAM);
        selected.setForeground(TEXT);
        unselected.setBackground(new Color(126, 78, 50));
        unselected.setForeground(WHITE);
        selected.revalidate();
        unselected.revalidate();
        selected.repaint();
        unselected.repaint();
    }

    private void showAddMenuDialog(String category) {
        JDialog dialog = new JDialog(this, "Tambah Menu", true);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        RoundedPanel root = new RoundedPanel(12, BROWN);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(154, 99, 64)),
                new EmptyBorder(18, 18, 16, 18)));
        root.setLayout(new BorderLayout(0, 12));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel headingBox = new JPanel();
        headingBox.setOpaque(false);
        headingBox.setLayout(new BoxLayout(headingBox, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel("Tambah " + category);
        heading.setForeground(WHITE);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Lengkapi data menu baru.");
        subtitle.setForeground(new Color(244, 217, 197));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        headingBox.add(heading);
        headingBox.add(subtitle);
        JButton topClose = dialogButton("×", CREAM, TEXT);
        topClose.setPreferredSize(new Dimension(38, 38));
        topClose.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topClose.addActionListener(event -> dialog.dispose());
        header.add(headingBox, BorderLayout.WEST);
        header.add(topClose, BorderLayout.EAST);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(2, 0, 2, 6));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        final File[] selectedImage = {null};
        final BufferedImage[] sourceImage = {null};
        JButton chooseImage = dialogButton(
                "Pilih Gambar Menu", CREAM, TEXT);
        chooseImage.setAlignmentX(Component.LEFT_ALIGNMENT);
        chooseImage.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chooseImage.setMinimumSize(new Dimension(450, 46));
        chooseImage.setPreferredSize(new Dimension(450, 46));
        chooseImage.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        JLabel imagePreview = new JLabel(
                "Pilih gambar, lalu atur zoom untuk menentukan tampilan.",
                SwingConstants.CENTER);
        imagePreview.setAlignmentX(Component.LEFT_ALIGNMENT);
        imagePreview.setOpaque(true);
        imagePreview.setBackground(new Color(151, 105, 76));
        imagePreview.setForeground(new Color(255, 235, 218));
        imagePreview.setFont(new Font("Segoe UI", Font.BOLD, 12));
        imagePreview.setBorder(BorderFactory.createLineBorder(
                new Color(213, 167, 128)));
        imagePreview.setMinimumSize(new Dimension(450, 150));
        imagePreview.setPreferredSize(new Dimension(450, 150));
        imagePreview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        JSlider zoomSlider = new JSlider(100, 220, 100);
        zoomSlider.setOpaque(false);
        zoomSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        zoomSlider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        chooseImage.addActionListener(event -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Pilih gambar menu");
            chooser.setFileFilter(new FileNameExtensionFilter(
                    "Gambar (JPG, PNG, GIF, BMP)",
                    "jpg", "jpeg", "png", "gif", "bmp"));
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage image = ImageIO.read(chooser.getSelectedFile());
                    if (image == null) {
                        throw new IllegalArgumentException(
                                "Format gambar tidak didukung.");
                    }
                    selectedImage[0] = chooser.getSelectedFile();
                    sourceImage[0] = image;
                    zoomSlider.setValue(100);
                    updateMenuImagePreview(imagePreview, image, 1.0);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(dialog,
                            "Gambar tidak dapat dibuka.\n"
                            + rootMessage(exception),
                            "Gambar Tidak Valid", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        zoomSlider.addChangeListener(event -> {
            if (sourceImage[0] != null) {
                updateMenuImagePreview(imagePreview, sourceImage[0],
                        zoomSlider.getValue() / 100.0);
            }
        });

        JTextField nameField = new JTextField();
        JTextField barcodeField = new JTextField();
        JTextField priceField = new JTextField();
        JTextArea descriptionField = new JTextArea(3, 20);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);
        configureAddMenuField(nameField, inputFont);
        configureAddMenuField(barcodeField, inputFont);
        configureAddMenuField(priceField, inputFont);
        descriptionField.setFont(inputFont);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setBackground(WHITE);
        descriptionField.setForeground(TEXT);
        descriptionField.setBorder(new EmptyBorder(10, 11, 10, 11));
        JScrollPane descriptionScroll = new JScrollPane(descriptionField);
        descriptionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        descriptionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

        JButton manualTab = dialogButton("Manual", CREAM, TEXT);
        JButton barcodeTab = dialogButton(
                "Lewat Barcode", new Color(126, 78, 50), WHITE);
        manualTab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        barcodeTab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JPanel tabs = new JPanel(new GridLayout(1, 2, 9, 0));
        tabs.setOpaque(false);
        tabs.setAlignmentX(Component.LEFT_ALIGNMENT);
        tabs.setMinimumSize(new Dimension(450, 48));
        tabs.setPreferredSize(new Dimension(450, 48));
        tabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        tabs.add(manualTab);
        tabs.add(barcodeTab);

        CardLayout barcodeModeLayout = new CardLayout();
        JPanel barcodeMode = new JPanel(barcodeModeLayout);
        barcodeMode.setOpaque(false);
        barcodeMode.setAlignmentX(Component.LEFT_ALIGNMENT);
        barcodeMode.setPreferredSize(new Dimension(450, 1));
        barcodeMode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        JPanel manualMode = new JPanel();
        manualMode.setOpaque(false);
        JPanel scanMode = new JPanel();
        scanMode.setOpaque(false);
        scanMode.setLayout(new BoxLayout(scanMode, BoxLayout.Y_AXIS));
        scanMode.add(dialogFieldLabel("BARCODE"));
        scanMode.add(Box.createVerticalStrut(5));
        scanMode.add(barcodeField);
        scanMode.add(Box.createVerticalStrut(6));
        JButton automaticBarcode = dialogButton(
                "Buat Barcode Otomatis",
                new Color(126, 78, 50), WHITE);
        automaticBarcode.setAlignmentX(Component.LEFT_ALIGNMENT);
        automaticBarcode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        automaticBarcode.addActionListener(event -> {
            String generated = String.valueOf(System.currentTimeMillis());
            barcodeField.setText(generated);
            barcodeField.requestFocusInWindow();
            barcodeField.selectAll();
        });
        scanMode.add(automaticBarcode);
        barcodeMode.add(manualMode, "manual");
        barcodeMode.add(scanMode, "barcode");
        manualTab.addActionListener(event -> {
            barcodeModeLayout.show(barcodeMode, "manual");
            barcodeField.setText("");
            barcodeMode.setPreferredSize(new Dimension(450, 1));
            barcodeMode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            form.revalidate();
            selectDialogTab(manualTab, barcodeTab);
        });
        barcodeTab.addActionListener(event -> {
            barcodeModeLayout.show(barcodeMode, "barcode");
            barcodeMode.setPreferredSize(new Dimension(450, 105));
            barcodeMode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
            form.revalidate();
            selectDialogTab(barcodeTab, manualTab);
            SwingUtilities.invokeLater(barcodeField::requestFocusInWindow);
        });

        form.add(dialogFieldLabel("GAMBAR MENU"));
        form.add(Box.createVerticalStrut(5));
        form.add(chooseImage);
        form.add(Box.createVerticalStrut(7));
        form.add(imagePreview);
        form.add(Box.createVerticalStrut(7));
        form.add(dialogFieldLabel("ZOOM GAMBAR"));
        form.add(zoomSlider);
        form.add(Box.createVerticalStrut(8));
        form.add(tabs);
        form.add(Box.createVerticalStrut(8));
        form.add(barcodeMode);
        form.add(Box.createVerticalStrut(7));
        form.add(dialogFieldLabel("NAMA " + category.toUpperCase(Locale.ROOT)));
        form.add(Box.createVerticalStrut(5));
        form.add(nameField);
        form.add(Box.createVerticalStrut(8));
        form.add(dialogFieldLabel("DESKRIPSI"));
        form.add(Box.createVerticalStrut(5));
        form.add(descriptionScroll);
        form.add(Box.createVerticalStrut(8));
        form.add(dialogFieldLabel("HARGA"));
        form.add(Box.createVerticalStrut(5));
        form.add(priceField);

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.getVerticalScrollBar().setUnitIncrement(14);
        formScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton cancel = dialogButton("Batal", WHITE, TEXT);
        JButton save = dialogButton("Simpan Menu", CREAM, TEXT);
        cancel.setPreferredSize(new Dimension(105, 46));
        save.setPreferredSize(new Dimension(138, 46));
        cancel.addActionListener(event -> dialog.dispose());
        save.addActionListener(event -> saveNewMenu(
                dialog, save, category, selectedImage[0],
                nameField.getText().trim(),
                barcodeField.getText().trim(),
                descriptionField.getText().trim(),
                priceField.getText().trim()));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0));
        actions.setOpaque(false);
        actions.add(cancel);
        actions.add(save);

        root.add(header, BorderLayout.NORTH);
        root.add(formScroll, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setSize(500, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void configureAddMenuField(JTextField field, Font font) {
        field.setFont(font);
        field.setForeground(TEXT);
        field.setBackground(WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(9, 11, 9, 11)));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
    }

    private void updateMenuImagePreview(
            JLabel preview, BufferedImage source, double zoom) {
        int width = 450;
        int height = 150;
        double coverScale = Math.max(
                (double) width / source.getWidth(),
                (double) height / source.getHeight());
        int imageWidth = Math.max(1,
                (int) Math.round(source.getWidth() * coverScale * zoom));
        int imageHeight = Math.max(1,
                (int) Math.round(source.getHeight() * coverScale * zoom));
        BufferedImage canvas = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = canvas.createGraphics();
        graphics.setColor(new Color(151, 105, 76));
        graphics.fillRect(0, 0, width, height);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(source,
                (width - imageWidth) / 2,
                (height - imageHeight) / 2,
                imageWidth, imageHeight, null);
        graphics.dispose();
        preview.setText("");
        preview.setIcon(new ImageIcon(canvas));
    }

    private void saveNewMenu(JDialog dialog, JButton saveButton,
            String category, File selectedImage, String name,
            String barcode, String description, String priceText) {
        java.math.BigDecimal price;
        try {
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Nama menu wajib diisi.");
            }
            if (name.length() > 150) {
                throw new IllegalArgumentException(
                        "Nama menu maksimal 150 karakter.");
            }
            if (description.length() > 300) {
                throw new IllegalArgumentException(
                        "Deskripsi maksimal 300 karakter.");
            }
            price = new java.math.BigDecimal(priceText.replace(",", "."));
            if (price.signum() < 0) {
                throw new IllegalArgumentException("Harga tidak boleh negatif.");
            }
            if (price.compareTo(new java.math.BigDecimal("50000")) > 0) {
                throw new IllegalArgumentException("Harga maksimal 50000.");
            }
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(dialog,
                    "Harga harus berupa angka yang valid.",
                    "Data Belum Valid", JOptionPane.WARNING_MESSAGE);
            return;
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(dialog, exception.getMessage(),
                    "Data Belum Valid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        saveButton.setEnabled(false);
        saveButton.setText("Menyimpan...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Connection connection = openConnection()) {
                    long categoryId;
                    try (PreparedStatement select = connection.prepareStatement(
                            "SELECT id_kategori FROM categories "
                            + "WHERE LOWER(nama_kategori)=LOWER(?) LIMIT 1")) {
                        select.setString(1, category);
                        try (ResultSet result = select.executeQuery()) {
                            if (!result.next()) {
                                throw new SQLException(
                                        "Kategori " + category + " tidak ditemukan.");
                            }
                            categoryId = result.getLong("id_kategori");
                        }
                    }
                    String sql = "INSERT INTO menus "
                            + "(id_kategori,nama_menu,barcode,deskripsi,harga,"
                            + "foto,stok,status,created_at,updated_at) "
                            + "VALUES (?,?,?,?,?,?,0,'habis',NOW(),NOW())";
                    try (PreparedStatement insert = connection.prepareStatement(sql)) {
                        insert.setLong(1, categoryId);
                        insert.setString(2, name);
                        if (barcode.isEmpty()) {
                            insert.setNull(3, java.sql.Types.VARCHAR);
                        } else {
                            insert.setString(3, barcode);
                        }
                        insert.setString(4, description);
                        insert.setBigDecimal(5, price);
                        if (selectedImage == null) {
                            insert.setNull(6, java.sql.Types.VARCHAR);
                        } else {
                            insert.setString(6, selectedImage.getAbsolutePath());
                        }
                        insert.executeUpdate();
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(KelolaStok.this,
                            name + " berhasil ditambahkan ke " + category + ".",
                            "Menu Ditambahkan", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } catch (Exception exception) {
                    saveButton.setEnabled(true);
                    saveButton.setText("Simpan Menu");
                    JOptionPane.showMessageDialog(dialog,
                            "Menu tidak dapat ditambahkan.\n"
                            + rootMessage(exception),
                            "Gagal Menambahkan", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void loadProducts() {
        loadingLabel.setText("Memuat produk...");
        loadingLabel.setVisible(true);
        new SwingWorker<List<Product>, Void>() {
            private boolean usingFallback;

            @Override
            protected List<Product> doInBackground() {
                try {
                    List<Product> products = readProducts();
                    if (!products.isEmpty()) {
                        return products;
                    }
                } catch (SQLException ignored) {
                    // Tampilan tetap dapat dipreview meskipun MySQL belum aktif.
                }
                usingFallback = true;
                return sampleProducts();
            }

            @Override
            protected void done() {
                try {
                    applyProducts(get());
                    loadingLabel.setText(usingFallback
                            ? "Mode preview — hubungkan database swiftbite untuk mengubah stok."
                            : "");
                    loadingLabel.setVisible(usingFallback);
                } catch (Exception exception) {
                    loadingLabel.setText("Produk gagal dimuat.");
                }
            }
        }.execute();
    }

    private List<Product> readProducts() throws SQLException {
        String sql = "SELECT m.id_menu,m.nama_menu,c.nama_kategori,m.stok,m.foto "
                + "FROM menus m JOIN categories c "
                + "ON c.id_kategori=m.id_kategori ORDER BY m.nama_menu";
        List<Product> products = new ArrayList<>();
        try (Connection connection = openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                products.add(new Product(
                        result.getLong("id_menu"),
                        result.getString("nama_menu"),
                        result.getString("nama_kategori"),
                        result.getInt("stok"),
                        result.getString("foto")));
            }
        }
        return products;
    }

    private void applyProducts(List<Product> products) {
        foodCards.removeAll();
        drinkCards.removeAll();
        int safe = 0;
        int low = 0;
        int empty = 0;
        int foods = 0;
        int drinks = 0;
        for (Product product : products) {
            if (product.stock() <= 0) {
                empty++;
            } else if (product.stock() <= 5) {
                low++;
            } else {
                safe++;
            }
            JPanel card = createProductCard(product);
            if (isDrink(product)) {
                drinkCards.add(card);
                drinkCards.add(Box.createHorizontalStrut(14));
                drinks++;
            } else {
                foodCards.add(card);
                foodCards.add(Box.createHorizontalStrut(14));
                foods++;
            }
        }
        totalValue.setText(String.valueOf(products.size()));
        safeValue.setText(String.valueOf(safe));
        lowValue.setText(String.valueOf(low));
        emptyValue.setText(String.valueOf(empty));
        foodCount.setText(foods + " Produk");
        drinkCount.setText(drinks + " Produk");
        foodCards.revalidate();
        drinkCards.revalidate();
        foodCards.repaint();
        drinkCards.repaint();
    }

    private boolean isDrink(Product product) {
        String category = normalize(product.category());
        String name = normalize(product.name());
        return category.contains("minum")
                || category.contains("drink")
                || name.contains("americano")
                || name.contains("latte")
                || name.contains("cappuccino")
                || name.contains("milk")
                || name.contains("tea")
                || name.contains("water")
                || name.contains("mocha");
    }

    private ImageIcon productImage(Product product) {
        if (product.imagePath() != null && !product.imagePath().isBlank()) {
            File imageFile = new File(product.imagePath());
            if (imageFile.isFile()) {
                Image image = new ImageIcon(imageFile.getAbsolutePath()).getImage()
                        .getScaledInstance(188, 142, Image.SCALE_SMOOTH);
                return new ImageIcon(image);
            }
        }
        String normalized = normalize(product.name());
        String[][] aliases = {
            {"almond croissant", "almond-croissant.jpg"},
            {"butter croissant", "butter-croissant.jpg"},
            {"chocolate croissant", "chocolate-croissant.jpg"},
            {"cheese bread", "cheese-bread.jpg"},
            {"chicken floss bread", "chicken-floss-bread.jpg"},
            {"pain au chocolat", "pain-au-chocolat.jpg"},
            {"sausage roll", "sausage-roll.jpg"},
            {"cinnamon roll", "cinnamon-roll.jpg"},
            {"donut glazed", "donut-glazed.jpg"},
            {"glazed donut", "donut-glazed.jpg"},
            {"chocolate muffin", "chocolate-muffin.webp"},
            {"americano", "americano.jpg"},
            {"cafe latte", "cafe-latte.jpg"},
            {"caffe latte", "cafe-latte.jpg"},
            {"cappuccino", "cappuccino.jpg"},
            {"chocolate milk", "chocolate-milk.jpg"},
            {"lemon tea", "lemon-tea.jpg"},
            {"matcha latte", "matcha-latte.jpg"},
            {"mineral water", "mineral-water.jpg"},
            {"mocha", "mocha.jpg"}
        };
        for (String[] alias : aliases) {
            if (normalized.contains(alias[0])) {
                URL url = getClass().getResource(
                        "/morning_bakery/assets/" + alias[1]);
                if (url != null) {
                    Image image = new ImageIcon(url).getImage()
                            .getScaledInstance(188, 142, Image.SCALE_SMOOTH);
                    return new ImageIcon(image);
                }
            }
        }
        return null;
    }

    private List<Product> sampleProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(-1, "Almond Croissant", "Makanan", 0, null));
        products.add(new Product(-2, "Butter Croissant", "Makanan", 15, null));
        products.add(new Product(-3, "Cheese Bread", "Makanan", 21, null));
        products.add(new Product(-4, "Chicken Floss Bread", "Makanan", 15, null));
        products.add(new Product(-5, "Chocolate Croissant", "Makanan", 11, null));
        products.add(new Product(-6, "Chocolate Muffin", "Makanan", 4, null));
        products.add(new Product(-7, "Cinnamon Roll", "Makanan", 13, null));
        products.add(new Product(-8, "Donut Glazed", "Makanan", 17, null));
        products.add(new Product(-9, "Pain au Chocolat", "Makanan", 12, null));
        products.add(new Product(-10, "Sausage Roll", "Makanan", 18, null));
        products.add(new Product(-11, "Croissant Original", "Makanan", 10, null));
        products.add(new Product(-12, "Sweet Bread", "Makanan", 14, null));
        products.add(new Product(-13, "Americano", "Minuman", 29, null));
        products.add(new Product(-14, "Cafe Latte", "Minuman", 23, null));
        products.add(new Product(-15, "Cappuccino", "Minuman", 25, null));
        products.add(new Product(-16, "Chocolate Milk", "Minuman", 20, null));
        products.add(new Product(-17, "Lemon Tea", "Minuman", 16, null));
        products.add(new Product(-18, "Matcha Latte", "Minuman", 22, null));
        products.add(new Product(-19, "Mineral Water", "Minuman", 30, null));
        products.add(new Product(-20, "Mocha", "Minuman", 19, null));
        return products;
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

    private static JLabel statisticValue() {
        JLabel label = new JLabel("0");
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 29));
        return label;
    }

    private static JLabel countBadge() {
        JLabel label = new JLabel("0 Produk", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(255, 255, 255, 28));
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setBorder(new EmptyBorder(5, 10, 5, 10));
        return label;
    }

    private static JPanel cardsStrip() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        return panel;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String withoutAccent = Normalizer.normalize(
                value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return withoutAccent.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ").trim();
    }

    private static String ellipsis(String value, int maxLength) {
        return value.length() <= maxLength
                ? value : value.substring(0, maxLength - 1) + "…";
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String rootMessage(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() == null
                ? "Periksa koneksi database." : cause.getMessage();
    }

    private record Product(long id, String name, String category, int stock,
            String imagePath) {
    }

    private record ScanResult(String name, String barcode, int stock) {
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
                    RenderingHints.KEY_ANTIALIASING,
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
        private final int radius;

        GradientPanel(Color start, Color end, int radius) {
            this.start = start;
            this.end = end;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setPaint(new GradientPaint(
                    0, 0, start, getWidth(), getHeight(), end));
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
        SwingUtilities.invokeLater(() -> new KelolaStok().setVisible(true));
    }
}
