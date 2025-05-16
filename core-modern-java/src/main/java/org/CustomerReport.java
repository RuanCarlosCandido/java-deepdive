package org;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public record CustomerReport(
        BigDecimal totalInBRL,
        Map<TransactionType, Integer> transactionCountByType,
        YearMonth peakMonth,
        Map<YearMonth, BigDecimal> monthValue) {

}
