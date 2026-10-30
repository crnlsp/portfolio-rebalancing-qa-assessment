package com.portfolio.rebalancing;

import java.math.BigDecimal;
import java.util.List;

/** Runs the exact scenario from the assessment. */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        List<SecurityAllocation> securities = List.of(
                security("IBM", "20", "10", "150"),
                security("MSFT", "20", "20", "90"),
                security("ORCL", "20", "30", "220"),
                security("AAPL", "20", "20", "450"),
                security("HD", "20", "20", "70")
        );
        List<TradeRecommendation> results = new PortfolioRebalancer()
                .calculate(new BigDecimal("100000"), securities);

        System.out.println("Account ABC: 100 000 USD (fractional shares, 6 decimal places)");
        System.out.printf("%-8s %-6s %s%n", "Symbol", "Action", "Shares");
        for (TradeRecommendation result : results) {
            System.out.printf("%-8s %-6s %s%n", result.symbol(), result.action(),
                    result.shares().stripTrailingZeros().toPlainString());
        }
    }

    private static SecurityAllocation security(
            String symbol, String target, String current, String price) {
        return new SecurityAllocation(symbol, new BigDecimal(target),
                new BigDecimal(current), new BigDecimal(price));
    }
}
