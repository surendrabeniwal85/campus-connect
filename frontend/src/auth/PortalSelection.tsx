import BrandHeader from './BrandHeader';

interface PortalSelectionProps {
  readonly onChoosePortal: (portalPath: '/institute/login' | '/placement/login') => void;
}

export default function PortalSelection({ onChoosePortal }: PortalSelectionProps) {
  return (
    <main className="auth-page pa-foundation portal-selection">
      <section className="portal-selection-content" aria-labelledby="portal-selection-title">
        <header className="portal-selection-header">
          <BrandHeader />
        </header>
        <h1 id="portal-selection-title">Welcome to Placement Assistant</h1>
        <p className="auth-description">
          Choose the portal associated with your account to continue.
        </p>
        <div className="portal-choice-grid">
          <article className="portal-choice pa-card pa-portal-institute">
            <p className="auth-eyebrow">INSTITUTE</p>
            <h2>Institute Portal</h2>
            <p>For students, teachers, and HODs.</p>
            <button
              className="auth-submit"
              type="button"
              onClick={() => onChoosePortal('/institute/login')}
            >
              Sign in to Institute
            </button>
            <p className="portal-account-note">
              Student accounts can be claimed after an HOD adds the student record.
              Claiming is not connected yet. Staff accounts are managed by the HOD.
            </p>
          </article>
          <article className="portal-choice pa-card pa-portal-placement">
            <p className="auth-eyebrow">PLACEMENT</p>
            <h2>Placement Portal</h2>
            <p>For Director HR, TPO, D.TPO, and placement coordinators.</p>
            <button
              className="auth-submit"
              type="button"
              onClick={() => onChoosePortal('/placement/login')}
            >
              Sign in to Placement
            </button>
            <p className="portal-account-note">
              Placement accounts are managed by Director HR. There is no public
              account registration flow.
            </p>
          </article>
        </div>
      </section>
    </main>
  );
}
