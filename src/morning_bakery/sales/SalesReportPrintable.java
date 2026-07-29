package morning_bakery.sales;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Printable berbasis data laporan; sidebar dan tombol UI tidak ikut dicetak. */
public final class SalesReportPrintable implements Printable {

    private static final DateTimeFormatter DATE_TIME
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final List<PrintLine> lines;

    public SalesReportPrintable(
            SalesFilter filter, SalesReportData data) {
        lines = buildLines(filter, data);
    }

    @Override
    public int print(
            Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        Graphics2D g = (Graphics2D) graphics.create();
        g.translate(
                pageFormat.getImageableX(),
                pageFormat.getImageableY());
        g.setColor(Color.BLACK);

        int lineHeight = 15;
        int linesPerPage = Math.max(
                1,
                (int) pageFormat.getImageableHeight() / lineHeight - 3);
        int start = pageIndex * linesPerPage;
        if (start >= lines.size()) {
            g.dispose();
            return NO_SUCH_PAGE;
        }

        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.drawString(
                "SwiftBite Morning Bakery - Laporan Penjualan",
                0, 11);
        g.setFont(new Font("SansSerif", Font.PLAIN, 8));
        g.drawString(
                "Halaman " + (pageIndex + 1),
                (int) pageFormat.getImageableWidth() - 70,
                11);

        int y = 30;
        int end = Math.min(lines.size(), start + linesPerPage);
        for (int index = start; index < end; index++) {
            PrintLine line = lines.get(index);
            g.setFont(new Font(
                    "SansSerif",
                    line.bold() ? Font.BOLD : Font.PLAIN,
                    line.size()));
            g.drawString(line.text(), line.indent(), y);
            y += lineHeight;
        }
        g.dispose();
        return PAGE_EXISTS;
    }

    private List<PrintLine> buildLines(
            SalesFilter filter, SalesReportData data) {
        List<PrintLine> result = new ArrayList<>();
        result.add(new PrintLine("LAPORAN PENJUALAN", true, 13, 0));
        result.add(new PrintLine(
                "Periode: " + SalesReportExportService.periodText(filter),
                true, 9, 0));
        result.add(new PrintLine(
                "Dicetak: " + LocalDateTime.now().format(DATE_TIME),
                false, 8, 0));

        SalesSummary summary = data.summary();
        result.add(section("RINGKASAN"));
        result.add(line("Total Pesanan: " + summary.totalOrders()));
        result.add(line("Produk Terjual: " + summary.totalProducts()));
        result.add(line("Pendapatan: "
                + SalesReportExportService.formatRupiah(
                        summary.totalRevenue())));
        result.add(line("Rata-rata Transaksi: "
                + SalesReportExportService.formatRupiah(
                        summary.averageTransaction())));

        String[] statusKeys = {
            "menunggu", "diproses", "siap_diantar",
            "menunggu_pembayaran", "selesai", "dibatalkan"
        };
        String[] statusLabels = {
            "Menunggu", "Diproses Baker", "Siap Diantar",
            "Menunggu Pembayaran", "Selesai", "Dibatalkan"
        };
        result.add(section("STATUS PESANAN"));
        for (int index = 0; index < statusKeys.length; index++) {
            result.add(line(statusLabels[index] + ": "
                    + data.orderStatus().getOrDefault(
                            statusKeys[index], 0)));
        }

        result.add(section("METODE PEMBAYARAN"));
        for (String payment : List.of(
                "Tunai", "QRIS", "GoPay", "DANA", "OVO",
                "ShopeePay", "E-Wallet Lain")) {
            result.add(line(payment + ": "
                    + data.paymentMethods().getOrDefault(payment, 0)));
        }

        result.add(section("TOP 5 MENU TERLARIS"));
        if (data.topProducts().isEmpty()) {
            result.add(line("Belum ada data penjualan."));
        } else {
            int rank = 1;
            for (TopSellingProduct product : data.topProducts()) {
                result.add(line(
                        rank++ + ". " + product.productName()
                        + " | " + product.quantitySold() + " terjual"
                        + " | " + SalesReportExportService.formatRupiah(
                                product.revenue())));
            }
        }

        result.add(section("DETAIL TRANSAKSI"));
        if (data.transactions().isEmpty()) {
            result.add(line("Belum ada transaksi pada periode ini."));
        } else {
            for (TransactionReportItem item : data.transactions()) {
                String date = item.transactionDate() == null
                        ? "-" : item.transactionDate().format(DATE_TIME);
                result.add(line(
                        item.transactionCode() + " | " + date
                        + " | " + item.customerName()
                        + " | Produk: " + item.totalProduct()
                        + " | " + SalesReportExportService.formatRupiah(
                                item.totalAmount())
                        + " | " + item.paymentMethod()
                        + " | " + SalesReportExportService.readableStatus(
                                item.status())));
            }
        }
        return result;
    }

    private PrintLine section(String text) {
        return new PrintLine(text, true, 10, 0);
    }

    private PrintLine line(String text) {
        return new PrintLine(text, false, 8, 12);
    }

    private record PrintLine(
            String text, boolean bold, int size, int indent) {
    }
}
