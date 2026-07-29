package morning_bakery.sales;

import java.util.List;
import java.util.Map;

public record SalesReportData(
        SalesSummary summary,
        Map<String, Integer> orderStatus,
        Map<String, Integer> paymentMethods,
        List<TopSellingProduct> topProducts,
        List<SalesChartItem> chartItems,
        List<TransactionReportItem> transactions) {
}
