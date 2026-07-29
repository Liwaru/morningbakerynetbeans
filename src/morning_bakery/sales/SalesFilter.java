package morning_bakery.sales;

import java.time.LocalDate;

/** Filter aktif untuk query dan export Laporan Penjualan. */
public record SalesFilter(
        LocalDate startDate,
        LocalDate endDate,
        String periodType,
        String status,
        String paymentMethod,
        String keyword) {

    public SalesFilter {
        keyword = keyword == null ? "" : keyword.trim();
    }
}
