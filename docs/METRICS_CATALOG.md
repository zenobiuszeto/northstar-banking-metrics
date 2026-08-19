# Banking Metrics Catalog

This catalog defines the dashboard metrics, why they matter, and which teams use them. Values in the demo are synthetic; a company deployment should map every metric to governed source fields and an approved reporting calendar.

## Audience map

| Audience | Primary questions | Dashboard areas |
|---|---|---|
| CEO and executive leadership | Are deposits, profitability, customers, and risk moving in the right direction? | Executive portfolio overview, leadership scorecard |
| Business and portfolio leaders | Which products and customer relationships are growing or weakening? | Product mix, growth funnel, retention and risk |
| Marketing and growth | Which acquisition channels create funded, durable relationships at an acceptable cost? | Applications, funded accounts, 90-day activation, CAC, digital acquisition |
| Pricing and treasury | Are paid rates, deposit beta, margin, and promotions producing profitable growth? | Net interest margin, deposit beta, rate sensitivity, promo conversion |
| Fraud and risk | Where are losses, suspicious activity, or balances at risk increasing? | Fraud loss rate, payment fraud, at-risk customers and deposits |
| CIO, CTO, and technology leaders | Is the platform available, current, and within service objectives? | API availability, latency, data freshness, pipeline success |
| Product managers | How is each account type used, activated, and retained? | Checking engagement, savings behavior, product funnels |

## Enterprise portfolio overview

| Metric | Definition and formula | Primary audience | Suggested cadence |
|---|---|---|---|
| Total deposits | Sum of end-of-period ledger balances for open deposit accounts. Exclude closed, charged-off, and internal settlement accounts according to finance policy. | Executive, finance, treasury | Daily with month-end certification |
| Net new money | External deposits minus external withdrawals during the period. Exclude transfers between the bank's own products to avoid double counting. | Executive, business, treasury | Daily and monthly |
| Net interest margin | `(interest income - interest expense) / average interest-earning assets`, annualized. | Executive, finance, pricing | Monthly |
| Fraud loss rate | Confirmed fraud losses divided by eligible transaction value. Display consistently as percent or basis points. | Executive, fraud, risk | Daily and monthly |
| Return on assets | Annualized net income divided by average total assets. | CEO, CFO, board | Monthly and quarterly |
| Cost-to-income | Operating expense divided by operating income. Lower is generally better, subject to growth investment. | CEO, CFO, operations | Monthly |
| Customer NPS | Percent of promoters minus percent of detractors using the approved survey methodology. | Executive, product, service | Monthly or quarterly |
| At-risk deposits | Current balances belonging to customers above the approved attrition-risk threshold. | Executive, business, retention | Daily |
| Product deposit mix | Each product's deposit balance divided by total deposit balances. | Executive, treasury, portfolio | Daily |

## Acquisition and relationship metrics

| Metric | Definition and formula | Notes |
|---|---|---|
| Applications | Distinct submitted applications in the selected period. Reopened or duplicate records need an agreed deduplication rule. | Funnel entry point |
| Approval rate | Approved applications divided by decisioned applications. Track policy declines separately from incomplete applications. | Credit/risk policy context is required |
| Funding rate | Accounts with a qualifying first deposit divided by approved applications. | Define the minimum qualifying deposit |
| 90-day active rate | Funded accounts meeting product-specific activity rules at day 90 divided by funded accounts. | Checking and savings use different activity rules |
| Digital acquisition | Accounts opened through digital channels divided by all opened accounts. | Marketing and channel strategy |
| Marketing CAC | Attributable acquisition spend divided by newly funded customers. Report blended and channel-specific CAC. | Compare with lifetime value |
| Promo conversion | Customers who accept and fund a promoted offer divided by eligible customers reached. | Include incremental lift and promotion cost |
| Customer lifetime value | Expected risk- and cost-adjusted relationship contribution over the modeled lifetime. | Model assumptions must be versioned |

## Checking-specific metrics

Checking products emphasize everyday usage and primary financial relationships.

| Metric | Definition |
|---|---|
| Operating deposits | End-of-period checking balances, separated into business and consumer portfolios. |
| Primary-bank rate | Checking customers meeting the approved primary-relationship rule, such as direct deposit plus recurring payments, divided by active checking customers. |
| Direct-deposit penetration | Active customers with a qualifying recurring direct deposit divided by active checking customers. |
| Debit-active rate | Customers with at least one qualifying debit-card transaction in the activity window divided by open card-enabled checking customers. |
| Payment activity | Count and value of ACH, wire, debit, check, and real-time payment transactions by period. |
| Treasury attach | Business checking relationships using at least one treasury-management service divided by active business checking relationships. |
| Payroll linked | Business relationships using payroll services or recurring payroll-originated ACH divided by eligible businesses. |
| Overdraft-free rate | Active consumer checking accounts with no overdraft event in the period divided by active consumer checking accounts. |
| Payment fraud rate | Confirmed fraudulent checking-payment value divided by eligible checking-payment value. |

## Savings-specific metrics

Savings products emphasize balance growth, liquidity, pricing response, and balance durability.

| Metric | Definition |
|---|---|
| Savings deposits | End-of-period savings balances by business or consumer portfolio. |
| Net inflows | External savings deposits minus external savings withdrawals. Exclude internal product transfers for enterprise net-new-money reporting. |
| Average rate paid | Interest expense divided by average interest-bearing deposit balance, annualized. |
| Deposit beta | Change in the portfolio rate paid divided by the change in the relevant market or policy rate over the same cycle. |
| Rate-sensitive share | Balance or customer share whose observed/modelled attrition response exceeds the approved threshold after a rate gap. |
| Recurring saver rate | Consumer savings customers with a qualifying recurring contribution divided by active consumer savings customers. |
| Goal-funded rate | Customers meeting their declared savings goal or scheduled funding threshold divided by customers with an active goal. |
| Operating-reserve rate | Business savings relationships holding the defined minimum months of operating expenses. |
| 90-day retention | Accounts still open and above the minimum balance at day 90 divided by funded accounts. |

## Retention and risk metrics

| Metric | Definition |
|---|---|
| Monthly churn | Customers whose final eligible deposit relationship closed during the month divided by eligible customers at the start of the month. Product closure and full-relationship churn should be reported separately. |
| At-risk customers | Customers whose approved churn model score exceeds the intervention threshold. |
| Balances at risk | Current eligible balances held by at-risk customers. |
| Win-back rate | Churned or at-risk customers restored to the approved active-state definition after treatment divided by treated customers. |
| 12-month retention | Customers remaining active after 12 months divided by the original eligible cohort. Use cohort-based reporting. |

## Technology and data health

| Metric | Definition |
|---|---|
| API availability | Successful eligible API minutes divided by total eligible minutes, measured from the customer or consumer perspective. |
| P95 API latency | 95th percentile end-to-end response time for the selected API population. |
| Data freshness | Current time minus the latest successfully processed business event timestamp. |
| Pipeline success | Successful scheduled/streaming data jobs divided by attempted jobs, with retries reported separately. |

## Governance requirements

- Assign a business owner, technical owner, authoritative source, refresh SLA, and certification status to every production metric.
- Version formulas and effective dates. Historical reports must retain the formula version used at publication time.
- Separate operational estimates from finance-certified figures and label both clearly.
- Apply customer-consent, retention, encryption, masking, and access-control policies before loading regulated or personally identifiable data.
- Reconcile dashboard aggregates to core banking and general-ledger controls before production release.
