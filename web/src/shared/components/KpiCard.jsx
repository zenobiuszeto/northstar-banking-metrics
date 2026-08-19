export function KpiCard({label, value, delta}) {
  return <article><small>{label}</small><strong>{value}</strong><em>{delta} vs prior period</em></article>;
}
