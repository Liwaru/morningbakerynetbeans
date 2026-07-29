package morning_bakery.sales;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/** Export snapshot Laporan Penjualan ke XLSX, DOCX, dan PDF. */
public final class SalesReportExportService {

    private static final DateTimeFormatter DATE_TIME
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final List<String> STATUS_KEYS = List.of(
            "menunggu", "diproses", "siap_diantar",
            "menunggu_pembayaran", "selesai", "dibatalkan");
    private static final List<String> STATUS_LABELS = List.of(
            "Menunggu", "Diproses Baker", "Siap Diantar",
            "Menunggu Pembayaran", "Selesai", "Dibatalkan");
    private static final List<String> PAYMENT_LABELS = List.of(
            "Tunai", "QRIS", "GoPay", "DANA", "OVO",
            "ShopeePay", "E-Wallet Lain");

    public void exportExcel(
            Path target, SalesFilter filter, SalesReportData data)
            throws Exception {
        try (Workbook workbook = new XSSFWorkbook();
                OutputStream output = Files.newOutputStream(target)) {
            CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(
                    IndexedColors.BROWN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            DataFormat formats = workbook.createDataFormat();
            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(
                    formats.getFormat("\"Rp\"#,##0"));
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(
                    formats.getFormat("dd/mm/yyyy hh:mm"));

            createSummarySheet(
                    workbook, filter, data, titleStyle, headerStyle,
                    currencyStyle);
            createStatusSheet(workbook, data, headerStyle);
            createPaymentSheet(workbook, data, headerStyle);
            createTopProductSheet(
                    workbook, data, headerStyle, currencyStyle);
            createTransactionSheet(
                    workbook, data, headerStyle, currencyStyle, dateStyle);
            workbook.write(output);
        }
    }

    private void createSummarySheet(
            Workbook workbook,
            SalesFilter filter,
            SalesReportData data,
            CellStyle titleStyle,
            CellStyle headerStyle,
            CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Ringkasan");
        Row title = sheet.createRow(0);
        Cell titleCell = title.createCell(0);
        titleCell.setCellValue("Laporan Penjualan SwiftBite");
        titleCell.setCellStyle(titleStyle);

        Row period = sheet.createRow(2);
        period.createCell(0).setCellValue("Periode");
        period.getCell(0).setCellStyle(headerStyle);
        period.createCell(1).setCellValue(periodText(filter));

        String[] labels = {
            "Total Pesanan", "Produk Terjual",
            "Pendapatan", "Rata-rata Transaksi"
        };
        SalesSummary summary = data.summary();
        for (int index = 0; index < labels.length; index++) {
            Row row = sheet.createRow(index + 4);
            row.createCell(0).setCellValue(labels[index]);
            row.getCell(0).setCellStyle(headerStyle);
            Cell value = row.createCell(1);
            if (index == 0) {
                value.setCellValue(summary.totalOrders());
            } else if (index == 1) {
                value.setCellValue(summary.totalProducts());
            } else {
                BigDecimal amount = index == 2
                        ? summary.totalRevenue()
                        : summary.averageTransaction();
                value.setCellValue(amount.doubleValue());
                value.setCellStyle(currencyStyle);
            }
        }
        autoSize(sheet, 2);
    }

    private void createStatusSheet(
            Workbook workbook, SalesReportData data, CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("Status Pesanan");
        createHeader(sheet, headerStyle, "Status", "Jumlah");
        for (int index = 0; index < STATUS_KEYS.size(); index++) {
            Row row = sheet.createRow(index + 1);
            row.createCell(0).setCellValue(STATUS_LABELS.get(index));
            row.createCell(1).setCellValue(
                    data.orderStatus().getOrDefault(
                            STATUS_KEYS.get(index), 0));
        }
        sheet.createFreezePane(0, 1);
        autoSize(sheet, 2);
    }

    private void createPaymentSheet(
            Workbook workbook, SalesReportData data, CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("Metode Pembayaran");
        createHeader(sheet, headerStyle, "Metode", "Jumlah");
        for (int index = 0; index < PAYMENT_LABELS.size(); index++) {
            String label = PAYMENT_LABELS.get(index);
            Row row = sheet.createRow(index + 1);
            row.createCell(0).setCellValue(label);
            row.createCell(1).setCellValue(
                    data.paymentMethods().getOrDefault(label, 0));
        }
        sheet.createFreezePane(0, 1);
        autoSize(sheet, 2);
    }

    private void createTopProductSheet(
            Workbook workbook,
            SalesReportData data,
            CellStyle headerStyle,
            CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Top 5 Menu");
        createHeader(
                sheet, headerStyle,
                "Peringkat", "Nama Produk", "Jumlah Terjual", "Pendapatan");
        int rowIndex = 1;
        for (TopSellingProduct product : data.topProducts()) {
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(rowIndex++);
            row.createCell(1).setCellValue(product.productName());
            row.createCell(2).setCellValue(product.quantitySold());
            Cell revenue = row.createCell(3);
            revenue.setCellValue(product.revenue().doubleValue());
            revenue.setCellStyle(currencyStyle);
        }
        sheet.createFreezePane(0, 1);
        autoSize(sheet, 4);
    }

    private void createTransactionSheet(
            Workbook workbook,
            SalesReportData data,
            CellStyle headerStyle,
            CellStyle currencyStyle,
            CellStyle dateStyle) {
        Sheet sheet = workbook.createSheet("Detail Transaksi");
        createHeader(
                sheet, headerStyle,
                "ID Transaksi", "Tanggal", "Pelanggan / Meja",
                "Total Produk", "Total Bayar", "Metode Pembayaran", "Status");
        int rowIndex = 1;
        for (TransactionReportItem item : data.transactions()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(item.transactionCode());
            Cell date = row.createCell(1);
            if (item.transactionDate() != null) {
                date.setCellValue(item.transactionDate());
                date.setCellStyle(dateStyle);
            }
            row.createCell(2).setCellValue(item.customerName());
            row.createCell(3).setCellValue(item.totalProduct());
            Cell amount = row.createCell(4);
            amount.setCellValue(item.totalAmount().doubleValue());
            amount.setCellStyle(currencyStyle);
            row.createCell(5).setCellValue(item.paymentMethod());
            row.createCell(6).setCellValue(readableStatus(item.status()));
        }
        sheet.createFreezePane(0, 1);
        autoSize(sheet, 7);
    }

    public void exportWord(
            Path target, SalesFilter filter, SalesReportData data)
            throws Exception {
        try (XWPFDocument document = new XWPFDocument();
                OutputStream output = Files.newOutputStream(target)) {
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setText("SwiftBite Morning Bakery");
            XWPFRun reportRun = title.createRun();
            reportRun.addBreak();
            reportRun.setBold(true);
            reportRun.setFontSize(16);
            reportRun.setText("Laporan Penjualan");

            addWordParagraph(document, "Periode: " + periodText(filter), true);
            addWordParagraph(
                    document,
                    "Dicetak: " + LocalDateTime.now().format(DATE_TIME),
                    false);

            SalesSummary summary = data.summary();
            addWordTable(
                    document,
                    new String[]{"Ringkasan", "Nilai"},
                    List.of(
                            new String[]{"Total Pesanan",
                                String.valueOf(summary.totalOrders())},
                            new String[]{"Produk Terjual",
                                String.valueOf(summary.totalProducts())},
                            new String[]{"Pendapatan",
                                formatRupiah(summary.totalRevenue())},
                            new String[]{"Rata-rata Transaksi",
                                formatRupiah(summary.averageTransaction())}));

            addWordParagraph(document, "Status Pesanan", true);
            List<String[]> statusRows = new java.util.ArrayList<>();
            for (int index = 0; index < STATUS_KEYS.size(); index++) {
                statusRows.add(new String[]{
                    STATUS_LABELS.get(index),
                    String.valueOf(data.orderStatus().getOrDefault(
                            STATUS_KEYS.get(index), 0))
                });
            }
            addWordTable(
                    document,
                    new String[]{"Status", "Jumlah"},
                    statusRows);

            addWordParagraph(document, "Metode Pembayaran", true);
            List<String[]> paymentRows = new java.util.ArrayList<>();
            for (String label : PAYMENT_LABELS) {
                paymentRows.add(new String[]{
                    label,
                    String.valueOf(
                            data.paymentMethods().getOrDefault(label, 0))
                });
            }
            addWordTable(
                    document,
                    new String[]{"Metode", "Jumlah"},
                    paymentRows);

            addWordParagraph(document, "Top 5 Menu Terlaris", true);
            List<String[]> productRows = new java.util.ArrayList<>();
            int rank = 1;
            for (TopSellingProduct product : data.topProducts()) {
                productRows.add(new String[]{
                    String.valueOf(rank++),
                    product.productName(),
                    String.valueOf(product.quantitySold()),
                    formatRupiah(product.revenue())
                });
            }
            if (productRows.isEmpty()) {
                productRows.add(new String[]{"-", "Belum ada data", "0", "Rp0"});
            }
            addWordTable(
                    document,
                    new String[]{"No", "Menu", "Terjual", "Pendapatan"},
                    productRows);

            addWordParagraph(document, "Detail Transaksi", true);
            List<String[]> transactionRows = new java.util.ArrayList<>();
            for (TransactionReportItem item : data.transactions()) {
                transactionRows.add(new String[]{
                    item.transactionCode(),
                    item.transactionDate() == null
                            ? "-" : item.transactionDate().format(DATE_TIME),
                    item.customerName(),
                    String.valueOf(item.totalProduct()),
                    formatRupiah(item.totalAmount()),
                    item.paymentMethod(),
                    readableStatus(item.status())
                });
            }
            if (transactionRows.isEmpty()) {
                transactionRows.add(new String[]{
                    "-", "-", "Belum ada transaksi", "0", "Rp0", "-", "-"
                });
            }
            addWordTable(
                    document,
                    new String[]{"ID", "Tanggal", "Pelanggan / Meja",
                        "Produk", "Total", "Pembayaran", "Status"},
                    transactionRows);
            addWordParagraph(
                    document, "SwiftBite Morning Bakery", false);
            document.write(output);
        }
    }

    public void exportPdf(
            Path target, SalesFilter filter, SalesReportData data)
            throws Exception {
        Document document = new Document(PageSize.A4.rotate(), 28, 28, 34, 34);
        try (OutputStream output = Files.newOutputStream(target)) {
            try {
                PdfWriter writer = PdfWriter.getInstance(document, output);
                writer.setPageEvent(new PageFooter());
                document.open();
                com.lowagie.text.Font titleFont = FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD, 18);
                com.lowagie.text.Font sectionFont = FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD, 12);

                Paragraph title = new Paragraph(
                        "SwiftBite Morning Bakery\nLaporan Penjualan", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                Paragraph period = new Paragraph(
                        "Periode: " + periodText(filter)
                        + " | Dicetak: " + LocalDateTime.now().format(DATE_TIME));
                period.setAlignment(Element.ALIGN_CENTER);
                period.setSpacingAfter(12);
                document.add(period);

                SalesSummary summary = data.summary();
                document.add(pdfTable(
                        new String[]{"Total Pesanan", "Produk Terjual",
                            "Pendapatan", "Rata-rata"},
                        java.util.Collections.singletonList(new String[]{
                            String.valueOf(summary.totalOrders()),
                            String.valueOf(summary.totalProducts()),
                            formatRupiah(summary.totalRevenue()),
                            formatRupiah(summary.averageTransaction())
                        })));

                document.add(section("Status Pesanan", sectionFont));
                List<String[]> statusRows = new java.util.ArrayList<>();
                for (int index = 0; index < STATUS_KEYS.size(); index++) {
                    statusRows.add(new String[]{
                        STATUS_LABELS.get(index),
                        String.valueOf(data.orderStatus().getOrDefault(
                                STATUS_KEYS.get(index), 0))
                    });
                }
                document.add(pdfTable(
                        new String[]{"Status", "Jumlah"}, statusRows));

                document.add(section("Metode Pembayaran", sectionFont));
                List<String[]> paymentRows = new java.util.ArrayList<>();
                for (String label : PAYMENT_LABELS) {
                    paymentRows.add(new String[]{
                        label,
                        String.valueOf(
                                data.paymentMethods().getOrDefault(label, 0))
                    });
                }
                document.add(pdfTable(
                        new String[]{"Metode", "Jumlah"}, paymentRows));

                document.add(section("Top 5 Menu Terlaris", sectionFont));
                List<String[]> products = new java.util.ArrayList<>();
                int rank = 1;
                for (TopSellingProduct product : data.topProducts()) {
                    products.add(new String[]{
                        String.valueOf(rank++),
                        product.productName(),
                        String.valueOf(product.quantitySold()),
                        formatRupiah(product.revenue())
                    });
                }
                if (products.isEmpty()) {
                    products.add(new String[]{"-", "Belum ada data", "0", "Rp0"});
                }
                document.add(pdfTable(
                        new String[]{"No", "Menu", "Terjual", "Pendapatan"},
                        products));

                document.add(section("Detail Transaksi", sectionFont));
                List<String[]> transactions = new java.util.ArrayList<>();
                for (TransactionReportItem item : data.transactions()) {
                    transactions.add(new String[]{
                        item.transactionCode(),
                        item.transactionDate() == null
                                ? "-" : item.transactionDate().format(DATE_TIME),
                        item.customerName(),
                        String.valueOf(item.totalProduct()),
                        formatRupiah(item.totalAmount()),
                        item.paymentMethod(),
                        readableStatus(item.status())
                    });
                }
                if (transactions.isEmpty()) {
                    transactions.add(new String[]{
                        "-", "-", "Belum ada transaksi", "0", "Rp0", "-", "-"
                    });
                }
                document.add(pdfTable(
                        new String[]{"ID", "Tanggal", "Pelanggan / Meja",
                            "Produk", "Total", "Pembayaran", "Status"},
                        transactions));
            } finally {
                if (document.isOpen()) {
                    document.close();
                }
            }
        }
    }

