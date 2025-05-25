package org.streams.exercises;

import java.math.BigDecimal;
import java.time.YearMonth;

public record Transaction(
        String user,
        TransactionType transactionType,
        BigDecimal value,
        String currency,
        YearMonth yearMonth) {
}
