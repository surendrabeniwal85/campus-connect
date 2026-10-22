import { useState, type FormEvent } from 'react';
import BrandHeader from './BrandHeader';
import { login, type AuthenticatedSession, type Portal } from './authApi';

interface PortalLoginFormProps {
  readonly portal: Portal;
  readonly title: string;
  readonly description: string;
  readonly onAuthenticated: (session: AuthenticatedSession) => void;
  readonly onChangePortal: () => void;
  readonly onResetPassword: () => void;
}

export default function PortalLoginForm({
  portal,
  title,
  description,
  onAuthenticated,
  onChangePortal,
  onResetPassword,
}: PortalLoginFormProps) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    setIsSubmitting(true);
    try {
      const session = await login(portal, email.trim(), password);
      setPassword('');
      onAuthenticated(session);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Unable to sign in. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="auth-page pa-foundation">
      <section className="auth-card pa-card" data-portal={portal.toLowerCase()} aria-labelledby="login-title">
        <BrandHeader />
        <h1 id="login-title">{title}</h1>
        <p className="auth-description">{description}</p>
        <form onSubmit={submit} aria-busy={isSubmitting}>
          <label htmlFor={`${portal}-email`}>Email address</label>
          <input
            id={`${portal}-email`}
            type="email"
            autoComplete="username"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
            maxLength={190}
            disabled={isSubmitting}
          />
          <label htmlFor={`${portal}-password`}>Password</label>
          <input
            id={`${portal}-password`}
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
            disabled={isSubmitting}
          />
          {error && <p className="auth-error" role="alert" aria-live="polite">{error}</p>}
          <button className="auth-submit" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
        <button className="auth-link" type="button" onClick={onResetPassword} disabled={isSubmitting}>
          Forgot password?
        </button>
        <p className="auth-note">
          {portal === 'INSTITUTE'
            ? 'Student accounts are claimed only after an HOD adds the student record. Account claiming is not connected yet.'
            : 'Placement accounts are managed by Director HR. Account management is not connected yet.'}
        </p>
        <button className="auth-link" type="button" onClick={onChangePortal} disabled={isSubmitting}>
          Choose a different portal
        </button>
      </section>
    </main>
  );
}
