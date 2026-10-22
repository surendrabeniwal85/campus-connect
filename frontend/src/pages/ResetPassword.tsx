import type { Portal } from '../auth/authApi';
import BrandHeader from '../auth/BrandHeader';

interface ResetPasswordProps {
  readonly portal: Portal;
  readonly onBackToLogin: () => void;
}

export default function ResetPassword({ portal, onBackToLogin }: ResetPasswordProps) {
  const portalName = portal === 'INSTITUTE' ? 'Institute' : 'Placement';

  return (
    <main className="auth-page pa-foundation">
      <section className="auth-card pa-card" aria-labelledby="reset-password-title">
        <BrandHeader />
        <h1 id="reset-password-title">Reset your password</h1>
        <p className="auth-description">
          {portalName} password reset is not available yet. The reset service and
          secure token-delivery flow are not connected to this application.
        </p>
        <p className="auth-note" role="status">
          No password reset was requested or performed. Return to sign in, or
          contact your institution or placement office for account help.
        </p>
        <button className="auth-submit" type="button" onClick={onBackToLogin}>
          Back to {portalName} sign in
        </button>
      </section>
    </main>
  );
}
