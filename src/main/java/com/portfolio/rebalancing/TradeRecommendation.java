package com.portfolio.rebalancing;

import java.math.BigDecimal;

/** Calculated trade quantity. Direction is separate from the non-negative share count. */
public record TradeRecommendation(String symbol, TradeAction action, BigDecimal shares) {
}