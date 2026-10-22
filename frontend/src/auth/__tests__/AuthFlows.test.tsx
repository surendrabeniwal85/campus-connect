import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import PortalSelection from '../PortalSelection';
import PortalLoginForm from '../PortalLoginForm';
import RegisterStudent from '../../pages/RegisterStudent';
import ResetPassword from '../../pages/ResetPassword';
import { getPreferredTheme, toggleTheme, applyTheme } from '../../design-system/theme';

describe('Theme System', () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.removeAttribute('data-theme');
    document.body.removeAttribute('data-theme');
  });

  it('defaults to light or system theme and allows toggle', () => {
    applyTheme('light');
    expect(document.documentElement.getAttribute('data-theme')).toBe('light');

    const next = toggleTheme('light');
    expect(next).toBe('dark');
    expect(document.documentElement.getAttribute('data-theme')).toBe('dark');
    expect(localStorage.getItem('campus_connect_theme')).toBe('dark');
  });

  it('retrieves saved theme from storage', () => {
    localStorage.setItem('campus_connect_theme', 'dark');
    expect(getPreferredTheme()).toBe('dark');
  });
});

describe('PortalSelection Landing Page', () => {
  it('renders product branding and portal entry points', () => {
    const onChoosePortal = vi.fn();
    render(<PortalSelection onChoosePortal={onChoosePortal} />);

    expect(screen.getByText('Placement Assistant')).toBeInTheDocument();
    expect(screen.getByText('Institute Portal')).toBeInTheDocument();
    expect(screen.getByText('Placement Portal')).toBeInTheDocument();

    const instituteBtn = screen.getByText(/Sign in to Institute Portal/i);
    fireEvent.click(instituteBtn);
    expect(onChoosePortal).toHaveBeenCalledWith('/institute/login');
  });

  it('opens the demo credentials directory modal on button click', () => {
    render(<PortalSelection onChoosePortal={vi.fn()} />);

    const demoBtn = screen.getByText(/View 7 Canonical Demo Accounts/i);
    fireEvent.click(demoBtn);

    expect(screen.getByText(/College Demo Account Directory/i)).toBeInTheDocument();
    expect(screen.getByText(/student@institute.ac.in/i)).toBeInTheDocument();
  });
});

describe('PortalLoginForm', () => {
  it('toggles password visibility between text and password', () => {
    render(
      <PortalLoginForm
        portal="INSTITUTE"
        title="Institute Sign In"
        description="Test login"
        onAuthenticated={vi.fn()}
        onChangePortal={vi.fn()}
        onResetPassword={vi.fn()}
      />
    );

    const passInput = screen.getByLabelText(/^Password$/i) as HTMLInputElement;
    expect(passInput.type).toBe('password');

    const toggleBtn = screen.getByRole('button', { name: /Show password/i });
    fireEvent.click(toggleBtn);
    expect(passInput.type).toBe('text');

    fireEvent.click(toggleBtn);
    expect(passInput.type).toBe('password');
  });

  it('prefills seed credentials when quick-credential chip is clicked', () => {
    render(
      <PortalLoginForm
        portal="INSTITUTE"
        title="Institute Sign In"
        description="Test login"
        onAuthenticated={vi.fn()}
        onChangePortal={vi.fn()}
        onResetPassword={vi.fn()}
      />
    );

    const studentChip = screen.getByText(/🎓 Student/i);
    fireEvent.click(studentChip);

    const emailInput = screen.getByLabelText(/Institutional Email/i) as HTMLInputElement;
    const passInput = screen.getByLabelText(/^Password$/i) as HTMLInputElement;

    expect(emailInput.value).toBe('student@institute.ac.in');
    expect(passInput.value).toBe('Student@123');
  });
});

describe('RegisterStudent Page', () => {
  it('validates institutional email domain on submission', async () => {
    render(<RegisterStudent onBackToLogin={vi.fn()} onRegistered={vi.fn()} />);

    fireEvent.change(screen.getByLabelText(/Full Name/i), { target: { value: 'Test Student' } });
    fireEvent.change(screen.getByLabelText(/Roll Number/i), { target: { value: 'CS2026-099' } });
    fireEvent.change(screen.getByLabelText(/Institutional Email Address/i), { target: { value: 'test@gmail.com' } });
    fireEvent.change(screen.getByLabelText(/Create Password/i), { target: { value: 'Password123!' } });
    fireEvent.change(screen.getByLabelText(/Confirm Password/i), { target: { value: 'Password123!' } });

    fireEvent.click(screen.getByRole('button', { name: /Register Student Account/i }));

    expect(screen.getByText(/Student registration requires an @institute.ac.in institutional email/i)).toBeInTheDocument();
  });
});

describe('ResetPassword Page', () => {
  it('renders request token form and allows transition to token entry', () => {
    render(<ResetPassword portal="INSTITUTE" onBackToLogin={vi.fn()} />);

    expect(screen.getByText(/Reset your password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Generate Reset Token/i })).toBeInTheDocument();

    const switchBtn = screen.getByText(/Already have a reset token/i);
    fireEvent.click(switchBtn);

    expect(screen.getByLabelText(/Reset Verification Token/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Set New Password/i })).toBeInTheDocument();
  });
});
