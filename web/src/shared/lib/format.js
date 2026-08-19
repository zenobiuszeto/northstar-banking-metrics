export const moneyMillions = (value) => `$${value}M`;

export function relativeFreshness(timestamp, now = Date.now()) {
  const elapsedMinutes = Math.max(0, Math.floor((now - new Date(timestamp).getTime()) / 60_000));
  if (!Number.isFinite(elapsedMinutes) || elapsedMinutes < 1) return 'Updated just now';
  return `Updated ${elapsedMinutes} min ago`;
}
