# Portfolio Rebalancing — QA Assessment

Java 17 / Maven / JUnit 5 solution to the rebalancing exercise.

The assessment provides allocations and unit prices, but no running application.
I added a small calculator to test the trade recommendations. Automated tests are in
`src/test`; manual scenarios are in [docs/manual-test-cases.md](docs/manual-test-cases.md).

## Run

Requires Java 17+ and Maven.

```bash
mvn clean verify
java -cp target/classes com.portfolio.rebalancing.Main
```

## Calculation and results

The task defines variance as `current% - target%` (negative means BUY, positive SELL).
The calculator uses `target% - current%`, reversing the sign but not the direction:

```text
trade value = total assets × abs(target% - current%) / 100
shares = trade value / unit price (up to 6 decimal places, rounded down)
```

Account ABC, total assets **100 000 USD**:

| Symbol | Action | Shares |
| --- | --- | ---: |
| IBM | BUY | 66,666666 |
| MSFT | HOLD | 0 |
| ORCL | SELL | 45,454545 |
| AAPL | HOLD | 0 |
| HD | HOLD | 0 |

To reach the target allocation exactly, we would need to buy **10 000 USD** of
IBM and sell **10 000 USD** of ORCL. That corresponds to repeating decimal share
quantities:

- IBM: `10 000 / 150 = 66,6666…` shares to buy.
- ORCL: `10 000 / 220 = 45,4545…` shares to sell.

The calculator supports six decimal places and rounds down, so the recommendations
are **BUY 66,666666 IBM** and **SELL 45,454545 ORCL**. At the given prices,
each recommendation is worth **9 999,9999 USD** before rounding to currency
units. The buy and sell amounts match in this example, but the resulting
allocations are only approximately 20% due to share rounding.

These are calculated recommendations, not executed trades.

*Number formatting:* The examples in this README use spaces for thousands and
commas for decimals. Java source code, test inputs and console output use a dot
as the decimal separator (for example, `66.666666`).

## Assumptions and scope

- Fractional shares are supported to **six decimal places**; quantities are rounded
  **down** so a trade never exceeds its calculated notional. The task does not
  specify either policy, so both would need confirmation with the business.
- Prices stay fixed. Fees, taxes, slippage and market-price changes are ignored.
- Percentages are passed as `20` for 20%, and current/target totals must each be 100%.
- The calculator generates recommendations for each security independently. It does not
  check available cash or execute trades.

## Tests

- `AssessmentScenarioTest`: checks the five expected outputs independently.
- `RebalancingRulesTest`: checks buy/sell/hold, fractional shares and precision limits.
- `ValidationTest`: checks invalid account values, prices, percentages and tickers.
- [Manual test cases](docs/manual-test-cases.md): test ideas and expected results.

Run tests with `mvn clean verify`. The GitHub Actions workflow runs the same command
on pushes and pull requests.
