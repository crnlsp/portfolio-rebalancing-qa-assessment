# Manual Test Cases — Portfolio Rebalancing

## Scope

Verify that the application returns the correct **security, trade direction (BUY / SELL / HOLD), and share quantity** for a portfolio rebalancing request. These are **black-box test cases**: they describe business inputs and expected outputs, not how the Java implementation works.

**Test status:** Designed, not executed against a supplied State Street application. The exercise provides no running UI or API.

**Assumptions:** As documented in the [README](../README.md), fractional shares are allowed to **six decimal places** and quantities are **rounded down**. Both current and target allocations must total 100%. Prices are fixed in USD; no fees or market movements are considered. These are project assumptions, not rules specified by the assessment.

**Notation:** `AAA (50 / 40 @ 10)` means security AAA has **target 50%**, **current 40%**, and **unit price 10 USD**. European formatting is used for displayed numbers: `1 000`, `6,666666`. Each test has its own portfolio; no state carries over between tests.

## How to execute

**Precondition:** A test environment accepts an account value and security allocations and allows the user to request a rebalancing calculation.

1. Enter the total asset value and all security rows specified for a test case.
2. Request rebalancing.
3. Check the **symbol, BUY / SELL / HOLD action, and share quantity for every security** against the expected result. Ensure no security is missing or duplicated.
4. For invalid inputs, check that the request is rejected and **no trade recommendations** are returned.
5. Record the actual output, PASS/FAIL and any defect reference when executing the test. These fields are intentionally not pre-filled.

**Priorities:** P0 = original requirement / critical business correctness; P1 = important boundaries and validation; P2 = additional robustness.

## 1. Assessment scenario

### TC-01 — Rebalance Account ABC · P0

**Total assets:** 100 000 USD

| Security | Target | Current | Unit price | Expected action | Expected shares |
| --- | ---: | ---: | ---: | --- | ---: |
| IBM | 20% | 10% | 150 USD | BUY | 66,666666 |
| MSFT | 20% | 20% | 90 USD | HOLD | 0 |
| ORCL | 20% | 30% | 220 USD | SELL | 45,454545 |
| AAPL | 20% | 20% | 450 USD | HOLD | 0 |
| HD | 20% | 20% | 70 USD | HOLD | 0 |

**Expected:** Exactly five recommendations. IBM is below target, ORCL is above target, and the other three securities remain unchanged. At the given prices, both recommended trades have a value of **9 999,9999 USD**; the small remaining allocation difference is due to six-decimal share rounding.

## 2. Calculation and boundary tests

All portfolios below have valid current and target totals of 100%.

| ID | Pri. | Scenario | Total assets and securities (target / current @ unit price) | Expected result |
| --- | :---: | --- | --- | --- |
| TC-02 | P0 | Basic buy and sell (exact quantities) | **1 000 USD**; AAA (50 / 40 @ 10); BBB (50 / 60 @ 25) | AAA **BUY 10**; BBB **SELL 4** |
| TC-03 | P0 | Already balanced | **1 000 USD**; AAA (50 / 50 @ 10); BBB (50 / 50 @ 25) | AAA **HOLD 0**; BBB **HOLD 0**; no trades |
| TC-04 | P1 | Different target weights across three securities | **2 000 USD**; AAA (50 / 25 @ 100); BBB (30 / 50 @ 40); CCC (20 / 25 @ 50) | AAA **BUY 5**; BBB **SELL 10**; CCC **SELL 2** |
| TC-05 | P1 | Security not currently owned | **1 000 USD**; AAA (20 / 0 @ 50); BBB (80 / 100 @ 20) | AAA **BUY 4**; BBB **SELL 10** |
| TC-06 | P1 | Target allocation is zero (sell entire allocation by value) | **1 000 USD**; AAA (0 / 20 @ 50); BBB (100 / 80 @ 20) | AAA **SELL 4**; BBB **BUY 10** |
| TC-07 | P2 | Single-security portfolio | **1 000 USD**; AAA (100 / 100 @ 10) | AAA **HOLD 0** |
| TC-08 | P1 | Decimal allocation percentages | **1 000 USD**; AAA (50 / 49,9 @ 0,50); BBB (50 / 50,1 @ 0,50) | AAA **BUY 2**; BBB **SELL 2** |
| TC-09 | P1 | Fractional trade smaller than one share | **1 000 USD**; AAA (50 / 40 @ 200); BBB (50 / 60 @ 10) | AAA **BUY 0,5**; BBB **SELL 10** |
| TC-10 | P0 | Six-decimal rounding | **1 000 USD**; AAA (50 / 40 @ 15); BBB (50 / 60 @ 10) | AAA **BUY 6,666666** (not 6,666667); BBB **SELL 10** |
| TC-11 | P1 | Exactly one share | **1 000 USD**; AAA (50 / 40 @ 100); BBB (50 / 60 @ 10) | AAA **BUY 1**; BBB **SELL 10** |
| TC-12 | P1 | Exactly the minimum fractional share quantity | **1 000 USD**; AAA (50 / 49,9999999 @ 1); BBB (50 / 50,0000001 @ 1) | AAA **BUY 0,000001**; BBB **SELL 0,000001** |
| TC-13 | P1 | Required quantity below minimum precision | **1 000 USD**; AAA (50 / 49,99999999 @ 1); BBB (50 / 50,00000001 @ 1) | Both **HOLD 0** after rounding down |
| TC-14 | P1 | Decimal unit prices | **1 000 USD**; AAA (50 / 40 @ 0,30); BBB (50 / 60 @ 0,80) | AAA **BUY 333,333333**; BBB **SELL 125** |
| TC-15 | P2 | Larger account value | **10 000 000 USD**; AAA (50 / 40 @ 25); BBB (50 / 60 @ 100) | AAA **BUY 40 000**; BBB **SELL 10 000** |
| TC-16 | P2 | Symbol normalization | **1 000 USD**; ` ibm ` (50 / 40 @ 10); `MsFt` (50 / 60 @ 25) | IBM **BUY 10**; MSFT **SELL 4**; symbols normalized, no extra securities |
| TC-17 | P2 | Securities entered in a different order | **1 000 USD**; BBB (50 / 60 @ 25) entered before AAA (50 / 40 @ 10) | BBB **SELL 4**; AAA **BUY 10**; quantities and directions are independent of input order |

