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
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

/** Halaman Data Absensi untuk Manager SwiftBite. */
public class DataAbsensi extends JFrame {

    private static final String DB_URL
            = "jdbc:mysql://127.0.0.1:3306/swiftbite"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final DateTimeFormatter DATE_FORMAT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT
            = DateTimeFormatter.ofPattern("HH:mm");

    private static final Color PAGE = new Color(249, 247, 244);
    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color CREAM = new Color(255, 247, 235);
    private static final Color BROWN_DARK = new Color(58, 28, 19);
    private static final Color BROWN = new Color(91, 48, 29);
    private static final Color BROWN_MID = new Color(132, 79, 47);
    private static final Color BROWN_LIGHT = new Color(166, 105, 67);
    private static final Color MUTED = new Color(235, 205, 184);

    private final long managerId;
    private final String managerName;
    private final JTextField nameFilter = new JTextField();
    private final JComboBox<String> roleFilter = new JComboBox<>(new String[]{
        "Semua Role", "Waiter", "Baker", "Kasir", "Manager", "Owner"
    });
    private final JTextField dateFilter = new JTextField();
    private final JComboBox<String> statusFilter = new JComboBox<>(new String[]{
        "Semua Status", "Hadir", "Terlambat", "Izin", "Sakit", "Tidak Hadir"
    });
    private final JLabel recordCount = new JLabel("Menampilkan 0 catatan absensi.");
    private final AbsensiTableModel tableModel = new AbsensiTableModel();
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<AbsensiTableModel> sorter
            = new TableRowSorter<>(tableModel);

    public DataAbsensi() {
        this(3L, "manager");
    }

