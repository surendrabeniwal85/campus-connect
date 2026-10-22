import { useEffect, useState } from 'react';
import { resolveRegisteredView, type RegisteredView } from '../app/routes';
import InstituteLogin from '../pages/InstituteLogin';
import PlacementLogin from '../pages/PlacementLogin';
import ResetPassword from '../pages/ResetPassword';
import PortalSelection from './PortalSelection';
import type { AuthenticatedSession, Portal } from './authApi';
import './auth.css';

function currentPath(): string {
  return window.location.pathname;
}

export default function AuthApplication() {
  const [pathname, setPathname] = useState(currentPath);
  const [session, setSession] = useState<AuthenticatedSession | null>(null);
  const [portal, setPortal] = useState<Portal | null>(null);

  function navigate(path: string) {
    window.history.pushState({}, '', path);
    setPathname(path);
  }

  useEffect(() => {
    const onPopState = () => setPathname(currentPath());
    window.addEventListener('popstate', onPopState);
    return () => window.removeEventListener('popstate', onPopState);
  }, []);

  useEffect(() => {
    if (!session) return undefined;
    const timeout = window.setTimeout(() => {
      setSession(null);
      navigate('/');
    }, session.expiresIn * 1000);
    return () => window.clearTimeout(timeout);
  }, [session]);

  const view: RegisteredView | null = resolveRegisteredView(pathname);

  if (session && view === 'AUTH_SESSION') {
    return (
      <main className="auth-page pa-foundation">
        <section className="auth-card pa-card auth-session" aria-labelledby="session-title">
          <p className="auth-eyebrow">PLACEMENT ASSISTANT</p>
          <p className="auth-tagline">Powered by AI</p>
          <h1 id="session-title">You’re signed in</h1>
          <p className="auth-description">{session.email}</p>
          <div className="auth-session-details">
            <p><strong>Portal</strong><span>{portal === 'INSTITUTE' ? 'Institute' : 'Placement'}</span></p>
            <p><strong>Roles</strong><span>{session.roles.join(', ')}</span></p>
            <p><strong>Session</strong><span>Access token expires in {Math.floor(session.expiresIn / 60)} minutes</span></p>
          </div>
          <p className="auth-note">Authentication succeeded. Protected portal features are not connected yet.</p>
          <button className="auth-submit" type="button" onClick={() => {
            setSession(null);
            setPortal(null);
            navigate('/');
          }}>
            Sign out and return to portal selection
          </button>
        </section>
      </main>
    );
  }

  if (!session && view === 'PORTAL_SELECTION') {
    return (
      <PortalSelection
        onChoosePortal={navigate}
      />
    );
  }

  if (!session && view === 'INSTITUTE_LOGIN') {
    return (
      <InstituteLogin
        onAuthenticated={(authenticatedSession) => {
          setSession(authenticatedSession);
          setPortal('INSTITUTE');
          navigate('/auth/session');
        }}
        onChangePortal={() => navigate('/')}
        onResetPassword={() => navigate('/institute/reset-password')}
      />
    );
  }
  if (!session && view === 'PLACEMENT_LOGIN') {
    return (
      <PlacementLogin
        onAuthenticated={(authenticatedSession) => {
          setSession(authenticatedSession);
          setPortal('PLACEMENT');
          navigate('/auth/session');
        }}
        onChangePortal={() => navigate('/')}
        onResetPassword={() => navigate('/placement/reset-password')}
      />
    );
  }

  if (!session && view === 'INSTITUTE_RESET_PASSWORD') {
    return (
      <ResetPassword portal="INSTITUTE" onBackToLogin={() => navigate('/institute/login')} />
    );
  }
  if (!session && view === 'PLACEMENT_RESET_PASSWORD') {
    return (
      <ResetPassword portal="PLACEMENT" onBackToLogin={() => navigate('/placement/login')} />
    );
  }

  return (
    <main className="auth-page pa-foundation">
      <section className="auth-card pa-card pa-foundation" aria-labelledby="portal-title">
        <p className="auth-eyebrow">PLACEMENT ASSISTANT</p>
        <p className="auth-tagline">Powered by AI</p>
        <h1 id="portal-title">{session ? 'Session is active' : 'Page not found'}</h1>
        <p className="auth-description">
          {session
            ? 'Sign out to return to portal selection before starting another sign in.'
            : 'This page is not registered in the application module registry.'}
        </p>
        <button className="auth-submit" type="button" onClick={() => {
          setSession(null);
          setPortal(null);
          navigate('/');
        }}>
          {session ? 'Sign out and return to portal selection' : 'Go to portal selection'}
        </button>
      </section>
    </main>
  );
}
