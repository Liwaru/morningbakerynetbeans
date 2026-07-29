package morning_bakery.sales;

import java.math.BigDecimal;

public record TopSellingProduct(
        long productId,
        String productName,
        int quantitySold,
        BigDecimal revenue) {
}
