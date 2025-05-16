package org;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransactionAnalyzer {

    public static Map<String, CustomerReport> analyze(Stream<Transaction> transactionsStream,
            CurrencyConverter currencyConverter) {

        return solucaoFuncional(transactionsStream, currencyConverter);

        //return solucaoImperativa(transactionsStream, currencyConverter);
    }

    private static Map<String, CustomerReport> solucaoFuncional(Stream<Transaction> transactionsStream,
            CurrencyConverter currencyConverter) {

        transactionsStream
        .filter(transaction -> currencyConverter.currencyMap().containsKey(transaction.currency()))
        .peek(transaction -> System.out.println("[DEBUG] Processando transacao: " + transaction)).collect(Collectors.toList());
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'solucaoFuncional'");
    }

    private static Map<String, CustomerReport> solucaoImperativa(Stream<Transaction> transactionsStream,
            CurrencyConverter currencyConverter) {
        List<Transaction> transactions = transactionsStream.collect(Collectors.toList());

        Map<String, CustomerReport> userReports = new HashMap<>();

        for (Transaction transaction : transactions) {

            if(!currencyConverter.currencyMap().containsKey(transaction.currency())) throw new IllegalArgumentException("Currency not mapped");

            BigDecimal amount = transaction.value().multiply(currencyConverter.currencyMap().get(transaction.currency()));

            String userId = transaction.user();

            if (userReports.containsKey(userId)) {
                CustomerReport existingReport = userReports.get(userId);

                Map<TransactionType, Integer> countsByType = existingReport.transactionCountByType();

                Map<YearMonth, BigDecimal> monthlyHighestValue = existingReport.monthValue();
                for (Entry<YearMonth, BigDecimal> entry : monthlyHighestValue.entrySet()) {
                    if (entry.getValue().compareTo(transaction.value()) < 0) {
                        Map<YearMonth, BigDecimal> updatedMonthlyValue = new HashMap<>();
                        updatedMonthlyValue.put(transaction.yearMonth(), amount);
                        monthlyHighestValue = updatedMonthlyValue;
                    }
                }

                if (countsByType.containsKey(transaction.transactionType())) {
                    int newCount = countsByType.get(transaction.transactionType()) + 1;
                    countsByType.put(transaction.transactionType(), newCount);
                } else {
                    countsByType.put(transaction.transactionType(), 1);
                }

                BigDecimal existingTotalInBRL = existingReport.totalInBRL();
                BigDecimal updatedTotalInBRL = existingTotalInBRL.add(amount);

                CustomerReport updatedReport = new CustomerReport(
                        updatedTotalInBRL,
                        countsByType,
                        monthlyHighestValue.keySet().stream().findFirst().orElse(null),
                        monthlyHighestValue);
                userReports.put(userId, updatedReport);

            } else {
                Map<TransactionType, Integer> countsByType = new HashMap<>();
                countsByType.put(transaction.transactionType(), 1);

                CustomerReport newReport = new CustomerReport(
                        amount,
                        countsByType,
                        transaction.yearMonth(),
                        Map.of(transaction.yearMonth(), transaction.value()));
                userReports.put(userId, newReport);
            }
        }

        System.out.println(userReports);
        return userReports;
    }

}
