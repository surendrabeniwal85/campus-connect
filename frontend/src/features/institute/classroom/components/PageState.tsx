

export function PageState({ loading, error, empty, onRetry }: {
  loading?: boolean; error?: string; empty?: boolean; onRetry?: () => void;
}) {
  if (loading) return <p role="status" className="classroom-state">Loading…</p>;
  if (error) return <div role="alert" className="classroom-state">
    <p>{error}</p>{onRetry && <button onClick={onRetry}>Retry</button>}
  </div>;
  if (empty) return <p className="classroom-state">No records found.</p>;
  return null;
}
