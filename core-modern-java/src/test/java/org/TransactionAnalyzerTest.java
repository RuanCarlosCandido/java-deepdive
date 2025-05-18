package org;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test suite para o sistema de análise de transações financeiras.
 * <p>
 * <strong>Descrição do problema:</strong>
 * <ul>
 *   <li>Dada uma lista massiva de transações financeiras (clientes diferentes, múltiplas moedas, tipos de transação e datas), implemente um sistema que:</li>
 *   <li>Agrupe por cliente e retorne:
 *     <ul>
 *       <li>Soma total transacionada em <strong>BRL</strong></li>
 *       <li>Número de transações por tipo ({@code PIX}, {@code TED}, {@code BOLETO}, etc.)</li>
 *       <li>Mês de maior volume financeiro</li>
 *     </ul>
 *   </li>
 *   <li>Use {@code parallelStream} para processar em lote</li>
 *   <li>Converta automaticamente valores em outras moedas para BRL (mock de taxas)</li>
 *   <li>Implemente um {@code Collector} customizado para compor o relatório do cliente</li>
 * </ul>
 */
class TransactionAnalyzerTest {

    private List<Transaction> transactions;
    private CurrencyConverter currencyConverter;

    @BeforeEach
    @SuppressWarnings("unused")
    void setup() {
        currencyConverter = new CurrencyConverter(Map.of(
                "USD", new BigDecimal("5.00"),
                "EUR", new BigDecimal("6.00"),
                "BRL", BigDecimal.ONE));

        transactions = List.of(
                new Transaction("user1", TransactionType.PIX, new BigDecimal("100"), "BRL",
                        YearMonth.of(2024, Month.JANUARY)),
                new Transaction("user1", TransactionType.TED, new BigDecimal("20"), "USD",
                        YearMonth.of(2024, Month.JANUARY)),
                new Transaction("user1", TransactionType.BOLETO, new BigDecimal("200"), "BRL",
                        YearMonth.of(2024, Month.FEBRUARY)),
                new Transaction("user2", TransactionType.PIX, new BigDecimal("50"), "EUR",
                        YearMonth.of(2024, Month.JANUARY)),
                new Transaction("user2", TransactionType.PIX, new BigDecimal("100"), "BRL",
                        YearMonth.of(2024, Month.FEBRUARY)),
                new Transaction("user3", TransactionType.TED, new BigDecimal("100"), "USD",
                        YearMonth.of(2024, Month.MARCH)),
                new Transaction("user3", TransactionType.TED, new BigDecimal("200"), "USD",
                        YearMonth.of(2024, Month.MARCH)));
    }

    @Test
    void shouldCorrectlyAggregateTransactionSummaryByCustomer() {
        Map<String, CustomerReport> result = TransactionAnalyzer
                .analyze(transactions.stream().parallel(), currencyConverter);

        assertEquals(3, result.size());

        CustomerReport user1 = result.get("user1");
        assertNotNull(user1);
        assertEquals(new BigDecimal("400.00"), user1.totalInBRL().setScale(2));
        assertEquals(3, user1.transactionCountByType().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(YearMonth.of(2024, Month.FEBRUARY), user1.peakMonth());

        CustomerReport user2 = result.get("user2");
        assertEquals(new BigDecimal("400.00"), user2.totalInBRL().setScale(2)); // 50*6 + 100

        CustomerReport user3 = result.get("user3");
        assertEquals(2, user3.transactionCountByType().get(TransactionType.TED));
        assertEquals(YearMonth.of(2024, Month.MARCH), user3.peakMonth());
    }

    @Test
    void shouldHandleEmptyTransactionList() {
        Map<String, CustomerReport> result = TransactionAnalyzer.analyze(Stream.empty(), currencyConverter);
        assertTrue(result.isEmpty());
    }

        @Test
        void shouldHandleUnknownCurrencyGracefully() {
                List<Transaction> faulty = List
                        .of(new Transaction("userX", TransactionType.PIX, new BigDecimal("100"), "JPY", YearMonth.now()));

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                        () -> TransactionAnalyzer.analyze(faulty.stream(), currencyConverter));
                assertNotNull(exception.getMessage());
        }

    @Test
    void shouldWorkWithLargeDatasetInParallel() {
        List<Transaction> large = new ArrayList<>();
        for (int i = 0; i < 100_000; i++) {
            large.add(new Transaction("user" + (i % 100), TransactionType.PIX, BigDecimal.TEN, "USD",
                    YearMonth.of(2024, Month.JANUARY)));
        }

        Map<String, CustomerReport> result = TransactionAnalyzer.analyze(large.parallelStream(), currencyConverter);
        assertEquals(100, result.size());
        assertEquals(new BigDecimal("50000.00"), result.get("user0").totalInBRL());
    }
}
