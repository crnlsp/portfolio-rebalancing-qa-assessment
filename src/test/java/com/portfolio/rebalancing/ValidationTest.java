package com.portfolio.rebalancing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationTest {
    private final PortfolioRebalancer rebalancer = new PortfolioRebalancer();

    private static SecurityAllocation security(
            String symbol, String target, String current, String price) {
        return new SecurityAllocation(symbol, new BigDecimal(target),
                new BigDecimal(current), new BigDecimal(price));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void rejectsNonPositiveAccountValue(String amount) {
        assertThrows(IllegalArgumentException.class, () -> rebalancer.calculate(
                new BigDecimal(amount), List.of(security("AAA", "100", "100", "10"))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void rejectsNonPositiveSharePrice(String price) {
        assertThrows(IllegalArgumentException.class,
                () -> security("AAA", "100", "100", price));
    }

    @Test
    void rejectsAllocationsThatDoNotTotal100Percent() {
        assertThrows(IllegalArgumentException.class, () -> rebalancer.calculate(
                new BigDecimal("1000"), List.of(
                        security("AAA", "40", "50", "10"),
                        security("BBB", "40", "50", "10"))));

        assertThrows(IllegalArgumentException.class, () -> rebalancer.calculate(
                new BigDecimal("1000"), List.of(
                        security("AAA", "50", "40", "10"),
                        security("BBB", "50", "40", "10"))));
    }

    @Test
    void rejectsDuplicateSymbols() {
        assertThrows(IllegalArgumentException.class, () -> rebalancer.calculate(
                new BigDecimal("1000"), List.of(
                        security("IBM", "50", "50", "10"),
                        security("ibm", "50", "50", "20"))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "101"})
    void rejectsPercentageOutside0To100(String percent) {
        assertThrows(IllegalArgumentException.class,
                () -> security("AAA", percent, "50", "10"));
    }

    @Test
    void rejectsEmptyPortfolio() {
        assertThrows(IllegalArgumentException.class,
                () -> rebalancer.calculate(new BigDecimal("1000"), List.of()));
    }
}
