package morning_bakery.sales;

import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import morning_bakery.owner.RoundedGradientPanel;
import morning_bakery.owner.StatCard;

/** Halaman Laporan Penjualan Owner dengan filter, export, dan print. */
public final class LaporanPenjualanPanel extends JPanel {

    private static final Color PAGE = new Color(249, 247, 244);
    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color CREAM = new Color(255, 247, 235);
    private static final Color MUTED = new Color(242, 211, 189);
    private static final Color BROWN_DARK = new Color(58, 28, 19);
    private static final Color BROWN = new Color(91, 48, 29);
    private static final Color CARD_START = new Color(145, 87, 51);
    private static final Color CARD_END = new Color(65, 31, 22);
    private static final Color DIVIDER = new Color(255, 255, 255, 45);

    private static final String[] PERIODS = {
        "Harian", "Mingguan", "Bulanan", "Tahunan", "Rentang Tanggal"
    };
    private static final String[] STATUS_OPTIONS = {
        "Semua Status", "Menunggu", "Diproses Baker", "Siap Diantar",
        "Menunggu Pembayaran", "Selesai", "Dibatalkan"
    };
    private static final String[] PAYMENT_OPTIONS = {
        "Semua Metode", "Tunai", "QRIS", "GoPay", "DANA", "OVO",
        "ShopeePay", "E-Wallet Lain"
    };
    private static final List<String> STATUS_KEYS = List.of(
            "menunggu", "diproses", "siap_diantar",
            "menunggu_pembayaran", "selesai", "dibatalkan");
    private static final List<String> STATUS_LABELS = List.of(
            "Menunggu", "Diproses Baker", "Siap Diantar",
            "Menunggu Pembayaran", "Selesai", "Dibatalkan");
    private static final List<String> PAYMENT_LABELS = List.of(
            "Tunai", "QRIS", "GoPay", "DANA", "OVO",
            "ShopeePay", "E-Wallet Lain");

    private final LaporanPenjualanDAO reportDAO = new LaporanPenjualanDAO();
    private final SalesReportExportService exportService
            = new SalesReportExportService();
    private final JComboBox<String> periodCombo
            = new JComboBox<>(PERIODS);
    private final JComboBox<String> chartPeriodCombo
            = new JComboBox<>(PERIODS);
    private final JDateChooser startDateChooser = new JDateChooser();
    private final JDateChooser endDateChooser = new JDateChooser();
    private final JComboBox<String> statusCombo
            = new JComboBox<>(STATUS_OPTIONS);
    private final JComboBox<String> paymentCombo
            = new JComboBox<>(PAYMENT_OPTIONS);
    private final JButton applyButton = actionButton(
            "Terapkan Filter", CREAM, BROWN_DARK);
    private final JButton resetButton = actionButton(
            "Reset", BROWN, WHITE);
    private final JButton excelButton = outlineButton("Export Excel");
    private final JButton wordButton = outlineButton("Export Word");
    private final JButton pdfButton = outlineButton("Export PDF");
    private final JButton printButton = outlineButton("Print");
    private final JLabel loadingLabel = new JLabel(" ");
    private final JLabel periodLabel = new JLabel("-");
    private final JLabel chartSubtitle = new JLabel("Tren penjualan.");
    private final StatCard totalOrdersCard
            = new StatCard("Total Pesanan", "0");
    private final StatCard productsCard
            = new StatCard("Produk Terjual", "0");
    private final StatCard revenueCard
            = new StatCard("Pendapatan", "Rp0");
    private final StatCard averageCard
            = new StatCard("Rata-rata Transaksi", "Rp0");
    private final SalesChartPanel chartPanel = new SalesChartPanel();
    private final Map<String, JLabel> statusValues = new LinkedHashMap<>();
    private final Map<String, JLabel> paymentValues = new LinkedHashMap<>();
    private final JPanel topProductsBody = transparent();
    private final TransactionTableModel transactionModel
            = new TransactionTableModel();
    private final JTable transactionTable = new JTable(transactionModel);
    private final CardLayout transactionLayout = new CardLayout();
    private final JPanel transactionBody = new JPanel(transactionLayout);
    private RoundedGradientPanel topProductsCard;
    private RoundedGradientPanel transactionCard;
    private final JScrollPane pageScroll;
    private SalesFilter activeFilter;
    private SalesReportData currentReportData;
    private SwingWorker<SalesReportData, Void> reportWorker;
    private boolean adjustingPeriod;