    public DataAbsensi(long managerId, String managerName) {
        this.managerId = managerId;
        this.managerName = managerName == null || managerName.isBlank()
                ? "manager" : managerName;
        setTitle("SwiftBite - Data Absensi");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1060, 680));
        setSize(1366, 768);
        setLocationRelativeTo(null);
        loadWindowIcon();
        initComponentsCustom();
        initFilterOptions();
        loadAbsensiData();
    }

    private void initComponentsCustom() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE);
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createContent(), BorderLayout.CENTER);
        setContentPane(root);
        configureAbsensiTable();
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
        sidebar.add(navButton("Data User", false, event -> {
            new DataUser(managerId, managerName).setVisible(true);
            dispose();
        }));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navButton("Data Absensi", true, null));
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
        return button;
    }

    private JPanel createAccountCard() {
        RoundedPanel card = new RoundedPanel(9, new Color(255, 255, 255, 30));
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 11, 11));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setPreferredSize(new Dimension(224, 67));
        card.setMaximumSize(new Dimension(224, 67));
        card.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 55)));
        JLabel initial = new JLabel(managerName.substring(0, 1).toUpperCase(),
                SwingConstants.CENTER);
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
        JPopupMenu popup = new JPopupMenu();
        JButton logout = actionButton("Logout", WHITE, BROWN_DARK);
        logout.setPreferredSize(new Dimension(218, 42));
        logout.setHorizontalAlignment(SwingConstants.LEFT);
        logout.addActionListener(event -> {
            popup.setVisible(false);
            dispose();
            new login().setVisible(true);
        });
        popup.add(logout);
        MouseAdapter showPopup = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                popup.show(card, 0, -popup.getPreferredSize().height - 4);
            }
        };
        card.addMouseListener(showPopup);
        initial.addMouseListener(showPopup);
        name.addMouseListener(showPopup);
        role.addMouseListener(showPopup);
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
        JPanel history = createHistoryPanel();
        history.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(history);
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
        header.setPreferredSize(new Dimension(900, 152));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 152));
        JLabel eyebrow = new JLabel("MANAGER OPERASIONAL");
        eyebrow.setForeground(new Color(255, 228, 202));
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel title = new JLabel("Data Absensi");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 43));
        JLabel subtitle = new JLabel(
                "Lihat status kehadiran karyawan, jam kerja, dan detail verifikasi absensi.");
        subtitle.setForeground(new Color(255, 235, 218));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        header.add(eyebrow);
        header.add(Box.createVerticalStrut(4));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        return header;
    }

    private JPanel createHistoryPanel() {
        RoundedPanel panel = new RoundedPanel(10, BROWN_MID);
        panel.setBorder(new EmptyBorder(20, 18, 20, 18));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(900, 530));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 530));
        JLabel title = new JLabel("Riwayat Absensi");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        recordCount.setForeground(new Color(255, 222, 198));
        recordCount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(recordCount);
        panel.add(Box.createVerticalStrut(15));
        panel.add(createFilterPanel());
        panel.add(Box.createVerticalStrut(15));
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableScroll.setBorder(BorderFactory.createLineBorder(
                new Color(255, 255, 255, 40)));
        tableScroll.getViewport().setBackground(BROWN);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScroll.getVerticalScrollBar().setUnitIncrement(16);
        tableScroll.setPreferredSize(new Dimension(900, 275));
        tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 275));
        panel.add(tableScroll);
        return panel;
    }

    private JPanel createFilterPanel() {
        RoundedPanel filters = new RoundedPanel(8, new Color(255, 255, 255, 22));
        filters.setLayout(new GridBagLayout());
        filters.setBorder(new EmptyBorder(12, 14, 12, 14));
        filters.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));
        configureFilterField(nameFilter, "Cari nama karyawan");
        configureFilterField(dateFilter, "dd/MM/yyyy");
        configureCombo(roleFilter);
        configureCombo(statusFilter);
        JButton apply = actionButton("Filter", CREAM, BROWN_DARK);
        JButton reset = actionButton("Reset", BROWN, WHITE);
        apply.addActionListener(event -> applyAbsensiFilter());
        reset.addActionListener(event -> resetAbsensiFilter());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 5, 10);
        c.gridx = 0; c.weightx = .34; c.fill = GridBagConstraints.HORIZONTAL;
        filters.add(filterLabel("NAMA"), c);
        c.gridx = 1; c.weightx = .19; filters.add(filterLabel("ROLE"), c);
        c.gridx = 2; c.weightx = .20; filters.add(filterLabel("TANGGAL"), c);
        c.gridx = 3; c.weightx = .19; filters.add(filterLabel("STATUS"), c);
        c.gridy = 1;
        c.gridx = 0; c.weightx = .34; filters.add(nameFilter, c);
        c.gridx = 1; c.weightx = .19; filters.add(roleFilter, c);
        c.gridx = 2; c.weightx = .20; filters.add(dateFilter, c);
        c.gridx = 3; c.weightx = .19; filters.add(statusFilter, c);
        c.gridx = 4; c.weightx = .04; filters.add(apply, c);
        c.gridx = 5; c.weightx = .04; c.insets = new Insets(0, 0, 5, 0);
        filters.add(reset, c);
        return filters;
    }

    private void initFilterOptions() {
        nameFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { }
            @Override public void removeUpdate(DocumentEvent event) { }
            @Override public void changedUpdate(DocumentEvent event) { }
        });
    }

    private void configureAbsensiTable() {
        table.setRowSorter(sorter);
        table.setRowHeight(62);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(255, 255, 255, 42));
        table.setBackground(BROWN);
        table.setForeground(WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(150, 94, 57));
        table.setSelectionForeground(WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 44));
        table.getTableHeader().setBackground(new Color(160, 106, 75));
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setReorderingAllowed(false);
        int[] widths = {190, 90, 110, 100, 105, 130, 110, 90};
        for (int index = 0; index < widths.length; index++) {
            TableColumn column = table.getColumnModel().getColumn(index);
            column.setPreferredWidth(widths[index]);
        }
        table.getColumnModel().getColumn(0).setCellRenderer(new NameRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new DetailButtonEditor());
        DefaultTableCellRenderer centered = new DefaultTableCellRenderer();
        centered.setHorizontalAlignment(SwingConstants.CENTER);
        centered.setForeground(WHITE);
        centered.setBackground(BROWN);
        for (int index = 1; index <= 5; index++) {
            table.getColumnModel().getColumn(index).setCellRenderer(centered);
        }
    }

    private void loadAbsensiData() {
        recordCount.setText("Memuat data absensi...");
        new SwingWorker<List<Absensi>, Void>() {
            @Override
            protected List<Absensi> doInBackground() throws Exception {
                List<Absensi> records = new ArrayList<>();
                String sql = "SELECT a.id_absensi,a.id_user,u.name,u.email,"
                        + "COALESCE(u.level,0) level,a.tanggal,a.jam_masuk,"
                        + "a.jam_keluar,a.status,a.foto_masuk,a.foto_pulang,"
                        + "a.created_at,a.updated_at "
                        + "FROM absensis a JOIN users u ON u.id_user=a.id_user "
                        + "ORDER BY a.tanggal DESC,a.jam_masuk DESC";
                try (Connection connection = openConnection();
                        PreparedStatement statement = connection.prepareStatement(sql);
                        ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        LocalDate date = result.getDate("tanggal").toLocalDate();
                        java.sql.Time masuk = result.getTime("jam_masuk");
                        java.sql.Time pulang = result.getTime("jam_keluar");
                        records.add(new Absensi(result.getLong("id_absensi"),
                                result.getLong("id_user"), result.getString("name"),
                                result.getString("email"), result.getInt("level"), date,
                                masuk == null ? null : masuk.toLocalTime(),
                                pulang == null ? null : pulang.toLocalTime(),
                                result.getString("status"), result.getString("foto_masuk"),
                                result.getString("foto_pulang"),
                                result.getTimestamp("created_at"),
                                result.getTimestamp("updated_at")));
                    }
                }
                return records;
            }

            @Override
            protected void done() {
                try {
                    tableModel.setRecords(get());
                    applyAbsensiFilter();
                } catch (Exception exception) {
                    recordCount.setText("Data absensi gagal dimuat.");
                    JOptionPane.showMessageDialog(DataAbsensi.this,
                            "Data absensi tidak dapat dimuat.\n" + rootMessage(exception),
                            "Koneksi Database", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void applyAbsensiFilter() {
        String keyword = nameFilter.getText().trim().toLowerCase(Locale.ROOT);
        String role = (String) roleFilter.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();
        LocalDate selectedDate = parseDateFilter();
        if (!dateFilter.getText().trim().isEmpty()
                && !"dd/MM/yyyy".equalsIgnoreCase(dateFilter.getText().trim())
                && selectedDate == null) {
            JOptionPane.showMessageDialog(this,
                    "Format tanggal harus dd/MM/yyyy.",
                    "Tanggal Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return;
        }
        sorter.setRowFilter(new RowFilter<AbsensiTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends AbsensiTableModel,
                    ? extends Integer> entry) {
                Absensi record = entry.getModel().getAbsensiAt(entry.getIdentifier());
                boolean nameMatch = keyword.isEmpty()
                        || record.name().toLowerCase(Locale.ROOT).contains(keyword);
                boolean roleMatch = "Semua Role".equals(role)
                        || mapRoleDisplay(record.level()).equals(role);
                boolean dateMatch = selectedDate == null || selectedDate.equals(record.date());
                boolean statusMatch = "Semua Status".equals(status)
                        || mapStatusDisplay(record.status()).equals(status);
                return nameMatch && roleMatch && dateMatch && statusMatch;
            }
        });
        updateRecordCount();
    }

    private void resetAbsensiFilter() {
        nameFilter.setText("");
        dateFilter.setText("");
        roleFilter.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
        applyAbsensiFilter();
    }

    private void updateRecordCount() {
        recordCount.setText("Menampilkan " + table.getRowCount()
                + " catatan absensi.");
    }

    private LocalDate parseDateFilter() {
        String text = dateFilter.getText().trim();
        if (text.isEmpty() || "dd/MM/yyyy".equalsIgnoreCase(text)) {
            return null;
        }
        try {
            return LocalDate.parse(text, DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private void openAbsensiDetail(long idAbsensi) {
        Absensi record = tableModel.findById(idAbsensi);
        if (record == null) {
            return;
        }
        JDialog dialog = new JDialog(this, "Detail Absensi", true);
        dialog.setUndecorated(true);
        RoundedPanel root = new RoundedPanel(11, BROWN);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 105, 70)),
                new EmptyBorder(18, 18, 18, 18)));
        root.setLayout(new BorderLayout(0, 12));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Detail Absensi");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JButton close = actionButton("X", CREAM, BROWN_DARK);
        close.setPreferredSize(new Dimension(40, 40));
        close.setBorder(new EmptyBorder(0, 0, 0, 0));
        close.addActionListener(event -> dialog.dispose());
        header.add(title, BorderLayout.WEST);
        header.add(close, BorderLayout.EAST);
        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.add(detailRow("Nama", record.name()));
        details.add(detailRow("Email", record.email()));
        details.add(detailRow("Role", mapRoleDisplay(record.level())));
        details.add(detailRow("Tanggal", record.date().format(DATE_FORMAT)));
        details.add(detailRow("Jam Masuk", timeText(record.masuk())));
        details.add(detailRow("Jam Pulang", timeText(record.pulang())));
        details.add(detailRow("Durasi Kerja", calculateDuration(record.masuk(), record.pulang())));
        details.add(detailRow("Status", mapStatusDisplay(record.status())));
        details.add(detailRow("Keterangan", "-"));
        details.add(detailRow("Verifikasi Masuk", blankAsDash(record.fotoMasuk())));
        details.add(detailRow("Verifikasi Pulang", blankAsDash(record.fotoPulang())));
        root.add(header, BorderLayout.NORTH);
        root.add(details, BorderLayout.CENTER);
        dialog.setContentPane(root);
        dialog.setSize(520, 570);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private String calculateDuration(LocalTime masuk, LocalTime pulang) {
        if (masuk == null || pulang == null) {
            return "-";
        }
        long minutes = Duration.between(masuk, pulang).toMinutes();
        if (minutes < 0) {
            minutes += 24 * 60;
        }
        return minutes / 60 + " jam " + minutes % 60 + " menit";
    }

    private String mapRoleDisplay(int level) {
        return switch (level) {
            case 1 -> "Waiter";
            case 2 -> "Baker";
            case 3 -> "Kasir";
            case 4 -> "Manager";
            case 5 -> "Owner";
            default -> "Staff";
        };
    }

    private String mapStatusDisplay(String status) {
        if (status == null) return "Tidak Hadir";
        return switch (status.toLowerCase(Locale.ROOT)) {
            case "hadir" -> "Hadir";
            case "terlambat" -> "Terlambat";
            case "izin" -> "Izin";
            case "sakit" -> "Sakit";
            case "alpha", "tidak_hadir", "tidak hadir" -> "Tidak Hadir";
            default -> status;
        };
    }

    private void configureFilterField(JTextField field, String hint) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(WHITE);
        field.setForeground(BROWN_DARK);
        field.setToolTipText(hint);
        if ("dd/MM/yyyy".equals(hint)) field.setText(hint);
        field.setBorder(new EmptyBorder(10, 12, 10, 12));
    }

    private void configureCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(WHITE);
        combo.setForeground(BROWN_DARK);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean selected, boolean focus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, focus);
                label.setBorder(new EmptyBorder(7, 9, 7, 9));
                label.setBackground(selected ? BROWN : WHITE);
                label.setForeground(selected ? WHITE : BROWN_DARK);
                return label;
            }
        });
    }

    private JLabel filterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        return label;
    }

    private JButton actionButton(String text, Color background, Color foreground) {
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

    private JPanel detailRow(String labelText, String valueText) {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0,
                        new Color(255, 255, 255, 45)),
                new EmptyBorder(8, 0, 8, 0)));
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

    private String timeText(LocalTime time) {
        return time == null ? "-" : time.format(TIME_FORMAT);
    }

    private String blankAsDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void loadWindowIcon() {
        URL url = getClass().getResource("/morning_bakery/assets/Swiftbite-icon.png");
        if (url != null) setIconImage(new ImageIcon(url).getImage());
    }

    private static String rootMessage(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) cause = cause.getCause();
        return cause.getMessage() == null ? "Periksa koneksi database." : cause.getMessage();
    }

    private final class NameRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable source, Object value,
                boolean selected, boolean focus, int row, int column) {
            Absensi record = tableModel.getAbsensiAt(source.convertRowIndexToModel(row));
            JLabel label = new JLabel("<html><b>" + escapeHtml(record.name())
                    + "</b><br><span style='font-size:10px'>"
                    + escapeHtml(record.email()) + "</span></html>");
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(0, 14, 0, 4));
            label.setForeground(WHITE);
            label.setBackground(selected ? new Color(150, 94, 57) : BROWN);
            return label;
        }
    }

    private final class StatusRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable source, Object value,
                boolean selected, boolean focus, int row, int column) {
            String status = String.valueOf(value);
            Color[] colors = statusColors(status);
            JLabel badge = new JLabel(status, SwingConstants.CENTER);
            badge.setOpaque(true);
            badge.setBackground(colors[0]);
            badge.setForeground(colors[1]);
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            badge.setBorder(new EmptyBorder(6, 9, 6, 9));
            JPanel holder = new JPanel(new GridBagLayout());
            holder.setOpaque(true);
            holder.setBackground(selected ? new Color(150, 94, 57) : BROWN);
            holder.add(badge);
            return holder;
        }
    }

    private final class ButtonRenderer extends JButton implements TableCellRenderer {
        ButtonRenderer() {
            setText("Detail");
            setUI(new BasicButtonUI());
            setBackground(CREAM);
            setForeground(BROWN_DARK);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBorder(new EmptyBorder(8, 13, 8, 13));
            setFocusPainted(false);
        }
        @Override
        public Component getTableCellRendererComponent(JTable source, Object value,
                boolean selected, boolean focus, int row, int column) {
            JPanel holder = new JPanel(new GridBagLayout());
            holder.setOpaque(true);
            holder.setBackground(selected ? new Color(150, 94, 57) : BROWN);
            holder.add(this);
            return holder;
        }
    }

    private final class DetailButtonEditor extends AbstractCellEditor
            implements TableCellEditor {
        private final JButton button = actionButton("Detail", CREAM, BROWN_DARK);
        private long idAbsensi;
        DetailButtonEditor() {
            button.addActionListener(event -> {
                stopCellEditing();
                openAbsensiDetail(idAbsensi);
            });
        }
        @Override public Object getCellEditorValue() { return "Detail"; }
        @Override public Component getTableCellEditorComponent(JTable source,
                Object value, boolean selected, int viewRow, int column) {
            int modelRow = source.convertRowIndexToModel(viewRow);
            idAbsensi = tableModel.getAbsensiAt(modelRow).idAbsensi();
            return button;
        }
    }

    private static Color[] statusColors(String status) {
        return switch (status) {
            case "Hadir" -> new Color[]{new Color(218, 252, 207), new Color(36, 111, 45)};
            case "Terlambat" -> new Color[]{new Color(255, 230, 183), new Color(166, 93, 14)};
            case "Izin" -> new Color[]{new Color(211, 233, 255), new Color(41, 91, 159)};
            case "Sakit" -> new Color[]{new Color(241, 215, 244), new Color(125, 57, 123)};
            default -> new Color[]{new Color(255, 221, 216), new Color(167, 44, 36)};
        };
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private record Absensi(long idAbsensi, long idUser, String name, String email,
            int level, LocalDate date, LocalTime masuk, LocalTime pulang,
            String status, String fotoMasuk, String fotoPulang,
            java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt) { }

    private final class AbsensiTableModel extends AbstractTableModel {
        private final String[] columns = {"NAMA", "ROLE", "TANGGAL", "JAM MASUK",
            "JAM PULANG", "DURASI KERJA", "STATUS", "AKSI"};
        private final List<Absensi> records = new ArrayList<>();
        void setRecords(List<Absensi> values) {
            records.clear(); records.addAll(values); fireTableDataChanged();
        }
        Absensi getAbsensiAt(int row) { return records.get(row); }
        Absensi findById(long id) { return records.stream().filter(a -> a.idAbsensi() == id)
                .findFirst().orElse(null); }
        @Override public int getRowCount() { return records.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }
        @Override public boolean isCellEditable(int row, int column) { return column == 7; }
        @Override public Object getValueAt(int row, int column) {
            Absensi a = records.get(row);
            return switch (column) {
                case 0 -> a.name();
                case 1 -> mapRoleDisplay(a.level());
                case 2 -> a.date().format(DATE_FORMAT);
                case 3 -> timeText(a.masuk());
                case 4 -> timeText(a.pulang());
                case 5 -> calculateDuration(a.masuk(), a.pulang());
                case 6 -> mapStatusDisplay(a.status());
                default -> "Detail";
            };
        }
    }

    private static final class RoundedPanel extends JPanel {
        private final int radius; private final Color fill;
        RoundedPanel(int radius, Color fill) { this.radius = radius; this.fill = fill; setOpaque(false); }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill); g.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g.dispose(); super.paintComponent(graphics);
        }
    }

    private static final class GradientPanel extends JPanel {
        private final Color start; private final Color end;
        GradientPanel(Color start, Color end) { this.start = start; this.end = end; setOpaque(false); }
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g.fillRect(0, 0, getWidth(), getHeight()); g.dispose(); super.paintComponent(graphics);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new DataAbsensi().setVisible(true));
    }
}
