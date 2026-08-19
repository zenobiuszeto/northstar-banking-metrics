export function MetricsState({status, error, onRetry}) {
  if (status === 'loading') return <section className="state" aria-live="polite"><span className="spinner" />Loading portfolio…</section>;
  if (status === 'empty') return <section className="state"><b>No metrics available</b><p>Try another product or refresh later.</p></section>;
  return <section className="state" role="alert"><b>Portfolio data is unavailable</b>
    <p>{error?.message ?? 'The metrics service could not be reached.'}</p>
    <button className="retry" type="button" onClick={onRetry}>Try again</button></section>;
}
