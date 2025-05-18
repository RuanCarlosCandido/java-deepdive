package org;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CustomerReportMapper {

        public static CustomerReport from(Transaction transaction, CurrencyConverter currencyConverter) {
            if (!currencyConverter.currencyMap().containsKey(transaction.currency()))
                throw new IllegalArgumentException("Currency not mapped");
    
            BigDecimal amountInBRL = transaction.value()
                    .multiply(currencyConverter.currencyMap().get(transaction.currency()));
    
            Map<TransactionType, Integer> countsByType = new HashMap<>();
            countsByType.put(transaction.transactionType(), 1);
    
            Map<YearMonth, BigDecimal> monthValue = new HashMap<>();
            monthValue.put(transaction.yearMonth(), amountInBRL);
    
            return new CustomerReport(
                    amountInBRL,
                    countsByType,
                    transaction.yearMonth(),
                    monthValue
            );
        }
    
        public static CustomerReport from(Transaction transaction, CurrencyConverter currencyConverter,
                                          Map<String, CustomerReport> userReports) {
    
            String userId = transaction.user();
            BigDecimal amountInBRL = transaction.value()
                    .multiply(currencyConverter.currencyMap().get(transaction.currency()));
    
            CustomerReport existingReport = userReports.get(userId);
    
            // Defensive copies
            Map<TransactionType, Integer> countsByType = new HashMap<>(existingReport.transactionCountByType());
            Map<YearMonth, BigDecimal> monthValue = new HashMap<>(existingReport.monthValue());
    
            // Atualiza contagem por tipo
            countsByType.merge(transaction.transactionType(), 1, Integer::sum);
    
            // Atualiza valor mensal se for maior
            monthValue.merge(transaction.yearMonth(), amountInBRL, (oldVal, newVal) ->
                    newVal.compareTo(oldVal) > 0 ? newVal : oldVal
            );
    
            // Define novo peakMonth
            YearMonth peakMonth = monthValue.entrySet().stream()
                    .max(Comparator.comparing(Entry::getValue))
                    .map(Entry::getKey)
                    .orElse(transaction.yearMonth());
    
            BigDecimal total = existingReport.totalInBRL().add(amountInBRL);
    
            return new CustomerReport(total, countsByType, peakMonth, monthValue);
        }
    
        public static CustomerReport merge(CustomerReport r1, CustomerReport r2) {
            BigDecimal total = r1.totalInBRL().add(r2.totalInBRL());
    
            Map<TransactionType, Integer> combinedCounts = new HashMap<>(r1.transactionCountByType());
            r2.transactionCountByType().forEach((type, count) ->
                    combinedCounts.merge(type, count, Integer::sum)
            );
    
            Map<YearMonth, BigDecimal> combinedMonthValues = new HashMap<>(r1.monthValue());
            r2.monthValue().forEach((month, value) ->
                    combinedMonthValues.merge(month, value, (v1, v2) -> v1.max(v2))
            );
    
            YearMonth peakMonth = combinedMonthValues.entrySet().stream()
                    .max(Comparator.comparing(Entry::getValue))
                    .map(Entry::getKey)
                    .orElse(null);
    
            return new CustomerReport(total, combinedCounts, peakMonth, combinedMonthValues);
        }
    }