import {describe, expect, it} from 'vitest';
import {dashboardView, PRODUCTS} from './dashboardViews';

const metrics = {applications: 4218, approvalRate: 74.2, depositsMillions: 184.6, fraudRate: 0.31,
  monthlyChurnRate: 1.1, retentionRate: 91.2};

describe('dashboard views', () => {
  it('defines a view for every selectable product', () => {
    for (const product of PRODUCTS) expect(dashboardView(product, metrics).cards).toHaveLength(4);
  });
  it('derives approved applications from API values', () => {
    expect(dashboardView('Business Checking', metrics).funnel[1][1][1]).toBe('3,130');
  });
});
