package org;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransactionAnalyzer {

    public static Map<String, CustomerReport> analyze(Stream<Transaction> parallel,
            CurrencyConverter currencyConverter) {

        Map<String, CustomerReport> result = new HashMap<>();

        // Transaction firstTransaction = parallel.findFirst().orElse(null);

        // Stream<Object> currenciesStream = parallel.map(inner -> inner.currency());

        // List<String> currencies = parallel.map(inner ->
        // inner.currency()).collect(getCollectorsToList());

        Object a = parallel.map(inner -> inner.currency()).collect(getCollectorsGroupingBy());

        System.out.println(a);

        return result;

    }

    private static Collector getCollectorsGroupingBy() {
       return Collectors.groupingBy(Transaction::currency);
    }

    private static <T> Collector<T, ?, List<T>> getCollectorsToList() {

        return Collectors.toList();
    }
    // Collector<Employee, ?, Map<Department, Integer>> summingSalariesByDept
    // = Collectors.groupingBy(Employee::getDepartment, summingSalaries);
}
