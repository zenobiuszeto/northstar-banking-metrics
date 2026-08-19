export function MetricRow({label, value, width, tone = ''}) {
  return <div className="row"><span>{label}</span><div><i className={tone} style={{width}} /></div><b>{value}</b></div>;
}
