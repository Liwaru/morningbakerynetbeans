package morning_bakery.owner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.JTextArea;

/** Isi Dashboard Owner. Hanya panel ini yang dapat di-scroll. */
public final class OwnerDashboardPanel extends JPanel {

    private static final Color PAGE = new Color(249, 247, 244);
    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color MUTED = new Color(245, 218, 195);
    private static final Color CARD_START = new Color(143, 86, 51);
    private static final Color CARD_END = new Color(73, 35, 24);
    private static final Color ROW = new Color(255, 255, 255, 18);
    private static final Color DIVIDER = new Color(255, 255, 255, 48);

    private static final List<String> STATUS_KEYS = List.of(
            "menunggu",
            "diproses",
            "siap_diantar",
            "menunggu_pembayaran",
            "selesai",
            "dibatalkan");
    private static final List<String> STATUS_LABELS = List.of(
            "Menunggu",
            "Diproses",
            "Siap Diantar",
            "Menunggu Pembayaran",
            "Selesai",
            "Dibatalkan");
    private static final List<String> PAYMENT_LABELS = List.of(
            "Tunai",
            "QRIS",
            "GoPay",
            "DANA",
            "OVO",
            "ShopeePay",
            "E-Wallet Lain");

    private final OwnerDashboardDAO dashboardDAO = new OwnerDashboardDAO();
    private final DecimalFormat rupiahFormat;
    private final StatCard revenueCard
            = new StatCard("Pendapatan Hari Ini", "Rp0");
    private final StatCard orderCountCard
            = new StatCard("Total Pesanan Hari Ini", "0");
    private final StatCard averageCard
            = new StatCard("Rata-rata Transaksi", "Rp0");
    private final StatCard bestMenuCard
            = new StatCard("Menu Terlaris", "-");
    private final Map<String, JLabel> statusValues = new LinkedHashMap<>();
    private final Map<String, JLabel> paymentValues = new LinkedHashMap<>();
    private final Map<DayOfWeek, WeeklyRevenueItem> weeklyItems
            = new EnumMap<>(DayOfWeek.class);
    private final JPanel topMenuBody = transparentPanel();
    private SwingWorker<OwnerDashboardData, Void> currentWorker;

    public OwnerDashboardPanel() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(
                Locale.forLanguageTag("id-ID"));
        symbols.setGroupingSeparator('.');
        rupiahFormat = new DecimalFormat("#,##0", symbols);
        initUI();
        refreshDashboard();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(PAGE);

        DashboardContentPanel content = new DashboardContentPanel();
        content.setBackground(PAGE);
        content.setBorder(BorderFactory.createEmptyBorder(32, 30, 34, 30));
        content.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;

        c.gridy = 0;
        content.add(createHeaderPanel(), c);

        c.gridy = 1;
        c.insets = new Insets(14, 0, 0, 0);
        content.add(createSummaryCardsPanel(), c);

        c.gridy = 2;
        c.insets = new Insets(16, 0, 0, 0);
        content.add(createDetailsGrid(), c);

