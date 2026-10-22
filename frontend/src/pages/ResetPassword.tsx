import { useState, type FormEvent } from 'react';
import type { Portal } from '../auth/authApi';
import BrandHeader from '../auth/BrandHeader';
import { forgotPassword, resetPassword } from '../auth/authApi';

interface ResetPasswordProps {
  readonly portal: Portal;
  readonly onBackToLogin: () => void;
}

export default function ResetPassword({ portal, onBackToLogin }: ResetPasswordProps) {
  const portalName = portal === 'INSTITUTE' ? 'Institute' : 'Placement';

  const [step, setStep] = useState<'REQUEST' | 'RESET' | 'DONE'>('REQUEST');
  const [email, setEmail] = useState('');
  const [token, setToken] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const [infoMessage, setInfoMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleRequestToken(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErrorMessage('');
    setInfoMessage('');
    setIsSubmitting(true);

    try {
      const response = await forgotPassword(email.trim());
      if (response.resetToken) {
        setToken(response.resetToken);
        setInfoMessage(`Reset token generated: ${response.resetToken}. (Provided directly in demo mode).`);
      } else {
        setInfoMessage('If an account with this email exists, a reset code was generated.');
      }
      setStep('RESET');
    } catch (cause) {
      setErrorMessage(cause instanceof Error ? cause.message : 'Unable to request password reset.');
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleResetPassword(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErrorMessage('');
    setIsSubmitting(true);

    if (newPassword.length < 8) {
      setErrorMessage('New password must be at least 8 characters long.');
      setIsSubmitting(false);
      return;
    }

    if (newPassword !== confirmPassword) {
      setErrorMessage('Passwords do not match.');
      setIsSubmitting(false);
      return;
    }

    try {
      await resetPassword(token.trim(), newPassword);
      setStep('DONE');
    } catch (cause) {
      setErrorMessage(cause instanceof Error ? cause.message : 'Unable to reset password. Please check your token.');
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="auth-page pa-foundation">
      <section className="auth-card pa-card" aria-labelledby="reset-password-title">
        <BrandHeader />
        <h1 id="reset-password-title">Reset your password</h1>
        <p className="auth-description">
          {portalName} Portal account password recovery service.
        </p>

        {errorMessage && <p className="auth-error" role="alert">{errorMessage}</p>}
        {infoMessage && <p className="auth-info" role="status">{infoMessage}</p>}

        {step === 'REQUEST' && (
          <form onSubmit={handleRequestToken} aria-busy={isSubmitting}>
            <label htmlFor="reset-email">Enter your account email</label>
            <input
              id="reset-email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder={portal === 'INSTITUTE' ? 'e.g. student@institute.ac.in' : 'e.g. tpo@placement.ac.in'}
              required
              disabled={isSubmitting}
            />
            <button className="auth-submit" type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Requesting reset token…' : 'Generate Reset Token'}
            </button>
            <button
              className="auth-link"
              type="button"
              onClick={() => setStep('RESET')}
              style={{ marginTop: '10px' }}
            >
              Already have a reset token? Enter it here
            </button>
          </form>
        )}

        {step === 'RESET' && (
          <form onSubmit={handleResetPassword} aria-busy={isSubmitting}>
            <label htmlFor="reset-token-input">Reset Verification Token</label>
            <input
              id="reset-token-input"
              type="text"
              value={token}
              onChange={(e) => setToken(e.target.value)}
              placeholder="e.g. A1B2C3D4"
              required
              disabled={isSubmitting}
            />

            <label htmlFor="reset-new-password">New Password (min 8 chars)</label>
            <div className="password-input-wrap">
              <input
                id="reset-new-password"
                type={showPassword ? 'text' : 'password'}
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="••••••••••••"
                required
                disabled={isSubmitting}
              />
              <button
                type="button"
                className="password-toggle-btn"
                onClick={() => setShowPassword(!showPassword)}
                aria-label={showPassword ? 'Hide password' : 'Show password'}
              >
                {showPassword ? 'Hide' : 'Show'}
              </button>
            </div>

            <label htmlFor="reset-confirm-password">Confirm New Password</label>
            <input
              id="reset-confirm-password"
              type={showPassword ? 'text' : 'password'}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="••••••••••••"
              required
              disabled={isSubmitting}
            />

            <button className="auth-submit" type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Updating password…' : 'Set New Password'}
            </button>

            <button
              className="auth-link"
              type="button"
              onClick={() => setStep('REQUEST')}
              style={{ marginTop: '10px' }}
            >
              ← Back to request token
            </button>
          </form>
        )}

        {step === 'DONE' && (
          <div>
            <p className="auth-success" role="status">
              ✓ Your password has been successfully updated! You can now sign in with your new password.
            </p>
            <button className="auth-submit" type="button" onClick={onBackToLogin} style={{ marginTop: '16px' }}>
              Sign in with new password
            </button>
          </div>
        )}

        <button className="auth-link" type="button" onClick={onBackToLogin} disabled={isSubmitting} style={{ marginTop: '16px' }}>
          Back to {portalName} sign in
        </button>
      </section>
    </main>
  );
}
