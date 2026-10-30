package com.portfolio.rebalancing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Calculates trade recommendations without placing orders. */
public final class PortfolioRebalancer {
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int SHARE_DECIMAL_PLACES = 6;

    public List<TradeRecommendation> calculate(
            BigDecimal totalAssets,
            List<SecurityAllocation> allocations) {

        Objects.requireNonNull(totalAssets, "totalAssets must not be null");
        Objects.requireNonNull(allocations, "allocations must not be null");
        if (totalAssets.signum() <= 0) {
            throw new IllegalArgumentException("totalAssets must be positive");
        }
        if (allocations.isEmpty()) {
            throw new IllegalArgumentException("allocations must not be empty");
        }

        BigDecimal targetSum = BigDecimal.ZERO;
        BigDecimal currentSum = BigDecimal.ZERO;
        Set<String> symbols = new HashSet<>();

        for (SecurityAllocation allocation : allocations) {
            Objects.requireNonNull(allocation, "allocation must not be null");
            if (!symbols.add(allocation.symbol())) {
                throw new IllegalArgumentException("duplicate symbol: " + allocation.symbol());
            }
            targetSum = targetSum.add(allocation.targetPercent());
            currentSum = currentSum.add(allocation.currentPercent());
        }
        if (targetSum.compareTo(HUNDRED) != 0) {
            throw new IllegalArgumentException("target percentages must total 100");
        }
        if (currentSum.compareTo(HUNDRED) != 0) {
            throw new IllegalArgumentException("current percentages must total 100");
        }

        List<TradeRecommendation> recommendations = new ArrayList<>();
        for (SecurityAllocation allocation : allocations) {
            // Positive target minus current means BUY, negative means SELL.
            BigDecimal difference = allocation.targetPercent()
                    .subtract(allocation.currentPercent());
            BigDecimal amount = totalAssets.multiply(difference.abs())
                    .divide(HUNDRED);
            // Allow fractional shares, up to six decimal places. Never exceed the target notional.
            BigDecimal shares = amount.divide(
                    allocation.unitPrice(), SHARE_DECIMAL_PLACES, RoundingMode.DOWN);

            TradeAction action = shares.signum() == 0 ? TradeAction.HOLD
                    : difference.signum() > 0 ? TradeAction.BUY : TradeAction.SELL;
            recommendations.add(new TradeRecommendation(allocation.symbol(), action, shares));
        }
        return List.copyOf(recommendations);
    }
}
