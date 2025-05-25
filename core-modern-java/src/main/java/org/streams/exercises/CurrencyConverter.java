package org.streams.exercises;

import java.math.BigDecimal;
import java.util.Map;

public record CurrencyConverter(
        Map<String, BigDecimal> currencyMap) {

}
