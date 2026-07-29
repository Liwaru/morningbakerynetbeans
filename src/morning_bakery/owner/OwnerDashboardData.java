package morning_bakery.owner;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

/** Snapshot lengkap data yang ditampilkan pada Dashboard Owner. */
public record OwnerDashboardData(
        BigDecimal todayRevenue,
        int todayOrderCount,
        BigDecimal todayAverageTransaction,
        String todayBestSellingMenu,
        Map<String, Integer> orderStatusSummary,
        Map<DayOfWeek, BigDecimal> currentWeekRevenue,
        List<TopMenuItem> topMenus,
        Map<String, Integer> paymentMethodSummary) {

    public static OwnerDashboardData empty() {
        return new OwnerDashboardData(
                BigDecimal.ZERO,
                0,
                BigDecimal.ZERO,
                "-",
                Map.of(),
                Map.of(),
                List.of(),
                Map.of());
    }
}
