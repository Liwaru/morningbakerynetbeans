package morning_bakery.sales;

import java.math.BigDecimal;

public record SalesSummary(
        int totalOrders,
        int totalProducts,
        BigDecimal totalRevenue,
        BigDecimal averageTransaction) {
}