## 3. Invalid-input tests

For each case, submit the stated input and check that **no recommendations are produced**. A clear validation error is expected. The exact text of the error is not specified by the assessment.

For cases that vary one field, use this valid starting portfolio unless noted otherwise: **1 000 USD**; AAA (100 / 100 @ 10).

| ID | Pri. | Scenario / test data | Expected result |
| --- | :---: | --- | --- |
| TC-18 | P0 | Total assets = **0 USD**; repeat with **−1 USD** | Both requests rejected: asset value must be positive |
| TC-19 | P0 | AAA price = **0 USD**; repeat with **−1 USD** | Both requests rejected: unit price must be positive |
| TC-20 | P0 | **1 000 USD**; AAA (40 / 50 @ 10); BBB (40 / 50 @ 10); targets total **80%** | Rejected: target allocations do not total 100% |
| TC-21 | P0 | **1 000 USD**; AAA (50 / 40 @ 10); BBB (50 / 40 @ 10); current allocations total **80%** | Rejected: current allocations do not total 100% |
| TC-22 | P1 | With the valid single-security baseline, replace **target** in separate runs with **−1%** and **101%**; repeat for **current** | Each request rejected: allocation percentage outside 0–100% |
| TC-23 | P1 | **1 000 USD**; IBM (50 / 50 @ 10); `ibm` (50 / 50 @ 20) | Rejected: duplicate security symbol (case-insensitive) |
| TC-24 | P1 | With the valid baseline, replace AAA symbol with an empty string, whitespace-only text, and a missing symbol (separate runs) | Each request rejected: symbol is required |
| TC-25 | P1 | Run separately with: **(a)** no securities; **(b)** missing total asset value; **(c)** missing target %, current % or price for the single-security baseline | Each request rejected: required inputs must be present |

## 4. Business risks to raise during review

These are **not marked as failing test cases**, because the exercise does not define the required behaviour. They should be clarified before testing a real order-execution application.

| Topic | Example / question | Why it matters |
| --- | --- | --- |
| **Funding purchases** | 1 000 USD portfolio: AAA (50 / 40 @ 10) produces BUY 10, while BBB (50 / 60 @ 200) produces HOLD 0. Where does the 100 USD for the purchase come from? | The current calculator generates independent recommendations; it does not enforce a cash budget. |
| **Exact zero variance** | Six-decimal rounding in TC-01 leaves a tiny residual deviation. Is a tolerance allowed, or must the system reach the mathematically exact target? | The phrase “zero target variance” is ambiguous with limited trading precision. |
| **Actual holdings and execution** | Are fractional shares always supported? Are existing holdings and available cash known? Do fees, price movements, and order rejections matter? | Recommendations are not the same as orders that can actually be executed. |
