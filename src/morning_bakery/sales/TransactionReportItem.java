package morning_bakery.sales;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionReportItem(
        long transactionId,
        String transactionCode,
        LocalDateTime transactionDate,
        String customerName,
        int totalProduct,
        BigDecimal totalAmount,
        String paymentMethod,
        String status,
        String notes) {
}