    private void createHeader(
            Sheet sheet, CellStyle style, String... headers) {
        Row row = sheet.createRow(0);
        for (int index = 0; index < headers.length; index++) {
            Cell cell = row.createCell(index);
            cell.setCellValue(headers[index]);
            cell.setCellStyle(style);
        }
    }

    private void autoSize(Sheet sheet, int columns) {
        for (int column = 0; column < columns; column++) {
            sheet.autoSizeColumn(column);
            int current = sheet.getColumnWidth(column);
            sheet.setColumnWidth(
                    column,
                    Math.min(current + 700, 15000));
        }
    }

    private void addWordParagraph(
            XWPFDocument document, String text, boolean bold) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(bold);
        run.setText(text);
    }

    private void addWordTable(
            XWPFDocument document,
            String[] headers,
            List<String[]> rows) {
        XWPFTable table = document.createTable();
        XWPFTableRow headerRow = table.getRow(0);
        for (int index = 0; index < headers.length; index++) {
            XWPFTableCell cell = index == 0
                    ? headerRow.getCell(0) : headerRow.addNewTableCell();
            setWordCell(cell, headers[index], true);
        }
        for (String[] values : rows) {
            XWPFTableRow row = table.createRow();
            for (int index = 0; index < headers.length; index++) {
                setWordCell(
                        row.getCell(index),
                        index < values.length ? values[index] : "",
                        false);
            }
        }
    }

    private void setWordCell(
            XWPFTableCell cell, String text, boolean bold) {
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(bold);
        run.setText(text == null ? "" : text);
    }

    private PdfPTable pdfTable(
            String[] headers, List<String[]> rows) {
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setSpacingAfter(8);
        table.setHeaderRows(1);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(
                    header,
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
            cell.setBackgroundColor(new Color(119, 70, 44));
            cell.setPadding(5);
            table.addCell(cell);
        }
        for (String[] row : rows) {
            for (int index = 0; index < headers.length; index++) {
                PdfPCell cell = new PdfPCell(new Phrase(
                        index < row.length && row[index] != null
                                ? row[index] : "",
                        FontFactory.getFont(
                                FontFactory.HELVETICA, 8)));
                cell.setPadding(4);
                table.addCell(cell);
            }
        }
        return table;
    }

    private Paragraph section(
            String title, com.lowagie.text.Font font) {
        Paragraph paragraph = new Paragraph(title, font);
        paragraph.setSpacingBefore(6);
        paragraph.setSpacingAfter(4);
        return paragraph;
    }

    public static String periodText(SalesFilter filter) {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return filter.startDate().format(date)
                + " - " + filter.endDate().format(date);
    }

    public static String formatRupiah(BigDecimal amount) {
        BigDecimal safe = amount == null ? BigDecimal.ZERO : amount;
        java.text.DecimalFormatSymbols symbols
                = java.text.DecimalFormatSymbols.getInstance(
                        LocaleHolder.INDONESIAN);
        symbols.setGroupingSeparator('.');
        return "Rp" + new java.text.DecimalFormat(
                "#,##0", symbols).format(safe.longValue());
    }

    public static String readableStatus(String status) {
        if (status == null) {
            return "-";
        }
        return switch (status.toLowerCase(java.util.Locale.ROOT)) {
            case "menunggu" -> "Menunggu";
            case "diproses" -> "Diproses Baker";
            case "siap_diantar" -> "Siap Diantar";
            case "menunggu_pembayaran" -> "Menunggu Pembayaran";
            case "selesai" -> "Selesai";
            case "dibatalkan" -> "Dibatalkan";
            default -> status;
        };
    }

    private static final class LocaleHolder {
        private static final java.util.Locale INDONESIAN
                = java.util.Locale.forLanguageTag("id-ID");
    }

    private static final class PageFooter extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            Phrase footer = new Phrase(
                    "SwiftBite Morning Bakery | Halaman "
                    + writer.getPageNumber(),
                    FontFactory.getFont(
                            BaseFont.HELVETICA, 8));
            com.lowagie.text.pdf.ColumnText.showTextAligned(
                    writer.getDirectContent(),
                    Element.ALIGN_CENTER,
                    footer,
                    (document.left() + document.right()) / 2,
                    document.bottom() - 16,
                    0);
        }
    }
}