    public LaporanPenjualanPanel() {
        setLayout(new BorderLayout());
        setBackground(PAGE);

        ScrollableContentPanel content = new ScrollableContentPanel();
        content.setBackground(PAGE);
        content.setBorder(new EmptyBorder(32, 30, 34, 30));
        content.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;

        c.gridy = 0;
        content.add(createHeaderPanel(), c);
        c.gridy++;
        c.insets = new Insets(14, 0, 0, 0);
        content.add(createSummaryPanel(), c);
        c.gridy++;
        content.add(createFilterPanel(), c);
        c.gridy++;
        content.add(createChartPanel(), c);
        c.gridy++;
        content.add(createSummaryGrid(), c);
        c.gridy++;
        content.add(createTopProductsPanel(), c);
        c.gridy++;
        content.add(createTransactionPanel(), c);
        c.gridy++;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        content.add(Box.createGlue(), c);

        pageScroll = new JScrollPane(content);
        pageScroll.setBorder(null);
        pageScroll.setViewportBorder(null);
        pageScroll.getViewport().setBackground(PAGE);
        pageScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pageScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pageScroll.getVerticalScrollBar().setUnitIncrement(18);
        add(pageScroll, BorderLayout.CENTER);

        initializeFilterOptions();
        installActions();
        resetFilter();
    }

