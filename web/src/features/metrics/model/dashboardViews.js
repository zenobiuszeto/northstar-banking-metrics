import {moneyMillions} from '../../../shared/lib/format';

export const PRODUCTS = ['All products', 'Business Checking', 'Consumer Checking', 'Business Savings', 'Consumer Savings'];

const PRODUCT_VIEWS = {
  'Business Checking': {
    title: 'Business checking performance', subtitle: 'Operating balances, payment activity, acquisition, and treasury engagement.',
    cards: (m) => [['Operating deposits', moneyMillions(m.depositsMillions), '↑ 6.8%'], ['Active businesses', '12,480', '↑ 4.1%'], ['Treasury attach', '38.6%', '↑ 2.7 pts'], ['Payment fraud', `${m.fraudRate}%`, '↓ 0.04 pts']],
    trend: ['Payment activity', 'ACH, wire, and debit transaction volume by week', [60, 74, 68, 88, 81, 104], [52, 61, 65, 72, 79, 86], ['W1', 'W2', 'W3', 'W4', 'W5', 'W6']],
    side: ['Business engagement', 'Products that deepen the primary relationship', [['Treasury services', '38.6%', '39%'], ['ACH active', '72.4%', '72%'], ['Card active', '61.8%', '62%'], ['Payroll linked', '43.2%', '43%']]],
    funnel: (m) => ['Business acquisition funnel', [['Applied', m.applications.toLocaleString(), '100%'], ['Approved', Math.round(m.applications * m.approvalRate / 100).toLocaleString(), '74%'], ['Funded', '3,121', '68%'], ['Active at 90d', '2,814', '61%']]],
  },
  'Consumer Checking': {
    title: 'Consumer checking performance', subtitle: 'Primary-bank behavior, card engagement, direct deposit, and attrition.',
    cards: (m) => [['Checking deposits', moneyMillions(m.depositsMillions), '↑ 3.9%'], ['Primary-bank rate', '64.1%', '↑ 1.8 pts'], ['Direct deposit', '71.6%', '↑ 2.4 pts'], ['Monthly churn', `${m.monthlyChurnRate}%`, '↓ 0.2 pts']],
    trend: ['Debit spend & transactions', 'Weekly card activity versus prior period', [64, 71, 78, 73, 94, 102], [58, 66, 69, 74, 81, 88], ['W1', 'W2', 'W3', 'W4', 'W5', 'W6']],
    side: ['Customer engagement', 'Behaviors associated with primary relationships', [['Direct deposit', '71.6%', '72%'], ['Debit active', '83.2%', '83%'], ['Digital active', '88.5%', '89%'], ['Overdraft-free', '94.7%', '95%']]],
    funnel: (m) => ['Consumer acquisition funnel', [['Applied', m.applications.toLocaleString(), '100%'], ['Approved', Math.round(m.applications * m.approvalRate / 100).toLocaleString(), '70%'], ['Funded', '4,522', '67%'], ['Active at 90d', '4,114', '61%']]],
  },
  'Business Savings': {
    title: 'Business savings & liquidity', subtitle: 'Liquidity reserves, yield sensitivity, retention, and relationship depth.',
    cards: (m) => [['Savings deposits', moneyMillions(m.depositsMillions), '↑ 7.6%'], ['Net new money', '$6.4M', '↑ 12.2%'], ['Avg rate paid', '3.72%', '↑ 8 bps'], ['12-mo retention', `${m.retentionRate}%`, '↑ 1.3 pts']],
    trend: ['Balance & net flow trend', 'Weekly ending balance versus growth plan', [63, 69, 77, 86, 91, 106], [58, 64, 70, 76, 83, 90], ['W1', 'W2', 'W3', 'W4', 'W5', 'W6']],
    side: ['Liquidity profile', 'Business savings behavior and pricing exposure', [['High balance', '44.8%', '45%'], ['Rate sensitive', '31.2%', '31%'], ['Operating reserve', '68.5%', '69%'], ['Relationship linked', '79.1%', '79%']]],
    funnel: () => ['Savings relationship funnel', [['Eligible businesses', '8,420', '100%'], ['Offer viewed', '5,980', '71%'], ['Account opened', '2,391', '28%'], ['90d retained', '2,236', '27%']]],
  },
  'Consumer Savings': {
    title: 'Consumer savings growth', subtitle: 'Net flows, recurring savings, rate response, goals, and retention.',
    cards: (m) => [['Savings deposits', moneyMillions(m.depositsMillions), '↑ 5.1%'], ['Net inflows', '$4.8M', '↑ 9.7%'], ['Recurring savers', '42.6%', '↑ 3.1 pts'], ['Monthly churn', `${m.monthlyChurnRate}%`, '↓ 0.3 pts']],
    trend: ['Savings balance growth', 'Weekly balances versus growth plan', [58, 66, 71, 79, 91, 99], [54, 60, 66, 72, 78, 84], ['W1', 'W2', 'W3', 'W4', 'W5', 'W6']],
    side: ['Savings behavior', 'Signals of durable household balances', [['Recurring save', '42.6%', '43%'], ['Goal funded', '36.8%', '37%'], ['Rate sensitive', '28.4%', '28%'], ['90d retained', '91.2%', '91%']]],
    funnel: () => ['Savings acquisition funnel', [['Offer reached', '18,400', '100%'], ['Started', '8,760', '48%'], ['Funded', '5,079', '28%'], ['90d retained', '4,632', '25%']]],
  },
};

export function dashboardView(product, metrics) {
  if (product !== 'All products') {
    const view = PRODUCT_VIEWS[product];
    if (!view) throw new Error(`Unsupported dashboard product: ${product}`);
    return {...view, cards: view.cards(metrics), funnel: view.funnel(metrics)};
  }
  return {
    title: 'Executive portfolio overview', subtitle: 'Enterprise view across deposits, growth, profitability, risk, customers, and platform health.',
    cards: [['Total deposits', moneyMillions(metrics.depositsMillions), '↑ 5.2%'], ['Net new money', '$24.8M', '↑ 8.7%'], ['Net interest margin', '3.12%', '↑ 6 bps'], ['Fraud loss rate', `${metrics.fraudRate}%`, '↓ 0.06 pts']],
    trend: ['Portfolio deposits vs plan', 'Monthly ending balances across all products', [58, 63, 71, 78, 86, 98], [55, 60, 66, 72, 79, 86], ['Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug']],
    side: ['Product deposit mix', 'Share of total relationship balances', [['Business checking', '38%', '38%'], ['Consumer checking', '26%', '26%'], ['Business savings', '18%', '18%'], ['Consumer savings', '18%', '18%']]],
    funnel: ['Enterprise growth funnel', [['Applications', metrics.applications.toLocaleString(), '100%'], ['Approved', '13,256', '72%'], ['Funded', '11,632', '63%'], ['Active at 90d', '10,984', '59%']]],
  };
}
