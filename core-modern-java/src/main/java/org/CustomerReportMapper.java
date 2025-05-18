package org;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomerReportMapper {

        public static CustomerReport from(Transaction tx, CurrencyConverter cc) {

                
                BigDecimal amountInBRL = tx.value().multiply(cc.currencyMap().get(tx.currency()));

                return new CustomerReport(
                                amountInBRL,
                                Map.of(tx.transactionType(), 1),
                                tx.yearMonth(),
                                Map.of(tx.yearMonth(), amountInBRL));
        }

        public static CustomerReport merge(CustomerReport r1, CustomerReport r2) {
                BigDecimal total = r1.totalInBRL().add(r2.totalInBRL());

                // Soma as contagens por tipo de transação
                Map<TransactionType, Integer> countsByType = Stream
                                .concat(r1.transactionCountByType().entrySet().stream(),
                                                r2.transactionCountByType().entrySet().stream())
                                .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                Map.Entry::getValue,
                                                Integer::sum));

                // Combina os valores mensais, mantendo o maior valor de cada mês
                Map<YearMonth, BigDecimal> monthValues = Stream
                                .concat(r1.monthValue().entrySet().stream(), r2.monthValue().entrySet().stream())
                                .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                Map.Entry::getValue,
                                                BigDecimal::max));

                // Determina o mês de maior valor
                YearMonth peakMonth = monthValues.entrySet().stream()
                                .max(Comparator.comparing(Map.Entry::getValue))
                                .map(Map.Entry::getKey)
                                .orElse(null);

                return new CustomerReport(total, countsByType, peakMonth, monthValues);
        }
}