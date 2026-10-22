import { Button } from '../design-system/components';

import './home.css';

export type Portal = 'institute' | 'placement';

export default function Home({
  onPortalSelect,
}: {
  onPortalSelect?: (portal: Portal) => void;
}) {
  return (
    <div className="home-page">
      <header className="home-header">
        <a className="home-brand" href="#home" aria-label="Placement Assistant home">
          <span aria-hidden="true">PA</span>
          <span>Placement Assistant</span>
        </a>
        <nav aria-label="Page sections">
          <a href="#features">Features</a>
          <a href="#about">About</a>
          <a href="#contact">Contact</a>
        </nav>
      </header>

      <section className="home-hero" id="home">
        <p className="home-eyebrow">YOUR CAMPUS, YOUR CAREER</p>
        <h1>One place for every next step.</h1>
        <p className="home-tagline">Placement Assistant <span>· Powered by AI</span></p>
        <p className="home-intro">
          Stay connected to institute life and discover opportunities that move your career forward.
        </p>
        <div className="home-portals" aria-label="Choose a portal">
          <article className="home-portal home-portal--institute">
            <span className="home-portal__label">INSTITUTE</span>
            <h2>Institute portal</h2>
            <p>Student, teacher and HOD tools for your campus journey.</p>
            <Button
              className="home-portal__button"
              variant="secondary"
              disabled={!onPortalSelect}
              aria-describedby={!onPortalSelect ? 'portal-route-note' : undefined}
              onClick={() => onPortalSelect?.('institute')}
            >
              Continue to Institute
              <span aria-hidden="true">→</span>
            </Button>
          </article>
          <article className="home-portal home-portal--placement">
            <span className="home-portal__label">PLACEMENT</span>
            <h2>Placement portal</h2>
            <p>Explore drives, prepare your resume and track placement rounds.</p>
            <Button
              className="home-portal__button"
              variant="secondary"
              disabled={!onPortalSelect}
              aria-describedby={!onPortalSelect ? 'portal-route-note' : undefined}
              onClick={() => onPortalSelect?.('placement')}
            >
              Continue to Placement
              <span aria-hidden="true">→</span>
            </Button>
          </article>
        </div>
        {!onPortalSelect && (
          <p className="home-route-note" id="portal-route-note">
            Portal sign-in routes are not connected yet.
          </p>
        )}
      </section>

      <section className="home-section" id="features" aria-labelledby="features-title">
        <p className="home-eyebrow">BUILT AROUND YOUR JOURNEY</p>
        <h2 id="features-title">The essentials, connected.</h2>
        <div className="home-feature-grid">
          <article><span aria-hidden="true">01</span><h3>Campus hub</h3><p>Find student services and updates in one place.</p></article>
          <article><span aria-hidden="true">02</span><h3>Placement journey</h3><p>Keep track of opportunities, rounds and next steps.</p></article>
          <article><span aria-hidden="true">03</span><h3>Resume readiness</h3><p>Get transparent, actionable guidance as you prepare.</p></article>
        </div>
      </section>

      <section className="home-about" id="about" aria-labelledby="about-title">
        <p className="home-eyebrow">ABOUT PLACEMENT ASSISTANT</p>
        <h2 id="about-title">A clearer path from campus to career.</h2>
        <p>
          Placement Assistant brings institute services and placement preparation together
          in a shared experience. Guidance is intended to be transparent and supportive.
        </p>
      </section>

      <footer className="home-contact" id="contact">
        <div>
          <p className="home-eyebrow">CONTACT</p>
          <h2>Need help getting started?</h2>
        </div>
        <p>Contact your institute or placement office for assistance.</p>
      </footer>
    </div>
  );
}
