import { useState } from 'react';
import PlacementDashboard from './features/placement/PlacementDashboard';
import RoundBoard from './features/placement/RoundBoard';
import EvaluatorForm from './features/placement/EvaluatorForm';
import ReadinessCard from './features/placement/ReadinessCard';

import ClassroomRoutes from './features/institute/classroom/routes';
import './features/institute/classroom/classroom.css';

type Page = 'Overview' | 'Academics' | 'Attendance' | 'Placements' | 'Announcements';

const navigation: { label: Page; icon: string }[] = [
  { label: 'Overview', icon: '⌂' },
  { label: 'Academics', icon: '▤' },
  { label: 'Attendance', icon: '✓' },
  { label: 'Placements', icon: '↗' },
  { label: 'Announcements', icon: '◉' },
];

const modules: { number: string; title: Page; description: string; detail: string }[] = [
  { number: '01', title: 'Academics', description: 'Review your profile, semester details and academic progress.', detail: 'View academic summary' },
  { number: '02', title: 'Attendance', description: 'Check attendance percentages and recent class sessions.', detail: 'View attendance' },
  { number: '03', title: 'Placements', description: 'Explore placement drives, eligibility and application status.', detail: 'Explore opportunities' },
  { number: '04', title: 'Announcements', description: 'Read campus notices, faculty updates and CRT information.', detail: 'Read announcements' },
];

function PageHeader({ eyebrow, title, description }: { eyebrow: string; title: string; description: string }) {
  return <div className="page-heading"><p className="eyebrow">{eyebrow}</p><h2>{title}</h2><p>{description}</p></div>;
}

function Overview({ navigate }: { navigate: (page: Page) => void }) {
  return <>
    <header className="topbar">
      <div><p className="eyebrow">STUDENT SUCCESS / WORKSPACE</p><h2>Campus overview</h2></div>
      <div className="profile-chip"><span>AS</span><div><strong>Ashish Sharma</strong><small>Student account</small></div></div>
    </header>
    <section className="welcome-panel">
      <div><p className="eyebrow light">YOUR CAMPUS, AT A GLANCE</p><h3>Make every next step<br />a little clearer.</h3><p>Academic progress, attendance and placement opportunities — all in one place.</p></div>
      <div className="welcome-art" aria-hidden="true"><div className="orbit orbit-one" /><div className="orbit orbit-two" /><div className="art-core">CC</div></div>
    </section>
    <div className="section-heading"><div><p className="eyebrow">YOUR WORKSPACE</p><h3>Campus services</h3></div><span className="pill">4 sections</span></div>
    <section className="module-grid" aria-label="Campus services">
      {modules.map((module) => <button className="module-card" key={module.number} onClick={() => navigate(module.title)}>
        <div className="card-top"><span className="module-number">{module.number}</span><span className="card-arrow" aria-hidden="true">↗</span></div>
        <h4>{module.title}</h4><p>{module.description}</p><span className="card-link">{module.detail} <span aria-hidden="true">→</span></span>
      </button>)}
    </section>
    <section className="summary-grid" aria-label="At a glance">
      <article className="summary-card"><span className="summary-icon">▤</span><div><p>Current semester</p><strong>Semester 7</strong><small>Academic year 2026–27</small></div></article>
      <article className="summary-card"><span className="summary-icon">✓</span><div><p>Attendance overview</p><strong>92%</strong><small>Across listed subjects</small></div></article>
      <article className="summary-card"><span className="summary-icon">↗</span><div><p>Placement activity</p><strong>Active drives</strong><small>Check Placements module</small></div></article>
    </section>
    <footer><span>Campus Connect</span><span>Academic · Attendance · Careers</span></footer>
  </>;
}

function Academics() {
  return <>
    <PageHeader eyebrow="STUDENT SERVICES" title="Academics" description="Your academic profile and semester progress." />
    <section className="profile-panel"><div className="avatar-large">AS</div><div className="student-info"><h3>Ashish Sharma</h3><p>B.Tech · Computer Science & Engineering</p><div className="tag-row"><span className="tag">Full Stack Development</span><span className="tag">Semester 7</span><span className="tag">2026–27</span></div></div></section>
    <section className="summary-grid three"><article className="summary-card"><div><p>Current CGPA</p><strong>8.52</strong><small>As provided in student profile</small></div></article><article className="summary-card"><div><p>Active backlogs</p><strong>0</strong><small>No active backlogs listed</small></div></article><article className="summary-card"><div><p>Programme</p><strong>B.Tech</strong><small>Computer Science & Engineering</small></div></article></section>
    <section className="data-panel"><div className="panel-heading"><div><p className="eyebrow">ACADEMIC RECORD</p><h3>Semester summary</h3></div><span className="tag">Illustrative record</span></div><div className="table-wrap"><table><thead><tr><th>Semester</th><th>Status</th><th>Record</th></tr></thead><tbody><tr><td>Semester 5</td><td><span className="status status-done">Completed</span></td><td>Result available</td></tr><tr><td>Semester 6</td><td><span className="status status-done">Completed</span></td><td>Result available</td></tr><tr><td>Semester 7</td><td><span className="status status-current">In progress</span></td><td>Current semester</td></tr></tbody></table></div><p className="fine-print">Replace illustrative values with verified university records when the academic API is connected.</p></section>
  </>;
}



