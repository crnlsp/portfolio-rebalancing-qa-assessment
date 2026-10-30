package com.portfolio.rebalancing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentScenarioTest {
    private static TradeRecommendation trade(String symbol, TradeAction action, String shares) {
        return new TradeRecommendation(symbol, action, new BigDecimal(shares).setScale(6));
    }

    private static SecurityAllocation security(
            String symbol, String target, String current, String price) {
        return new SecurityAllocation(symbol, new BigDecimal(target),
                new BigDecimal(current), new BigDecimal(price));
    }

    @Test
    void calculatesExpectedTradesForAccountABC() {
        List<SecurityAllocation> securities = List.of(
                security("IBM", "20", "10", "150"),
                security("MSFT", "20", "20", "90"),
                security("ORCL", "20", "30", "220"),
                security("AAPL", "20", "20", "450"),
                security("HD", "20", "20", "70")
        );

        List<TradeRecommendation> actual = new PortfolioRebalancer()
                .calculate(new BigDecimal("100000"), securities);

        assertEquals(List.of(
                trade("IBM", TradeAction.BUY, "66.666666"),
                trade("MSFT", TradeAction.HOLD, "0"),
                trade("ORCL", TradeAction.SELL, "45.454545"),
                trade("AAPL", TradeAction.HOLD, "0"),
                trade("HD", TradeAction.HOLD, "0")
        ), actual);
    }
}
