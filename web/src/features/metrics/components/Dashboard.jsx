import {BarChart} from '../../../shared/components/BarChart';
import {KpiCard} from '../../../shared/components/KpiCard';
import {MetricRow} from '../../../shared/components/MetricRow';
import {Score} from '../../../shared/components/Score';
import {relativeFreshness} from '../../../shared/lib/format';
import {dashboardView} from '../model/dashboardViews';

const executiveScores = [
  ['Return on assets', '1.41%', 'Above plan'], ['Cost-to-income', '48.6%', 'Improving'],
  ['Customer NPS', '+47', 'Top quartile'], ['At-risk deposits', '$9.6M', 'Needs attention', 'watch'],
];
const leadershipScores = [
  ['Digital acquisition', '62.8%', '↑ 4.2 pts'], ['Marketing CAC', '$84', '↓ 6.7%'],
  ['Deposit beta', '44.1%', 'Within guardrail'], ['Promo conversion', '14.8%', '↑ 1.9 pts'],
];
const technologyScores = [
  ['API availability', '99.98%', 'Healthy'], ['P95 API latency', '184 ms', 'Within SLO'],
  ['Data freshness', '2 min', 'Current'], ['Pipeline success', '99.6%', '1 retry', 'watch'],
];

function ScoreStrip({scores, className = ''}) {
  return <section className={`scoreStrip ${className}`}>{scores.map(([label, value, note, status]) =>
    <Score key={label} label={label} value={value} note={note} status={status} />)}</section>;
}

function Rows({rows}) {
  return rows.map(([label, value, width], index) =>
    <MetricRow key={label} label={label} value={value} width={width}
      tone={index === 3 ? 'light' : index === 2 ? 'gray' : ''} />);
}

export function Dashboard({product, metrics}) {
  const view = dashboardView(product, metrics);
  const [trendTitle, trendSubtitle, trendValues, comparison, labels] = view.trend;
  const [sideTitle, sideSubtitle, sideRows] = view.side;
  const [funnelTitle, funnelRows] = view.funnel;
  const isExecutive = product === 'All products';

  return <div className="dashboard">
    <div className="heading"><div><h1>{view.title}</h1><p>{view.subtitle}</p></div>
      <span className="fresh">{relativeFreshness(metrics.generatedAt)}</span></div>
    <section className="cards">{view.cards.map(([label, value, delta]) =>
      <KpiCard key={label} label={label} value={value} delta={delta} />)}</section>
    {isExecutive && <ScoreStrip scores={executiveScores} />}
    <section className="grid">
      <article className="panel wide"><h2>{trendTitle}</h2><p>{trendSubtitle}</p>
        <div className="legendLine"><span>■ Actual</span><span>■ Plan / prior</span></div>
        <BarChart values={trendValues} comparison={comparison} labels={labels} /></article>
      <article className="panel"><h2>{sideTitle}</h2><p>{sideSubtitle}</p><Rows rows={sideRows} /></article>
    </section>
    <section className="grid">
      <article className="panel"><h2>{funnelTitle}</h2><p>Conversion and activation through the selected journey</p><Rows rows={funnelRows} /></article>
      <article className="panel"><h2>Retention & risk</h2><p>Early-warning customer and balance indicators</p>
        <MetricRow label="Monthly churn" value={`${metrics.monthlyChurnRate}%`} width="19%" tone="red" />
        <MetricRow label="At-risk customers" value={metrics.atRiskCustomers.toLocaleString()} width="31%" />
        <MetricRow label="Balances at risk" value={`$${metrics.depositsAtRiskMillions}M`} width="28%" />
        <MetricRow label="Win-back rate" value={`${metrics.winBackRate}%`} width="42%" tone="light" /></article>
    </section>
    {isExecutive && <><h3>Leadership operating scorecard</h3><ScoreStrip scores={leadershipScores} className="tech" />
      <h3>Technology & data health</h3><ScoreStrip scores={technologyScores} className="tech" /></>}
  </div>;
}
