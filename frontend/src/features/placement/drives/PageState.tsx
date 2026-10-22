import type { ReactNode } from "react";

interface Props {
  loading: boolean;
  error: string | null;
  empty: boolean;
  emptyText: string;
  children: ReactNode;
}

/** Shows loading, error or empty text, otherwise the content. */
export default function PageState({ loading, error, empty, emptyText, children }: Props) {
  if (loading) return <p className="drives-note">Loading...</p>;
  if (error) return <p className="drives-note drives-error" role="alert">{error}</p>;
  if (empty) return <p className="drives-note">{emptyText}</p>;
  return <>{children}</>;
}
