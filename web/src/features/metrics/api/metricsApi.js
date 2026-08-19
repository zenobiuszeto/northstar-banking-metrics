const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

export async function fetchMetrics(product, {signal} = {}) {
  const response = await fetch(`${API_BASE_URL}/api/metrics?product=${encodeURIComponent(product)}`, {
    headers: {Accept: 'application/json'}, signal,
  });
  if (!response.ok) {
    const problem = await response.json().catch(() => null);
    throw new Error(problem?.detail ?? `Metrics request failed (${response.status})`);
  }
  const metrics = await response.json();
  if (!metrics || metrics.product !== product) throw new Error('The metrics service returned an unexpected response.');
  return metrics;
}
