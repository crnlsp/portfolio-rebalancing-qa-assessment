package com.portfolio.rebalancing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RebalancingRulesTest {
    private final PortfolioRebalancer rebalancer = new PortfolioRebalancer();

    private static TradeRecommendation trade(String symbol, TradeAction action, String shares) {
        return new TradeRecommendation(symbol, action, new BigDecimal(shares).setScale(6));
    }

    private static SecurityAllocation security(
            String symbol, String target, String current, String price) {
        return new SecurityAllocation(symbol, new BigDecimal(target),
                new BigDecimal(current), new BigDecimal(price));
    }

    @Test
    void buysUnderweightAndSellsOverweightSecurity() {
        assertEquals(List.of(
                trade("AAA", TradeAction.BUY, "10"),
                trade("BBB", TradeAction.SELL, "4")
        ), calculate("50", "40", "10", "50", "60", "25"));
    }

    @Test
    void holdsWhenAllocationsAlreadyMatchTargets() {
        assertEquals(List.of(
                trade("AAA", TradeAction.HOLD, "0"),
                trade("BBB", TradeAction.HOLD, "0")
        ), calculate("50", "50", "10", "50", "50", "25"));
    }

    @Test
    void roundsFractionalSharesDownToSixDecimals() {
        // $100 / $15 = 6.666666..., so keep 6 decimals without rounding up.
        assertEquals(trade("AAA", TradeAction.BUY, "6.666666"),
                calculate("50", "40", "15", "50", "60", "10").get(0));
    }

    @Test
    void buysLessThanOneShare() {
        assertEquals(trade("AAA", TradeAction.BUY, "0.5"),
                calculate("50", "40", "200", "50", "60", "10").get(0));
    }

    @Test
    void buysExactlyOneShare() {
        assertEquals(trade("AAA", TradeAction.BUY, "1"),
                calculate("50", "40", "100", "50", "60", "10").get(0));
    }

    @Test
    void holdsWhenTradeIsBelowMinimumFraction() {
        // 0.0000001 share becomes zero when keeping only six decimal places.
        assertEquals(trade("AAA", TradeAction.HOLD, "0"),
                calculate("50", "49.99999999", "1", "50", "50.00000001", "1").get(0));
    }

    @Test
    void supportsDecimalAllocationPercentages() {
        assertEquals(List.of(
                trade("AAA", TradeAction.BUY, "2"),
                trade("BBB", TradeAction.SELL, "2")
        ), calculate("50", "49.9", "0.5", "50", "50.1", "0.5"));
    }

    private List<TradeRecommendation> calculate(String targetA, String currentA,
                                                String priceA, String targetB,
                                                String currentB, String priceB) {
        return rebalancer.calculate(new BigDecimal("1000"), List.of(
                security("AAA", targetA, currentA, priceA),
                security("BBB", targetB, currentB, priceB)));
    }
}
