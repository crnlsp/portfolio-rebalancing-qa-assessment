package com.portfolio.rebalancing;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

/** An allocation input expressed in percentage points, e.g. 20 = 20%. */
public record SecurityAllocation(
        String symbol,
        BigDecimal targetPercent,
        BigDecimal currentPercent,
        BigDecimal unitPrice) {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public SecurityAllocation {
        Objects.requireNonNull(symbol, "symbol must not be null");
        symbol = symbol.strip().toUpperCase(Locale.ROOT);
        if (symbol.isEmpty()) {
            throw new IllegalArgumentException("symbol must not be blank");
        }
        validatePercent(targetPercent, "targetPercent");
        validatePercent(currentPercent, "currentPercent");
        Objects.requireNonNull(unitPrice, "unitPrice must not be null");
        if (unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("unitPrice must be positive");
        }
    }

    private static void validatePercent(BigDecimal percent, String fieldName) {
        Objects.requireNonNull(percent, fieldName + " must not be null");
        if (percent.signum() < 0 || percent.compareTo(HUNDRED) > 0) {
            throw new IllegalArgumentException(fieldName + " must be between 0 and 100");
        }
    }
}