import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import RoundDetails from './RoundDetails';

describe('RoundDetails', () => {
  it('renders all four rounds and exposes bounded progress accessibly', () => {
    render(
      <RoundDetails
        rounds={[
          { name: 'Aptitude', progress: 110, status: 'Complete' },
          { name: 'Technical', progress: 45, status: 'In progress' },
          { name: 'HR', progress: -10, status: 'Not started' },
        ]}
      />,
    );

    expect(screen.getByRole('heading', { name: 'Aptitude' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Technical' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Managerial' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'HR' })).toBeInTheDocument();
    expect(screen.getByRole('progressbar', { name: 'Aptitude progress' })).toHaveAttribute('aria-valuenow', '100');
    expect(screen.getByRole('progressbar', { name: 'HR progress' })).toHaveAttribute('aria-valuenow', '0');
    expect(screen.getByText('Progress not provided')).toBeInTheDocument();
  });

  it('shows an unavailable state when round data is missing', () => {
    render(<RoundDetails />);
    expect(screen.getByText('Round progress unavailable')).toBeInTheDocument();
  });
});
