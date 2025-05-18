package org;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransactionAnalyzer {

    public static Map<String, CustomerReport> analyze(Stream<Transaction> transactionsStream,
            CurrencyConverter currencyConverter) {

        return solucaoFuncional(transactionsStream, currencyConverter);

        // return solucaoImperativa(transactionsStream, currencyConverter);
    }

    private static Map<String, CustomerReport> solucaoFuncional(Stream<Transaction> transactionsStream,
            CurrencyConverter currencyConverter) {

        return transactionsStream
                .filter(tx -> currencyConverter.currencyMap().containsKey(tx.currency()))
                .collect(
                        java.util.stream.Collectors.toConcurrentMap(
                                Transaction::user,
                                tx -> CustomerReportMapper.from(tx, currencyConverter),
                                CustomerReportMapper::merge));

    }

    private static Map<String, CustomerReport> solucaoImperativa(Stream<Transaction> transactionsStream,
            CurrencyConverter currencyConverter) {
        List<Transaction> transactions = transactionsStream.collect(Collectors.toList());

        Map<String, CustomerReport> userReports = new HashMap<>();

        for (Transaction transaction : transactions) {

            userReports.put(transaction.user(), calculate(currencyConverter, userReports, transaction));

        }

        System.out.println(userReports);
        return userReports;
    }

    private static CustomerReport calculate(CurrencyConverter currencyConverter,
            Map<String, CustomerReport> userReports, Transaction transaction) {

        String userId = transaction.user();

        CustomerReport finalReport;
        if (!currencyConverter.currencyMap().containsKey(transaction.currency()))
            throw new IllegalArgumentException("Currency not mapped");

        // Tentar extrair a logica daqui pra classe CustomerReportMapper ou outra coisa
        // semelhante, facilitando o uso dos streams
        if (userReports.containsKey(userId)) {

            finalReport = CustomerReportMapper.from(transaction, currencyConverter, userReports);

        } else {

            finalReport = CustomerReportMapper.from(transaction, currencyConverter);
        }
        return finalReport;
    }

}
