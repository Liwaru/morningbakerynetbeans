package morning_bakery.sales;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/** Uji query nyata serta pembuatan XLSX, DOCX, dan PDF tanpa dialog UI. */
public final class SalesReportSmokeTest {

    private SalesReportSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        SalesFilter filter = new SalesFilter(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                "Bulanan",
                null,
                null,
                "");
        SalesReportData data
                = new LaporanPenjualanDAO().getSalesReport(filter);
        if (data.summary().totalOrders() <= 0) {
            throw new AssertionError(
                    "Data transaksi Juni 2026 seharusnya tersedia.");
        }
        if (data.chartItems().size() != 30) {
            throw new AssertionError(
                    "Grafik bulanan harus memiliki seluruh tanggal.");
        }
        SalesFilter qrisCompleted = new SalesFilter(
                filter.startDate(),
                filter.endDate(),
                "Bulanan",
                "selesai",
                "qris",
                "");
        SalesReportData filtered
                = new LaporanPenjualanDAO().getSalesReport(qrisCompleted);
        for (TransactionReportItem item : filtered.transactions()) {
            if (!"selesai".equals(item.status())
                    || !"QRIS".equals(item.paymentMethod())) {
                throw new AssertionError(
                        "Filter status/metode tidak konsisten.");
            }
        }

        Path outputDirectory = Path.of("build", "sales-report-smoke");
        Files.createDirectories(outputDirectory);
        Path excel = outputDirectory.resolve("laporan.xlsx");
        Path word = outputDirectory.resolve("laporan.docx");
        Path pdf = outputDirectory.resolve("laporan.pdf");
        SalesReportExportService service = new SalesReportExportService();
        service.exportExcel(excel, filter, data);
        service.exportWord(word, filter, data);
        service.exportPdf(pdf, filter, data);

        for (Path file : new Path[]{excel, word, pdf}) {
            if (!Files.isRegularFile(file) || Files.size(file) < 100) {
                throw new AssertionError(
                        "Export gagal dibuat: " + file);
            }
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook(
                Files.newInputStream(excel))) {
            if (workbook.getNumberOfSheets() != 5) {
                throw new AssertionError(
                        "Workbook harus memiliki lima sheet.");
            }
        }
        try (XWPFDocument document = new XWPFDocument(
                Files.newInputStream(word))) {
            if (document.getTables().isEmpty()) {
                throw new AssertionError(
                        "Dokumen Word harus memiliki tabel laporan.");
            }
        }
        byte[] pdfHeader = Files.readAllBytes(pdf);
        if (pdfHeader.length < 4
                || pdfHeader[0] != '%'
                || pdfHeader[1] != 'P'
                || pdfHeader[2] != 'D'
                || pdfHeader[3] != 'F') {
            throw new AssertionError("File PDF tidak valid.");
        }
        SalesReportPrintable printable
                = new SalesReportPrintable(filter, data);
        PageFormat pageFormat = new PageFormat();
        Paper paper = new Paper();
        paper.setSize(842, 595);
        paper.setImageableArea(24, 24, 794, 547);
        pageFormat.setPaper(paper);
        pageFormat.setOrientation(PageFormat.LANDSCAPE);
        BufferedImage printImage = new BufferedImage(
                842, 595, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = printImage.createGraphics();
        int printResult = printable.print(graphics, pageFormat, 0);
        graphics.dispose();
        if (printResult != Printable.PAGE_EXISTS) {
            throw new AssertionError(
                    "Halaman print pertama harus tersedia.");
        }
        System.out.println(
                "SalesReport OK: orders="
                + data.summary().totalOrders()
                + ", products=" + data.summary().totalProducts()
                + ", revenue=" + data.summary().totalRevenue());
    }
}
