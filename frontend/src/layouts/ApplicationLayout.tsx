import { useEffect, useMemo, useState, type ReactNode } from 'react';

import { Button, Modal } from '../design-system/components';

import './application-layout.css';

export type AppNavigationItem<T extends string = string> = {
  label: T;
  icon: string;
};

export default function ApplicationLayout<T extends string>({
  navigation,
  activePage,
  homePageLabel,
  onNavigate,
  children,
}: {
  navigation: AppNavigationItem<T>[];
  activePage: T;
  homePageLabel: T;
  onNavigate: (page: T) => void;
  children: ReactNode;
}) {
  const [quickNavOpen, setQuickNavOpen] = useState(false);
  const [query, setQuery] = useState('');

  useEffect(() => {
    function handleShortcut(event: KeyboardEvent) {
      if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'k') {
        event.preventDefault();
        setQuickNavOpen(true);
      }
    }

    window.addEventListener('keydown', handleShortcut);
    return () => window.removeEventListener('keydown', handleShortcut);
  }, []);

  const matches = useMemo(
    () => navigation.filter((item) =>
      item.label.toLowerCase().includes(query.trim().toLowerCase())),
    [navigation, query],
  );

  function selectPage(page: T) {
    setQuickNavOpen(false);
    setQuery('');
    onNavigate(page);
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <button className="brand-mark" aria-label="Go to home" onClick={() => selectPage(homePageLabel)}>
          PA
        </button>
        <p className="eyebrow">CAMPUS PLATFORM</p>
        <h1>Placement<br />Assistant</h1>
        <p className="muted">One campus. Connected workflows.</p>
        <nav className="app-sidebar-nav" aria-label="Main navigation">
          {navigation.map((item) => (
            <button
              key={item.label}
              className={activePage === item.label ? 'active' : ''}
              aria-current={activePage === item.label ? 'page' : undefined}
              onClick={() => selectPage(item.label)}
            >
              <span className="nav-icon" aria-hidden="true">{item.icon}</span>
              {item.label}
            </button>
          ))}
        </nav>
        <div className="sidebar-note">
          <span className="status-dot" /> Campus services
          <small>Academic life, attendance and careers</small>
        </div>
      </aside>

      <div className="app-content">
        <header className="app-topbar">
          <nav className="app-breadcrumbs" aria-label="Breadcrumb">
            <ol>
              <li><button onClick={() => selectPage(homePageLabel)}>{homePageLabel}</button></li>
              {activePage !== homePageLabel && <li aria-current="page">{activePage}</li>}
            </ol>
          </nav>
          <div className="app-topbar__actions">
            <div role="search">
              <label className="visually-hidden" htmlFor="app-quick-search">Search pages</label>
              <input
                id="app-quick-search"
                type="search"
                placeholder="Search pages"
                readOnly
                onClick={() => setQuickNavOpen(true)}
                onFocus={() => setQuickNavOpen(true)}
                aria-keyshortcuts="Control+K Meta+K"
              />
            </div>
            <Button
              variant="quiet"
              disabled
              aria-label="Notifications integration pending"
              title="Notifications are not connected"
            >
              <span aria-hidden="true">♢</span>
            </Button>
            <div className="app-account" aria-label="Account">
              <span aria-hidden="true">PA</span>
              <span>Account</span>
            </div>
          </div>
        </header>
        <main className="content">{children}</main>
      </div>

      {quickNavOpen && (
        <Modal title="Quick navigation" onClose={() => setQuickNavOpen(false)}>
          <label className="app-quick-nav__label" htmlFor="quick-nav-query">Find a page</label>
          <input
            id="quick-nav-query"
            className="app-quick-nav__input"
            autoFocus
            value={query}
            onChange={(event) => setQuery(event.target.value)}
          />
          <ul className="app-quick-nav__results">
            {matches.map((item) => (
              <li key={item.label}>
                <button type="button" onClick={() => selectPage(item.label)}>
                  <span aria-hidden="true">{item.icon}</span>
                  {item.label}
                </button>
              </li>
            ))}
            {matches.length === 0 && <li role="status">No matching pages.</li>}
          </ul>
          <p className="app-quick-nav__hint">Press Ctrl+K or ⌘K to open quick navigation.</p>
        </Modal>
      )}
    </div>
  );
}
