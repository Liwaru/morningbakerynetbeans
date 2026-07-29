package morning_bakery.sales;

import java.math.BigDecimal;

public record TransactionProductItem(
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal) {
}
