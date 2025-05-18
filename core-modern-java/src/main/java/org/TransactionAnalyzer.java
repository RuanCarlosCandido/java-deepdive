package org;

import java.util.Map;
import java.util.stream.Stream;

public class TransactionAnalyzer {

    public static Map<String, CustomerReport> analyze(Stream<Transaction> transactionsStream,
            CurrencyConverter currencyConverter) {

        return transactionsStream
                .filter(tx -> currencyConverter.currencyMap().containsKey(tx.currency()))
                .collect(
                        java.util.stream.Collectors.toConcurrentMap(
                                Transaction::user,
                                tx -> CustomerReportMapper.from(tx, currencyConverter),
                                CustomerReportMapper::merge));
    }

}
