export function Score({label, value, note, status = 'good'}) {
  return <div className="score"><span>{label}</span><b>{value}</b><small className={status}>{note}</small></div>;
}
