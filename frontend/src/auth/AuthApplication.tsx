import { useEffect, useState } from 'react';
import { resolveRegisteredView, type RegisteredView } from '../app/routes';
import InstituteLogin from '../pages/InstituteLogin';
import PlacementLogin from '../pages/PlacementLogin';
import RegisterStudent from '../pages/RegisterStudent';
import ResetPassword from '../pages/ResetPassword';
import PortalSelection from './PortalSelection';
import PortalDashboard from './PortalDashboard';
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
    window.scrollTo({ top: 0, behavior: 'smooth' });
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
      setPortal(null);
      navigate('/');
    }, session.expiresIn * 1000);
    return () => window.clearTimeout(timeout);
  }, [session]);

  const view: RegisteredView | null = resolveRegisteredView(pathname);

  // Authenticated State -> Render Full Interactive Portal Dashboard
  if (session) {
    if (view === 'AUTH_SESSION' || view === 'PORTAL_DASHBOARD' || pathname.startsWith('/dashboard') || pathname.startsWith('/portal')) {
      return (
        <PortalDashboard
          session={session}
          portal={portal ?? 'INSTITUTE'}
          onSignOut={() => {
            setSession(null);
            setPortal(null);
            navigate('/');
          }}
        />
      );
    }
  }

  // Unauthenticated Route Handlers
  if (!session && view === 'PORTAL_SELECTION') {
    return (
      <PortalSelection
        onChoosePortal={navigate}
        onClaimAccount={() => navigate('/institute/register')}
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
        onRegister={() => navigate('/institute/register')}
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

  if (!session && view === 'INSTITUTE_REGISTER') {
    return (
      <RegisterStudent
        onBackToLogin={() => navigate('/institute/login')}
        onRegistered={() => navigate('/institute/login')}
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

  // Active Session Default fallback or 404 / Unauthorized Handlers
  if (session) {
    return (
      <PortalDashboard
        session={session}
        portal={portal ?? 'INSTITUTE'}
        onSignOut={() => {
          setSession(null);
          setPortal(null);
          navigate('/');
        }}
      />
    );
  }

  return (
    <main className="auth-page pa-foundation">
      <section className="auth-card pa-card pa-foundation" aria-labelledby="portal-title">
        <p className="auth-eyebrow">PLACEMENT ASSISTANT</p>
        <p className="auth-tagline">Powered by AI</p>
        <h1 id="portal-title">Page not found</h1>
        <p className="auth-description">
          The requested page route (<code>{pathname}</code>) is not registered in the application registry.
        </p>
        <button className="auth-submit" type="button" onClick={() => navigate('/')}>
          Return to Portal Selection
        </button>
      </section>
    </main>
  );
}
