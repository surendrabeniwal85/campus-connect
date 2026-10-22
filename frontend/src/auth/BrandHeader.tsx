import { useState, useEffect } from 'react';
import { getPreferredTheme, toggleTheme, applyTheme, type Theme } from '../design-system/theme';

interface BrandHeaderProps {
  readonly showThemeToggle?: boolean;
}

export default function BrandHeader({ showThemeToggle = true }: BrandHeaderProps) {
  const [theme, setCurrentTheme] = useState<Theme>('light');

  useEffect(() => {
    const current = getPreferredTheme();
    setCurrentTheme(current);
    applyTheme(current);
  }, []);

  function handleToggle() {
    const next = toggleTheme(theme);
    setCurrentTheme(next);
  }

  return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
      <div className="pa-brand" aria-label="Placement Assistant, Powered by AI">
        <svg className="pa-brand-mark" viewBox="0 0 40 40" aria-hidden="true">
          <path
            d="M7 10.5h9.2c3.1 0 5.3 1.2 6.8 3.4 1.5-2.2 3.7-3.4 6.8-3.4H33v19h-3.2c-3.2 0-5.3 1.1-6.8 3.1-1.5-2-3.6-3.1-6.8-3.1H7z"
            fill="none"
            stroke="currentColor"
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth="2.5"
          />
          <path d="M23 14v17" fill="none" stroke="currentColor" strokeWidth="2.5" />
          <circle cx="31.5" cy="8.5" r="3" fill="var(--pa-color-saffron)" />
        </svg>
        <span className="pa-brand-copy">
          <span className="pa-brand-name">Placement Assistant</span>
          <span className="pa-brand-tagline">Powered by AI</span>
        </span>
      </div>
      {showThemeToggle && (
        <button
          className="theme-toggle-btn"
          type="button"
          onClick={handleToggle}
          title={`Switch to ${theme === 'dark' ? 'Light' : 'Dark'} mode`}
          aria-label={`Toggle theme, current is ${theme}`}
        >
          {theme === 'dark' ? '☀️' : '🌙'}
        </button>
      )}
    </div>
  );
}
