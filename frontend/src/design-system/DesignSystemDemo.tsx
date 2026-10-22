import { useState } from 'react';

import {
  Button,
  Card,
  DataTable,
  EmptyState,
  ErrorState,
  FunnelChart,
  LoadingState,
  Modal,
  ProgressRing,
  RoundTimeline,
  SectionTitle,
  Skeleton,
  TextField,
  Toast,
} from './components';

export default function DesignSystemDemo() {
  const [theme, setTheme] = useState<'light' | 'dark'>('light');
  const [showModal, setShowModal] = useState(false);
  const [showToast, setShowToast] = useState(false);

  return (
    <div className="ds-demo" data-theme={theme}>
      <header className="ds-demo__header">
        <div>
          <p className="ds-demo__eyebrow">PLACEMENT ASSISTANT</p>
          <h1>Design system</h1>
        </div>
        <Button variant="secondary" onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}>
          Switch to {theme === 'light' ? 'dark' : 'light'} theme
        </Button>
      </header>

      <section className="ds-demo__grid" aria-label="Component examples">
        <Card>
          <SectionTitle eyebrow="Actions" title="Buttons" />
          <div className="ds-demo__row">
            <Button>Institute action</Button>
            <Button variant="secondary">Secondary</Button>
            <Button variant="danger">Danger</Button>
          </div>
        </Card>

        <Card>
          <SectionTitle eyebrow="Inputs" title="Form field" />
          <TextField label="Target role" placeholder="e.g. Software engineer" />
        </Card>

        <Card>
          <SectionTitle eyebrow="Status" title="Feedback states" />
          <div className="ds-demo__stack">
            <LoadingState label="Loading dashboard data" />
            <EmptyState title="No announcements">New announcements will appear here.</EmptyState>
            <ErrorState message="Unable to load this section." onRetry={() => setShowToast(true)} />
            <Skeleton width="70%" />
          </div>
        </Card>

        <Card>
          <SectionTitle eyebrow="Progress" title="Readiness and rounds" />
          <div className="ds-demo__row">
            <ProgressRing value={78} label="Placement readiness" />
            <RoundTimeline rounds={[
              { name: 'Aptitude', status: 'complete' },
              { name: 'Technical', status: 'current' },
              { name: 'Managerial', status: 'upcoming' },
              { name: 'HR', status: 'upcoming' },
            ]} />
          </div>
        </Card>

        <Card>
          <SectionTitle eyebrow="Data" title="Table and funnel" />
          <DataTable
            caption="Example placement stages"
            headers={['Stage', 'Students']}
            rows={ [['Applied', 120], ['Shortlisted', 48]] }
          />
          <FunnelChart steps={[
            { label: 'Applied', value: 120 },
            { label: 'Shortlisted', value: 48 },
            { label: 'Selected', value: 12 },
          ]} />
        </Card>

        <Card>
          <SectionTitle eyebrow="Overlays" title="Modal and toast" />
          <div className="ds-demo__row">
            <Button onClick={() => setShowModal(true)}>Open dialog</Button>
            <Button variant="secondary" onClick={() => setShowToast(true)}>Show toast</Button>
          </div>
          {showModal && (
            <Modal title="Example dialog" onClose={() => setShowModal(false)}>
              <p>This dialog uses a labelled dialog role and a keyboard-operable close button.</p>
            </Modal>
          )}
          {showToast && <Toast>Example status notification.</Toast>}
        </Card>
      </section>
    </div>
  );
}
