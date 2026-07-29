package morning_bakery.sales;

import java.math.BigDecimal;

public record SalesChartItem(
        String label,
        int totalOrders,
        int completedOrders,
        int productsSold,
        BigDecimal revenue) {
}