    private JPanel createHeaderPanel() {
        RoundedGradientPanel header = new RoundedGradientPanel(
                new BorderLayout(),
                new Color(154, 94, 54),
                new Color(53, 25, 18),
                14);
        header.setBorder(new EmptyBorder(20, 22, 20, 22));
        header.setPreferredSize(new Dimension(0, 155));
        JPanel text = transparent();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("OWNER REPORT");
        eyebrow.setForeground(MUTED);
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel title = new JLabel("Laporan Penjualan");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 38));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextArea description = new JTextArea(
                "Pantau total pesanan, produk terjual, pendapatan, status pesanan, "
                + "menu terlaris, pembayaran, dan detail transaksi.");
        description.setOpaque(false);
        description.setEditable(false);
        description.setFocusable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setForeground(new Color(255, 235, 218));
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        text.add(eyebrow);
        text.add(Box.createVerticalStrut(6));
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(description);
        header.add(text, BorderLayout.CENTER);
        return header;
    }

    private JPanel createFilterPanel() {
        RoundedGradientPanel filter = new RoundedGradientPanel(
                new GridBagLayout(), CARD_START, CARD_END, 12);
        filter.setBorder(new EmptyBorder(14, 16, 14, 16));
        filter.setPreferredSize(new Dimension(0, 92));
        configureCombo(periodCombo);
        configureCombo(statusCombo);
        configureCombo(paymentCombo);
        configureDateChooser(startDateChooser);
        configureDateChooser(endDateChooser);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 5, 9);
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = .15;
        filter.add(filterLabel("JENIS PERIODE"), c);
        c.gridx = 1;
        c.weightx = .14;
        filter.add(filterLabel("TANGGAL AWAL"), c);
        c.gridx = 2;
        filter.add(filterLabel("TANGGAL AKHIR"), c);
        c.gridx = 3;
        c.weightx = .18;
        filter.add(filterLabel("STATUS PESANAN"), c);
        c.gridx = 4;
        c.weightx = .17;
        filter.add(filterLabel("METODE PEMBAYARAN"), c);

        c.gridy = 1;
        c.gridx = 0;
        c.weightx = .15;
        filter.add(periodCombo, c);
        c.gridx = 1;
        c.weightx = .14;
        filter.add(startDateChooser, c);
        c.gridx = 2;
        filter.add(endDateChooser, c);
        c.gridx = 3;
        c.weightx = .18;
        filter.add(statusCombo, c);
        c.gridx = 4;
        c.weightx = .17;
        filter.add(paymentCombo, c);

        JPanel actions = transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        loadingLabel.setForeground(MUTED);
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        actions.add(loadingLabel);
        actions.add(applyButton);
        actions.add(resetButton);
        c.gridx = 5;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 0, 0);
        c.anchor = GridBagConstraints.EAST;
        filter.add(actions, c);
        return filter;
    }

    private JPanel createSummaryPanel() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(0, 92));
        cards.add(totalOrdersCard);
        cards.add(productsCard);
        cards.add(revenueCard);
        cards.add(averageCard);
        return cards;
    }

    private JPanel createChartPanel() {
        RoundedGradientPanel card = dashboardCard("Grafik Penjualan", 382);
        JPanel header = transparent(new BorderLayout(10, 0));
        JPanel text = transparent();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Grafik Penjualan");
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        chartSubtitle.setForeground(MUTED);
        chartSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        text.add(title);
        text.add(Box.createVerticalStrut(3));
        text.add(chartSubtitle);

        configureCombo(chartPeriodCombo);
        chartPeriodCombo.setPreferredSize(new Dimension(138, 38));
        JPanel buttons = transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(chartPeriodCombo);
        buttons.add(excelButton);
        buttons.add(wordButton);
        buttons.add(pdfButton);
        buttons.add(printButton);
        header.add(text, BorderLayout.CENTER);
        header.add(buttons, BorderLayout.EAST);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(255, 255, 255, 55)),
                new EmptyBorder(8, 8, 8, 8)));
        card.add(header, BorderLayout.NORTH);
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createSummaryGrid() {
        JPanel grid = transparent(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.weightx = .5;
        c.insets = new Insets(0, 0, 0, 7);
        grid.add(createStatusSummaryPanel(), c);
        c.gridx = 1;
        c.insets = new Insets(0, 7, 0, 0);
        grid.add(createPaymentSummaryPanel(), c);
        return grid;
    }

    private JPanel createStatusSummaryPanel() {
        RoundedGradientPanel card = dashboardCard(
                "Ringkasan Status Pesanan", 320);
        JPanel heading = transparent(new BorderLayout());
        JLabel title = sectionTitle("Ringkasan Status Pesanan");
        periodLabel.setForeground(MUTED);
        periodLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        heading.add(title, BorderLayout.WEST);
        heading.add(periodLabel, BorderLayout.EAST);
        JPanel body = transparent();
        body.setLayout(new GridLayout(STATUS_KEYS.size(), 1));
        for (int index = 0; index < STATUS_KEYS.size(); index++) {
            JLabel value = badge("0");
            statusValues.put(STATUS_KEYS.get(index), value);
            body.add(summaryRow(STATUS_LABELS.get(index), value));
        }
        card.add(heading, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPaymentSummaryPanel() {
        RoundedGradientPanel card = dashboardCard("Metode Pembayaran", 320);
        JPanel body = transparent();
        body.setLayout(new GridLayout(PAYMENT_LABELS.size(), 1));
        for (String label : PAYMENT_LABELS) {
            JLabel value = badge("0");
            paymentValues.put(label, value);
            body.add(summaryRow(label, value));
        }
        card.add(sectionTitle("Metode Pembayaran"), BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTopProductsPanel() {
        topProductsCard = dashboardCard(
                "Top 5 Menu Terlaris", 245);
        topProductsBody.setLayout(
                new BoxLayout(topProductsBody, BoxLayout.Y_AXIS));
        topProductsCard.add(
                sectionTitle("Top 5 Menu Terlaris"), BorderLayout.NORTH);
        topProductsCard.add(topProductsBody, BorderLayout.CENTER);
        return topProductsCard;
    }

    private JPanel createTransactionPanel() {
        transactionCard = dashboardCard("Detail Transaksi", 400);
        transactionTable.setRowHeight(48);
        transactionTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        transactionTable.setFillsViewportHeight(true);
        transactionTable.setBackground(BROWN);
        transactionTable.setForeground(WHITE);
        transactionTable.setSelectionBackground(
                new Color(153, 93, 57));
        transactionTable.setSelectionForeground(WHITE);
        transactionTable.setGridColor(DIVIDER);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        transactionTable.getTableHeader().setPreferredSize(
                new Dimension(0, 44));
        transactionTable.getTableHeader().setBackground(
                new Color(161, 105, 72));
        transactionTable.getTableHeader().setForeground(WHITE);
        transactionTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 10));
        transactionTable.getTableHeader().setReorderingAllowed(false);
        transactionTable.setRowSorter(
                new javax.swing.table.TableRowSorter<>(transactionModel));

        transactionTable.getColumnModel().getColumn(0)
                .setPreferredWidth(130);
        transactionTable.getColumnModel().getColumn(1)
                .setPreferredWidth(125);
        transactionTable.getColumnModel().getColumn(2)
                .setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(3)
                .setPreferredWidth(70);
        transactionTable.getColumnModel().getColumn(4)
                .setPreferredWidth(105);
        transactionTable.getColumnModel().getColumn(5)
                .setPreferredWidth(95);
        transactionTable.getColumnModel().getColumn(6)
                .setPreferredWidth(115);
        transactionTable.getColumnModel().getColumn(7)
                .setPreferredWidth(75);
        transactionTable.getColumnModel().getColumn(6)
                .setCellRenderer(new StatusBadgeRenderer());
        transactionTable.getColumnModel().getColumn(7)
                .setCellRenderer(new DetailButtonRenderer());
        transactionTable.getColumnModel().getColumn(7)
                .setCellEditor(new DetailButtonEditor());

        JScrollPane tableScroll = new JScrollPane(transactionTable);
        tableScroll.setBorder(null);
        tableScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroll.getViewport().setBackground(BROWN);
        transactionBody.setOpaque(false);
        JPanel emptyPanel = transparent(new GridBagLayout());
        JLabel emptyLabel = new JLabel(
                "Belum ada transaksi pada periode ini.");
        emptyLabel.setForeground(MUTED);
        emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emptyPanel.add(emptyLabel);
        transactionBody.add(emptyPanel, "EMPTY");
        transactionBody.add(tableScroll, "TABLE");
        transactionCard.add(
                sectionTitle("Detail Transaksi"), BorderLayout.NORTH);
        transactionCard.add(transactionBody, BorderLayout.CENTER);
        return transactionCard;
    }

    private RoundedGradientPanel dashboardCard(String ignoredTitle, int height) {
        RoundedGradientPanel card = new RoundedGradientPanel(
                new BorderLayout(0, 12), CARD_START, CARD_END, 12);
        card.setBorder(new EmptyBorder(17, 18, 17, 18));
        card.setPreferredSize(new Dimension(0, height));
        return card;
    }

    private void initializeFilterOptions() {
        adjustingPeriod = true;
        periodCombo.setSelectedItem("Mingguan");
        chartPeriodCombo.setSelectedItem("Mingguan");
        adjustingPeriod = false;
    }

    private void installActions() {
        periodCombo.addActionListener(event -> {
            if (!adjustingPeriod) {
                adjustingPeriod = true;
                chartPeriodCombo.setSelectedItem(
                        periodCombo.getSelectedItem());
                adjustDatesForPeriod();
                adjustingPeriod = false;
            }
        });
        chartPeriodCombo.addActionListener(event -> {
            if (!adjustingPeriod) {
                adjustingPeriod = true;
                periodCombo.setSelectedItem(
                        chartPeriodCombo.getSelectedItem());
                adjustDatesForPeriod();
                adjustingPeriod = false;
                applyFilter();
            }
        });
        applyButton.addActionListener(event -> applyFilter());
        resetButton.addActionListener(event -> resetFilter());
        excelButton.addActionListener(event -> exportExcel());
        wordButton.addActionListener(event -> exportWord());
        pdfButton.addActionListener(event -> exportPdf());
        printButton.addActionListener(event -> printReport());
    }

    private void adjustDatesForPeriod() {
        String period = String.valueOf(periodCombo.getSelectedItem());
        LocalDate base = dateValue(startDateChooser);
        if (base == null) {
            base = LocalDate.now();
        }
        LocalDate start;
        LocalDate end;
        switch (period) {
            case "Harian" -> {
                start = base;
                end = base;
            }
            case "Bulanan" -> {
                YearMonth month = YearMonth.from(base);
                start = month.atDay(1);
                end = month.atEndOfMonth();
            }
            case "Tahunan" -> {
                start = LocalDate.of(base.getYear(), 1, 1);
                end = LocalDate.of(base.getYear(), 12, 31);
            }
            case "Rentang Tanggal" -> {
                startDateChooser.setEnabled(true);
                endDateChooser.setEnabled(true);
                return;
            }
            default -> {
                start = base.with(TemporalAdjusters.previousOrSame(
                        DayOfWeek.MONDAY));
                end = start.plusDays(6);
            }
        }
        setChooserDate(startDateChooser, start);
        setChooserDate(endDateChooser, end);
        startDateChooser.setEnabled(true);
        endDateChooser.setEnabled(true);
    }

    private SalesFilter readFilter() {
        LocalDate start = dateValue(startDateChooser);
        LocalDate end = dateValue(endDateChooser);
        return new SalesFilter(
                start,
                end,
                String.valueOf(periodCombo.getSelectedItem()),
                statusValue(String.valueOf(statusCombo.getSelectedItem())),
                paymentValue(String.valueOf(paymentCombo.getSelectedItem())),
                "");
    }

    private boolean validateFilter(SalesFilter filter) {
        if (filter.startDate() == null || filter.endDate() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tanggal awal dan tanggal akhir wajib dipilih.",
                    "Filter Tidak Valid",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (filter.startDate().isAfter(filter.endDate())) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tanggal awal tidak boleh melebihi tanggal akhir.",
                    "Filter Tidak Valid",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void applyFilter() {
        SalesFilter filter = readFilter();
        if (validateFilter(filter)) {
            loadReport(filter);
        }
    }

    private void resetFilter() {
        adjustingPeriod = true;
        periodCombo.setSelectedItem("Mingguan");
        chartPeriodCombo.setSelectedItem("Mingguan");
        adjustingPeriod = false;
        LocalDate monday = LocalDate.now().with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        setChooserDate(startDateChooser, monday);
        setChooserDate(endDateChooser, monday.plusDays(6));
        statusCombo.setSelectedIndex(0);
        paymentCombo.setSelectedIndex(0);
        pageScroll.getVerticalScrollBar().setValue(0);
        applyFilter();
    }

    public void refreshReport() {
        if (activeFilter == null) {
            resetFilter();
        } else {
            loadReport(activeFilter);
        }
    }

    private void loadReport(SalesFilter filter) {
        if (reportWorker != null && !reportWorker.isDone()) {
            return;
        }
        currentReportData = null;
        setLoadingState(true);
        clearVisibleData();
        reportWorker = new SwingWorker<>() {
            @Override
            protected SalesReportData doInBackground() throws Exception {
                return reportDAO.getSalesReport(filter);
            }

            @Override
            protected void done() {
                try {
                    currentReportData = get();
                    activeFilter = filter;
                    updateReportUI(currentReportData);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause() == null
                            ? exception : exception.getCause();
                    cause.printStackTrace();
                    if (isShowing()) {
                        JOptionPane.showMessageDialog(
                                LaporanPenjualanPanel.this,
                                "Gagal memuat laporan penjualan.\n"
                                + safeMessage(cause),
                                "Koneksi Database",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } finally {
                    setLoadingState(false);
                }
            }
        };
        reportWorker.execute();
    }

    private void updateReportUI(SalesReportData data) {
        updateSummaryCards(data.summary());
        updateChart(data.chartItems());
        updateOrderStatus(data.orderStatus());
        updatePaymentMethods(data.paymentMethods());
        updateTopProducts(data.topProducts());
        updateTransactions(data.transactions());
        periodLabel.setText(
                SalesReportExportService.periodText(activeFilter));
        chartSubtitle.setText(chartSubtitle(activeFilter));
        revalidate();
        repaint();
    }

    private void updateSummaryCards(SalesSummary summary) {
        totalOrdersCard.setValue(String.valueOf(summary.totalOrders()));
        productsCard.setValue(String.valueOf(summary.totalProducts()));
        revenueCard.setValue(
                SalesReportExportService.formatRupiah(
                        summary.totalRevenue()));
        averageCard.setValue(
                SalesReportExportService.formatRupiah(
                        summary.averageTransaction()));
    }

    private void updateChart(List<SalesChartItem> data) {
        chartPanel.setItems(data);
    }

    private void updateOrderStatus(Map<String, Integer> data) {
        for (String key : STATUS_KEYS) {
            statusValues.get(key).setText(
                    String.valueOf(data.getOrDefault(key, 0)));
        }
    }

    private void updatePaymentMethods(Map<String, Integer> data) {
        for (String label : PAYMENT_LABELS) {
            paymentValues.get(label).setText(
                    String.valueOf(data.getOrDefault(label, 0)));
        }
    }

    private void updateTopProducts(List<TopSellingProduct> data) {
        topProductsBody.removeAll();
        if (data == null || data.isEmpty()) {
            JLabel empty = new JLabel("Belum ada data penjualan.");
            empty.setForeground(MUTED);
            empty.setFont(new Font("Segoe UI", Font.BOLD, 13));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            topProductsBody.add(empty);
        } else {
            for (int index = 0; index < data.size(); index++) {
                TopSellingProduct product = data.get(index);
                JPanel row = transparent(new BorderLayout());
                row.setBorder(BorderFactory.createMatteBorder(
                        0, 0, 1, 0, DIVIDER));
                JLabel name = new JLabel(
                        (index + 1) + ". " + product.productName());
                name.setForeground(WHITE);
                name.setFont(new Font("Segoe UI", Font.BOLD, 13));
                JLabel detail = new JLabel(
                        product.quantitySold() + " produk terjual  |  "
                        + SalesReportExportService.formatRupiah(
                                product.revenue()),
                        SwingConstants.RIGHT);
                detail.setForeground(MUTED);
                detail.setFont(new Font("Segoe UI", Font.BOLD, 12));
                row.add(name, BorderLayout.WEST);
                row.add(detail, BorderLayout.EAST);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                topProductsBody.add(row);
            }
        }
        topProductsCard.setPreferredSize(new Dimension(
                0, data == null || data.isEmpty() ? 125 : 245));
        topProductsBody.revalidate();
        topProductsBody.repaint();
    }

    private void updateTransactions(List<TransactionReportItem> data) {
        transactionModel.setTransactions(data);
        transactionLayout.show(
                transactionBody,
                data == null || data.isEmpty() ? "EMPTY" : "TABLE");
        transactionCard.setPreferredSize(new Dimension(
                0, data == null || data.isEmpty() ? 150 : 400));
    }

    private void setLoadingState(boolean loading) {
        applyButton.setEnabled(!loading);
        resetButton.setEnabled(!loading);
        excelButton.setEnabled(!loading && currentReportData != null);
        wordButton.setEnabled(!loading && currentReportData != null);
        pdfButton.setEnabled(!loading && currentReportData != null);
        printButton.setEnabled(!loading && currentReportData != null);
        loadingLabel.setText(loading ? "Memuat laporan..." : " ");
    }

    private void clearVisibleData() {
        totalOrdersCard.setValue("...");
        productsCard.setValue("...");
        revenueCard.setValue("Memuat...");
        averageCard.setValue("Memuat...");
        updateOrderStatus(Map.of());
        updatePaymentMethods(Map.of());
        updateTopProducts(List.of());
        updateTransactions(List.of());
        chartPanel.setItems(List.of());
    }

    private void exportExcel() {
        exportFile(
                "xlsx",
                path -> exportService.exportExcel(
                        path, activeFilter, currentReportData),
                "Laporan Excel berhasil disimpan.");
    }

    private void exportWord() {
        exportFile(
                "docx",
                path -> exportService.exportWord(
                        path, activeFilter, currentReportData),
                "Laporan Word berhasil disimpan.");
    }

    private void exportPdf() {
        exportFile(
                "pdf",
                path -> exportService.exportPdf(
                        path, activeFilter, currentReportData),
                "Laporan PDF berhasil disimpan.");
    }

    private void exportFile(
            String extension,
            ExportTask task,
            String successMessage) {
        if (currentReportData == null || activeFilter == null) {
            return;
        }
        Path target = chooseExportFile(extension);
        if (target == null) {
            return;
        }
        setLoadingState(true);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                task.run(target);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(
                            LaporanPenjualanPanel.this,
                            successMessage);
                } catch (Exception exception) {
                    Throwable cause = exception.getCause() == null
                            ? exception : exception.getCause();
                    cause.printStackTrace();
                    JOptionPane.showMessageDialog(
                            LaporanPenjualanPanel.this,
                            "File tidak dapat disimpan. Pastikan file tidak "
                            + "sedang dibuka dan folder dapat ditulis.",
                            "Export Gagal",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    setLoadingState(false);
                }
            }
        }.execute();
    }

    private Path chooseExportFile(String extension) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan Laporan Penjualan");
        chooser.setFileFilter(new FileNameExtensionFilter(
                extension.toUpperCase() + " (*." + extension + ")",
                extension));
        String period = activeFilter.startDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                + "_sampai_"
                + activeFilter.endDate()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        chooser.setSelectedFile(new java.io.File(
                "Laporan_Penjualan_" + period + "." + extension));
        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        Path target = chooser.getSelectedFile().toPath();
        if (!target.getFileName().toString()
                .toLowerCase(java.util.Locale.ROOT)
                .endsWith("." + extension)) {
            target = Path.of(target.toString() + "." + extension);
        }
        if (Files.exists(target)) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "File sudah ada. Timpa file tersebut?",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return null;
            }
        }
        return target;
    }

    private void printReport() {
        if (currentReportData == null || activeFilter == null) {
            return;
        }
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat format = job.defaultPage();
        format.setOrientation(PageFormat.LANDSCAPE);
        job.setPrintable(
                new SalesReportPrintable(
                        activeFilter, currentReportData),
                format);
        if (!job.printDialog()) {
            return;
        }
        setLoadingState(true);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                job.print();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(
                            LaporanPenjualanPanel.this,
                            "Gagal mencetak laporan.",
                            "Print Gagal",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    setLoadingState(false);
                }
            }
        }.execute();
    }

    private void openTransactionDetail(long transactionId) {
        TransactionReportItem transaction
                = transactionModel.findById(transactionId);
        if (transaction == null) {
            return;
        }
        setLoadingState(true);
        new SwingWorker<List<TransactionProductItem>, Void>() {
            @Override
            protected List<TransactionProductItem> doInBackground()
                    throws Exception {
                return reportDAO.getTransactionProducts(transactionId);
            }

            @Override
            protected void done() {
                setLoadingState(false);
                try {
                    Window owner = SwingUtilities.getWindowAncestor(
                            LaporanPenjualanPanel.this);
                    TransactionDetailDialog.show(
                            owner, transaction, get());
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(
                            LaporanPenjualanPanel.this,
                            "Detail transaksi tidak dapat dimuat.",
                            "Koneksi Database",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private String chartSubtitle(SalesFilter filter) {
        return switch (filter.periodType()) {
            case "Harian" -> "Tren penjualan hari ini.";
            case "Bulanan" -> "Tren penjualan per tanggal pada bulan terpilih.";
            case "Tahunan" -> "Tren penjualan per bulan pada tahun terpilih.";
            case "Rentang Tanggal" -> "Tren penjualan pada rentang terpilih.";
            default -> "Tren jumlah pesanan per hari minggu ini.";
        };
    }

    private String statusValue(String label) {
        return switch (label) {
            case "Menunggu" -> "menunggu";
            case "Diproses Baker" -> "diproses";
            case "Siap Diantar" -> "siap_diantar";
            case "Menunggu Pembayaran" -> "menunggu_pembayaran";
            case "Selesai" -> "selesai";
            case "Dibatalkan" -> "dibatalkan";
            default -> null;
        };
    }

    private String paymentValue(String label) {
        return switch (label) {
            case "Tunai" -> "cash";
            case "QRIS" -> "qris";
            case "GoPay", "DANA", "OVO", "ShopeePay", "E-Wallet Lain"
                    -> "ewallet";
            default -> null;
        };
    }

    private LocalDate dateValue(JDateChooser chooser) {
        Calendar selected = chooser.getCalendar();
        if (selected == null) {
            return null;
        }
        return LocalDate.of(
                selected.get(Calendar.YEAR),
                selected.get(Calendar.MONTH) + 1,
                selected.get(Calendar.DAY_OF_MONTH));
    }

    private void setChooserDate(
            JDateChooser chooser, LocalDate date) {
        chooser.setDate(java.sql.Date.valueOf(date));
    }

    private void configureDateChooser(JDateChooser chooser) {
        chooser.setDateFormatString("dd/MM/yyyy");
        chooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chooser.setBackground(WHITE);
        chooser.setPreferredSize(new Dimension(130, 38));
        chooser.getCalendarButton().setBackground(CREAM);
        chooser.getCalendarButton().setFocusPainted(false);
        Component editor = chooser.getDateEditor().getUiComponent();
        if (editor instanceof JComponent component) {
            component.setBorder(new EmptyBorder(7, 9, 7, 5));
            component.setBackground(WHITE);
            component.setForeground(BROWN_DARK);
        }
    }

    private void configureCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(WHITE);
        combo.setForeground(BROWN_DARK);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean selected,
                    boolean focused) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, focused);
                label.setBorder(new EmptyBorder(6, 8, 6, 8));
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

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        return label;
    }

    private JPanel summaryRow(String text, JLabel value) {
        JPanel row = transparent(new BorderLayout());
        row.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, DIVIDER));
        JLabel label = new JLabel(text);
        label.setForeground(WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JLabel badge(String value) {
        JLabel label = new JLabel(value, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(CREAM);
        label.setForeground(BROWN_DARK);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(new EmptyBorder(5, 11, 5, 11));
        return label;
    }

    private static JButton actionButton(
            String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(new EmptyBorder(9, 12, 9, 12));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static JButton outlineButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setBackground(new Color(84, 45, 31));
        button.setForeground(WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(255, 255, 255, 120)),
                new EmptyBorder(8, 10, 8, 10)));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static JPanel transparent() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    private static JPanel transparent(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private static String safeMessage(Throwable throwable) {
        return throwable.getMessage() == null
                ? "Periksa koneksi MySQL dan database swiftbite."
                : throwable.getMessage();
    }

    @FunctionalInterface
    private interface ExportTask {
        void run(Path path) throws Exception;
    }

    private static final class ScrollableContentPanel
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

    private static final class TransactionTableModel
            extends AbstractTableModel {

        private final String[] columns = {
            "ID TRANSAKSI", "TANGGAL", "PELANGGAN / MEJA", "PRODUK",
            "TOTAL BAYAR", "PEMBAYARAN", "STATUS", "AKSI"
        };
        private final List<TransactionReportItem> transactions
                = new ArrayList<>();

        void setTransactions(List<TransactionReportItem> values) {
            transactions.clear();
            if (values != null) {
                transactions.addAll(values);
            }
            fireTableDataChanged();
        }

        TransactionReportItem getTransaction(int row) {
            return transactions.get(row);
        }

        TransactionReportItem findById(long id) {
            return transactions.stream()
                    .filter(item -> item.transactionId() == id)
                    .findFirst().orElse(null);
        }

        @Override
        public int getRowCount() {
            return transactions.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 7;
        }

        @Override
        public Object getValueAt(int row, int column) {
            TransactionReportItem item = transactions.get(row);
            return switch (column) {
                case 0 -> item.transactionCode();
                case 1 -> item.transactionDate() == null
                        ? "-" : item.transactionDate().format(
                                DateTimeFormatter.ofPattern(
                                        "dd/MM/yyyy HH:mm"));
                case 2 -> item.customerName();
                case 3 -> item.totalProduct();
                case 4 -> SalesReportExportService.formatRupiah(
                        item.totalAmount());
                case 5 -> item.paymentMethod();
                case 6 -> SalesReportExportService.readableStatus(
                        item.status());
                default -> "Detail";
            };
        }
    }

    private static final class StatusBadgeRenderer
            implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {
            JLabel badge = new JLabel(
                    String.valueOf(value), SwingConstants.CENTER);
            badge.setOpaque(true);
            badge.setBackground(new Color(255, 241, 218));
            badge.setForeground(new Color(83, 43, 27));
            badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            badge.setBorder(new EmptyBorder(5, 7, 5, 7));
            JPanel holder = transparent(new GridBagLayout());
            holder.setOpaque(true);
            holder.setBackground(selected
                    ? new Color(153, 93, 57) : BROWN);
            holder.add(badge);
            return holder;
        }
    }

    private static final class DetailButtonRenderer
            extends JButton implements TableCellRenderer {

        DetailButtonRenderer() {
            setText("Detail");
            setUI(new BasicButtonUI());
            setBackground(CREAM);
            setForeground(BROWN_DARK);
            setFont(new Font("Segoe UI", Font.BOLD, 10));
            setBorder(new EmptyBorder(7, 10, 7, 10));
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {
            JPanel holder = transparent(new GridBagLayout());
            holder.setOpaque(true);
            holder.setBackground(selected
                    ? new Color(153, 93, 57) : BROWN);
            holder.add(this);
            return holder;
        }
    }

    private final class DetailButtonEditor
            extends AbstractCellEditor implements TableCellEditor {

        private final JButton button = actionButton(
                "Detail", CREAM, BROWN_DARK);
        private long transactionId;

        DetailButtonEditor() {
            button.addActionListener(event -> {
                stopCellEditing();
                openTransactionDetail(transactionId);
            });
        }

        @Override
        public Object getCellEditorValue() {
            return "Detail";
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean selected,
                int viewRow,
                int column) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            transactionId = transactionModel
                    .getTransaction(modelRow).transactionId();
            return button;
        }
    }
}
