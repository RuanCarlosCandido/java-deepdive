package org;

import java.math.BigDecimal;
import java.time.YearMonth;

public record Transaction(
        String user,
        TransactionType transactionType,
        BigDecimal bigDecimal,
        String currency,
        YearMonth of) {
}
