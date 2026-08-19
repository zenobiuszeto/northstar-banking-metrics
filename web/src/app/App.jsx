import {useState} from 'react';
import {Dashboard} from '../features/metrics/components/Dashboard';
import {MetricsState} from '../features/metrics/components/MetricsState';
import {useMetrics} from '../features/metrics/hooks/useMetrics';
import {PRODUCTS} from '../features/metrics/model/dashboardViews';
import {AppHeader} from '../shared/components/AppHeader';
import {ProductNav} from '../shared/components/ProductNav';

export function App() {
  const [product, setProduct] = useState(PRODUCTS[0]);
  const {metrics, status, error, reload} = useMetrics(product);

  return (
    <>
      <AppHeader />
      <main>
        <ProductNav products={PRODUCTS} selected={product} onSelect={setProduct} />
        {status === 'success' && metrics
          ? <Dashboard product={product} metrics={metrics} />
          : <MetricsState status={status} error={error} onRetry={reload} />}
      </main>
    </>
  );
}
