import type {
  ButtonHTMLAttributes,
  CSSProperties,
  HTMLAttributes,
  InputHTMLAttributes,
  ReactNode,
} from 'react';
import { useEffect, useRef } from 'react';

import './design-system.css';

type ButtonVariant = 'primary' | 'secondary' | 'quiet' | 'danger';

export function Button({
  variant = 'primary',
  className = '',
  ...props
}: ButtonHTMLAttributes<HTMLButtonElement> & { variant?: ButtonVariant }) {
  return (
    <button className={`ds-button ds-button--${variant} ${className}`.trim()} {...props} />
  );
}

export function Card({
  className = '',
  ...props
}: HTMLAttributes<HTMLElement>) {
  return <section className={`ds-card ${className}`.trim()} {...props} />;
}

export function TextField({
  id,
  label,
  error,
  ...props
}: InputHTMLAttributes<HTMLInputElement> & { label: string; error?: string }) {
  const fieldId = id ?? `field-${label.toLowerCase().replace(/[^a-z0-9]+/g, '-')}`;
  const errorId = `${fieldId}-error`;

  return (
    <div className="ds-field">
      <label htmlFor={fieldId}>{label}</label>
      <input
        id={fieldId}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? errorId : undefined}
        {...props}
      />
      {error && <span className="ds-field__error" id={errorId}>{error}</span>}
    </div>
  );
}

export function DataTable({
  caption,
  headers,
  rows,
}: {
  caption: string;
  headers: string[];
  rows: ReactNode[][];
}) {
  return (
    <div className="ds-table-wrap">
      <table className="ds-table">
        <caption>{caption}</caption>
        <thead>
          <tr>{headers.map((header) => <th key={header} scope="col">{header}</th>)}</tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={index}>{row.map((cell, cellIndex) => <td key={cellIndex}>{cell}</td>)}</tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function Modal({
  title,
  children,
  onClose,
}: {
  title: string;
  children: ReactNode;
  onClose: () => void;
}) {
  const dialogRef = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    const dialog = dialogRef.current;
    dialog?.showModal();
    return () => dialog?.close();
  }, []);

  return (
    <dialog
      className="ds-modal"
      ref={dialogRef}
      aria-labelledby="ds-modal-title"
      onCancel={(event) => {
        event.preventDefault();
        onClose();
      }}
    >
      <div className="ds-modal__header">
        <h2 id="ds-modal-title">{title}</h2>
        <Button variant="quiet" onClick={onClose} aria-label="Close dialog">×</Button>
      </div>
      <div className="ds-modal__body">{children}</div>
    </dialog>
  );
}

export function Toast({ children }: { children: ReactNode }) {
  return <div className="ds-toast" role="status">{children}</div>;
}

export function Skeleton({
  width = '100%',
  className = '',
}: {
  width?: string;
  className?: string;
}) {
  return (
    <span
      className={`ds-skeleton ${className}`.trim()}
      style={{ width }}
      aria-hidden="true"
    />
  );
}

export function EmptyState({
  title,
  children,
}: {
  title: string;
  children: ReactNode;
}) {
  return (
    <div className="ds-state" role="status">
      <h3>{title}</h3>
      <p>{children}</p>
    </div>
  );
}

export function LoadingState({ label = 'Loading' }: { label?: string }) {
  return (
    <div className="ds-state" role="status" aria-live="polite">
      <span className="ds-spinner" aria-hidden="true" />
      <p>{label}</p>
    </div>
  );
}

export function ErrorState({
  message,
  onRetry,
}: {
  message: string;
  onRetry?: () => void;
}) {
  return (
    <div className="ds-state ds-state--error" role="alert">
      <p>{message}</p>
      {onRetry && <Button variant="secondary" onClick={onRetry}>Try again</Button>}
    </div>
  );
}

export function ProgressRing({
  value,
  label,
}: {
  value: number;
  label: string;
}) {
  const boundedValue = Math.max(0, Math.min(100, value));

  return (
    <div
      className="ds-progress-ring"
      style={{ '--ring-value': `${boundedValue}%` } as CSSProperties}
      role="img"
      aria-label={`${label}: ${boundedValue}%`}
    >
      <span>{boundedValue}%</span>
    </div>
  );
}

export function RoundTimeline({
  rounds,
}: {
  rounds: { name: string; status: 'complete' | 'current' | 'upcoming' }[];
}) {
  return (
    <ol className="ds-round-timeline">
      {rounds.map((round) => (
        <li className={`ds-round-timeline__item is-${round.status}`} key={round.name}>
          <span className="ds-round-timeline__marker" aria-hidden="true" />
          <span>{round.name}</span>
          <span className="ds-round-timeline__status">{round.status}</span>
        </li>
      ))}
    </ol>
  );
}

export function FunnelChart({
  steps,
}: {
  steps: { label: string; value: number }[];
}) {
  const maxValue = Math.max(1, ...steps.map((step) => step.value));

  return (
    <ol className="ds-funnel">
      {steps.map((step) => (
        <li className="ds-funnel__step" key={step.label}>
          <span>{step.label}</span>
          <span
            className="ds-funnel__bar"
            style={{ '--funnel-width': `${Math.max(4, (step.value / maxValue) * 100)}%` } as CSSProperties}
          />
          <strong>{step.value}</strong>
        </li>
      ))}
    </ol>
  );
}

export function SectionTitle({
  eyebrow,
  title,
  children,
}: {
  eyebrow?: string;
  title: string;
  children?: ReactNode;
}) {
  return (
    <div className="ds-section-title">
      {eyebrow && <p>{eyebrow}</p>}
      <h2>{title}</h2>
      {children}
    </div>
  );
}