        c.gridy = 3;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);
        content.add(Box.createGlue(), c);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setViewportBorder(null);
        scrollPane.getViewport().setBackground(PAGE);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        RoundedGradientPanel header = new RoundedGradientPanel(
                new BorderLayout(),
                new Color(154, 94, 54),
                new Color(53, 25, 18),
                14);
        header.setBorder(BorderFactory.createEmptyBorder(22, 22, 20, 22));
        header.setPreferredSize(new Dimension(0, 176));

        JPanel text = transparentPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("OWNER REPORT");
        eyebrow.setForeground(MUTED);
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Dashboard Owner");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea description = new JTextArea(
                "Pantau performa bisnis SwiftBite Morning Bakery dari pendapatan, "
                + "transaksi, menu terlaris, dan pola pembayaran pelanggan.");
        description.setEditable(false);
        description.setFocusable(false);
        description.setOpaque(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setForeground(new Color(255, 235, 218));
        description.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        description.setRows(2);
        description.setBorder(null);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        description.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        text.add(eyebrow);
        text.add(Box.createVerticalStrut(7));
        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(description);
        header.add(text, BorderLayout.CENTER);
        return header;
    }

    private JPanel createSummaryCardsPanel() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(0, 92));
        cards.add(revenueCard);
        cards.add(orderCountCard);
        cards.add(averageCard);
        cards.add(bestMenuCard);
        return cards;
    }

    private JPanel createDetailsGrid() {
        JPanel grid = transparentPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        c.gridx = 0;
        c.weightx = .58;
        c.insets = new Insets(0, 0, 14, 8);
        grid.add(createOrderSummaryPanel(), c);

        c.gridx = 1;
        c.weightx = .42;
        c.insets = new Insets(0, 8, 14, 0);
        grid.add(createWeeklyRevenuePanel(), c);

        c.gridy = 1;
        c.gridx = 0;
        c.weightx = .58;
        c.insets = new Insets(0, 0, 0, 8);
        grid.add(createTopMenuPanel(), c);

        c.gridx = 1;
        c.weightx = .42;
        c.insets = new Insets(0, 8, 0, 0);
        grid.add(createPaymentMethodPanel(), c);
        return grid;
    }

    private JPanel createOrderSummaryPanel() {
        RoundedGradientPanel card = dashboardCard(
                "Ringkasan Transaksi Hari Ini", 390);
        JPanel body = transparentPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 0);
        addSummaryRow(body, c, "STATUS", "JUMLAH", true, true);

        for (int index = 0; index < STATUS_KEYS.size(); index++) {
            JLabel value = new JLabel("0", SwingConstants.RIGHT);
            statusValues.put(STATUS_KEYS.get(index), value);
            c.gridy++;
            addSummaryRow(body, c, STATUS_LABELS.get(index), value, false, false);
        }

        JLabel totalValue = new JLabel("0", SwingConstants.RIGHT);
        statusValues.put("total", totalValue);
        c.gridy++;
        addSummaryRow(body, c, "Total Pesanan", totalValue, false, true);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel createWeeklyRevenuePanel() {
        RoundedGradientPanel card = dashboardCard(
                "Pendapatan Minggu Ini", 390);
        JPanel body = transparentPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        String[] dayNames = {
            "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"
        };
        DayOfWeek[] days = DayOfWeek.values();
        for (int index = 0; index < days.length; index++) {
            WeeklyRevenueItem item = new WeeklyRevenueItem(dayNames[index]);
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
            weeklyItems.put(days[index], item);
            body.add(item);
            if (index < days.length - 1) {
                body.add(Box.createVerticalStrut(8));
            }
        }
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTopMenuPanel() {
        RoundedGradientPanel card = dashboardCard("Top 3 Menu Terlaris", 205);
        topMenuBody.setLayout(new BoxLayout(topMenuBody, BoxLayout.Y_AXIS));
        card.add(topMenuBody, BorderLayout.CENTER);
        updateTopMenus(List.of());
        return card;
    }

    private JPanel createPaymentMethodPanel() {
        RoundedGradientPanel card = dashboardCard("Metode Pembayaran", 350);
        JPanel body = transparentPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        for (String label : PAYMENT_LABELS) {
            JLabel value = new JLabel("0", SwingConstants.RIGHT);
            paymentValues.put(label, value);
            addSummaryRow(body, c, label, value, false, false);
            c.gridy++;
        }
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private RoundedGradientPanel dashboardCard(String title, int height) {
        RoundedGradientPanel card = new RoundedGradientPanel(
                new BorderLayout(0, 13), CARD_START, CARD_END, 12);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(0, height));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 21));
        card.add(titleLabel, BorderLayout.NORTH);
        return card;
    }

    private void addSummaryRow(
            JPanel parent,
            GridBagConstraints base,
            String labelText,
            String valueText,
            boolean header,
            boolean emphasized) {
        JLabel value = new JLabel(valueText, SwingConstants.RIGHT);
        addSummaryRow(parent, base, labelText, value, header, emphasized);
    }

    private void addSummaryRow(
            JPanel parent,
            GridBagConstraints base,
            String labelText,
            JLabel value,
            boolean header,
            boolean emphasized) {
        JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setOpaque(true);
        row.setBackground(header ? ROW : new Color(0, 0, 0, 0));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER),
                BorderFactory.createEmptyBorder(0, 14, 0, 14)));

        JLabel label = new JLabel(labelText);
        int style = header || emphasized ? Font.BOLD : Font.PLAIN;
        int size = header ? 11 : 14;
        label.setForeground(header ? MUTED : WHITE);
        label.setFont(new Font("Segoe UI", style, size));

        value.setForeground(header ? MUTED : WHITE);
        value.setFont(new Font("Segoe UI", Font.BOLD, size));
        value.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(label);
        row.add(value);

        GridBagConstraints c = (GridBagConstraints) base.clone();
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        parent.add(row, c);
    }

    public void refreshDashboard() {
        if (currentWorker != null && !currentWorker.isDone()) {
            return;
        }
        setLoadingState();
        currentWorker = new SwingWorker<>() {
            @Override
            protected OwnerDashboardData doInBackground() throws Exception {
                return dashboardDAO.loadDashboardData();
            }

            @Override
            protected void done() {
                try {
                    updateDashboardUI(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    updateDashboardUI(OwnerDashboardData.empty());
                } catch (ExecutionException exception) {
                    updateDashboardUI(OwnerDashboardData.empty());
                    Throwable cause = exception.getCause() == null
                            ? exception : exception.getCause();
                    cause.printStackTrace();
                    if (isShowing()) {
                        JOptionPane.showMessageDialog(
                                OwnerDashboardPanel.this,
                                "Dashboard tidak dapat dimuat.\n" + safeMessage(cause),
                                "Koneksi Database",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        currentWorker.execute();
    }

    private void setLoadingState() {
        revenueCard.setValue("Memuat...");
        orderCountCard.setValue("...");
        averageCard.setValue("Memuat...");
        bestMenuCard.setValue("...");
        updateOrderStatus(Map.of(), 0);
        updateWeeklyRevenue(Map.of());
        updateTopMenus(List.of());
        updatePaymentMethods(Map.of());
    }

    private void updateDashboardUI(OwnerDashboardData data) {
        revenueCard.setValue(formatRupiah(data.todayRevenue()));
        orderCountCard.setValue(String.valueOf(data.todayOrderCount()));
        averageCard.setValue(formatRupiah(data.todayAverageTransaction()));
        bestMenuCard.setValue(
                data.todayBestSellingMenu() == null
                        || data.todayBestSellingMenu().isBlank()
                        ? "-" : data.todayBestSellingMenu());
        updateOrderStatus(data.orderStatusSummary(), data.todayOrderCount());
        updateWeeklyRevenue(data.currentWeekRevenue());
        updateTopMenus(data.topMenus());
        updatePaymentMethods(data.paymentMethodSummary());
        revalidate();
        repaint();
    }

    private void updateOrderStatus(Map<String, Integer> data, int total) {
        for (String key : STATUS_KEYS) {
            statusValues.get(key).setText(
                    String.valueOf(data.getOrDefault(key, 0)));
        }
        statusValues.get("total").setText(String.valueOf(total));
    }

    private void updateWeeklyRevenue(Map<DayOfWeek, BigDecimal> data) {
        BigDecimal highest = BigDecimal.ZERO;
        for (DayOfWeek day : DayOfWeek.values()) {
            BigDecimal amount = safeAmount(data.get(day));
            if (amount.compareTo(highest) > 0) {
                highest = amount;
            }
        }

        for (DayOfWeek day : DayOfWeek.values()) {
            BigDecimal amount = safeAmount(data.get(day));
            int percentage = highest.signum() == 0
                    ? 0
                    : amount.multiply(BigDecimal.valueOf(100))
                            .divide(highest, 0, java.math.RoundingMode.HALF_UP)
                            .intValue();
            weeklyItems.get(day).setRevenue(
                    formatRupiah(amount), percentage);
        }
    }

    private void updateTopMenus(List<TopMenuItem> data) {
        topMenuBody.removeAll();
        if (data == null || data.isEmpty()) {
            JLabel empty = new JLabel("Belum ada menu terjual hari ini.");
            empty.setForeground(MUTED);
            empty.setFont(new Font("Segoe UI", Font.BOLD, 14));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            topMenuBody.add(empty);
        } else {
            for (int index = 0; index < data.size(); index++) {
                TopMenuItem item = data.get(index);
                JLabel name = new JLabel(
                        (index + 1) + ". " + item.menuName());
                name.setForeground(WHITE);
                name.setFont(new Font("Segoe UI", Font.BOLD, 14));
                name.setAlignmentX(Component.LEFT_ALIGNMENT);
                JLabel quantity = new JLabel(item.quantity() + " terjual");
                quantity.setForeground(MUTED);
                quantity.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                quantity.setAlignmentX(Component.LEFT_ALIGNMENT);
                topMenuBody.add(name);
                topMenuBody.add(Box.createVerticalStrut(2));
                topMenuBody.add(quantity);
                if (index < data.size() - 1) {
                    topMenuBody.add(Box.createVerticalStrut(9));
                }
            }
        }
        topMenuBody.revalidate();
        topMenuBody.repaint();
    }

    private void updatePaymentMethods(Map<String, Integer> data) {
        for (String label : PAYMENT_LABELS) {
            paymentValues.get(label).setText(
                    String.valueOf(data.getOrDefault(label, 0)));
        }
    }

    private String formatRupiah(BigDecimal value) {
        return "Rp" + rupiahFormat.format(safeAmount(value).longValue());
    }

    private static BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static String safeMessage(Throwable throwable) {
        return throwable.getMessage() == null
                ? "Periksa koneksi MySQL dan database swiftbite."
                : throwable.getMessage();
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

    private static final class DashboardContentPanel
            extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(
                Rectangle visibleRect, int orientation, int direction) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(
                Rectangle visibleRect, int orientation, int direction) {
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
}