function Placements() {
  const [subTab, setSubTab] = useState<'dashboard' | 'rounds' | 'evaluations' | 'readiness'>('dashboard');
  const studentId = 1;
  const sampleDriveId = 1;
  const sampleRoundId = 1;

  return (
    <>
      <PageHeader eyebrow="CAREER SERVICES" title="Placements & Readiness" description="Explore placement drives, check explainable eligibility, track rounds, and view your readiness checklist." />
      
      <div style={{ display: 'flex', gap: '10px', margin: '20px 0', borderBottom: '1px solid #e6ebef', paddingBottom: '14px', flexWrap: 'wrap' }}>
        <button 
          onClick={() => setSubTab('dashboard')} 
          style={{
            padding: '8px 16px', borderRadius: '10px', fontSize: '12px', fontWeight: '700', cursor: 'pointer', border: '0',
            background: subTab === 'dashboard' ? '#10283b' : '#edf0f2',
            color: subTab === 'dashboard' ? '#fff' : '#526574'
          }}
        >
          Drives & Applications
        </button>
        <button 
          onClick={() => setSubTab('rounds')} 
          style={{
            padding: '8px 16px', borderRadius: '10px', fontSize: '12px', fontWeight: '700', cursor: 'pointer', border: '0',
            background: subTab === 'rounds' ? '#10283b' : '#edf0f2',
            color: subTab === 'rounds' ? '#fff' : '#526574'
          }}
        >
          TPO Round Board
        </button>
        <button 
          onClick={() => setSubTab('evaluations')} 
          style={{
            padding: '8px 16px', borderRadius: '10px', fontSize: '12px', fontWeight: '700', cursor: 'pointer', border: '0',
            background: subTab === 'evaluations' ? '#10283b' : '#edf0f2',
            color: subTab === 'evaluations' ? '#fff' : '#526574'
          }}
        >
          Evaluator Scoring
        </button>
        <button 
          onClick={() => setSubTab('readiness')} 
          style={{
            padding: '8px 16px', borderRadius: '10px', fontSize: '12px', fontWeight: '700', cursor: 'pointer', border: '0',
            background: subTab === 'readiness' ? '#10283b' : '#edf0f2',
            color: subTab === 'readiness' ? '#fff' : '#526574'
          }}
        >
          Readiness Checklist
        </button>
      </div>

      <div className="placement-module-content">
        {subTab === 'dashboard' && <PlacementDashboard studentId={studentId} />}
        {subTab === 'rounds' && <RoundBoard driveId={sampleDriveId} roundId={sampleRoundId} />}
        {subTab === 'evaluations' && <EvaluatorForm roundId={sampleRoundId} />}
        {subTab === 'readiness' && <ReadinessCard studentId={studentId} driveId={sampleDriveId} />}
      </div>
    </>
  );
}

function Announcements() {
  const notices = [
    { category: 'PLACEMENT CELL', date: '18 Oct 2026', title: 'Placement readiness session', body: 'A preparation session covering resumes, aptitude practice and interview expectations.', tone: 'mint' },
    { category: 'ACADEMICS', date: '17 Oct 2026', title: 'Semester timetable update', body: 'Check the latest timetable shared by your department before attending classes.', tone: 'blue' },
    { category: 'CRT', date: '15 Oct 2026', title: 'Technical practice schedule', body: 'Review the practice schedule and coordinate with your assigned training group.', tone: 'sand' },
  ];
  return <><PageHeader eyebrow="CAMPUS UPDATES" title="Announcements" description="Important notices from the department, placement cell and CRT team." /><section className="notice-list">{notices.map((notice) => <article className="notice-card" key={notice.title}><div className={`notice-icon ${notice.tone}`}>▣</div><div className="notice-content"><div className="notice-meta"><span>{notice.category}</span><time>{notice.date}</time></div><h3>{notice.title}</h3><p>{notice.body}</p></div></article>)}</section><p className="fine-print">These notices are examples for the current interface. Official announcements will appear when the service is connected.</p></>;
}

export default function App() {
  const [activePage, setActivePage] = useState<Page>('Overview');
  const navigate = (page: Page) => {
    setActivePage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return <main className="app-shell">
    <aside className="sidebar">
      <button className="brand-mark" aria-label="Go to overview" onClick={() => navigate('Overview')}>CC</button>
      <p className="eyebrow">CAMPUS PLATFORM</p><h1>Campus<br />Connect</h1><p className="muted">One campus. Connected workflows.</p>
      <nav aria-label="Main navigation">{navigation.map((item) => <button key={item.label} className={activePage === item.label ? 'active' : ''} onClick={() => navigate(item.label)}><span className="nav-icon" aria-hidden="true">{item.icon}</span>{item.label}</button>)}</nav>
      <div className="sidebar-note"><span className="status-dot" /> Campus services<small>Academic life, attendance and careers</small></div>
    </aside>
    <section className="content" key={activePage}>
      {activePage === 'Overview' && <Overview navigate={navigate} />}
      {activePage === 'Academics' && <Academics />}
      {activePage === 'Attendance' && <ClassroomRoutes />}
      {activePage === 'Placements' && <Placements />}
      {activePage === 'Announcements' && <Announcements />}
    </section>
  </main>;
}