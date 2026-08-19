import {describe, expect, it} from 'vitest';
import {moneyMillions, relativeFreshness} from './format';

describe('format utilities', () => {
  it('formats portfolio values in millions', () => expect(moneyMillions(486.2)).toBe('$486.2M'));
  it('formats response freshness', () => {
    expect(relativeFreshness('2026-08-19T12:00:00Z', Date.parse('2026-08-19T12:02:30Z'))).toBe('Updated 2 min ago');
  });
});
