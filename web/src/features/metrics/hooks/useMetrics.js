import {useCallback, useEffect, useState} from 'react';
import {fetchMetrics} from '../api/metricsApi';

export function useMetrics(product) {
  const [requestVersion, setRequestVersion] = useState(0);
  const [state, setState] = useState({status: 'loading', metrics: null, error: null});
  useEffect(() => {
    const controller = new AbortController();
    setState({status: 'loading', metrics: null, error: null});
    fetchMetrics(product, {signal: controller.signal})
      .then((metrics) => setState({status: metrics ? 'success' : 'empty', metrics, error: null}))
      .catch((error) => {
        if (error.name !== 'AbortError') setState({status: 'error', metrics: null, error});
      });
    return () => controller.abort();
  }, [product, requestVersion]);
  const reload = useCallback(() => setRequestVersion((version) => version + 1), []);
  return {...state, reload};
}
