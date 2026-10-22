import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';

import StudentDashboard, { type DashboardTile } from './StudentDashboard';
import StaffDashboard from './StaffDashboard';

const tile: DashboardTile = {
  id: 'attendance',
  title: 'Class attendance',
  description: 'Review your attendance summary.',
  value: '92%',
};

describe('Institute dashboards', () => {
  it('renders student news before tiles and opens a selected tile', () => {
    const onOpenTile = vi.fn();
    render(
      <StudentDashboard
        tiles={[tile]}
        news={<div>Latest campus news</div>}
        onOpenTile={onOpenTile}
      />,
    );

    expect(screen.getByText('Latest campus news').compareDocumentPosition(
      screen.getByRole('heading', { name: 'Student dashboard' }),
    ) & Node.DOCUMENT_POSITION_FOLLOWING).toBeTruthy();
    expect(screen.getByText('92%')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Open Class attendance' }));
    expect(onOpenTile).toHaveBeenCalledWith(tile);
  });

  it.each([
    ['TEACHER', 'Teacher dashboard'],
    ['HOD', 'HOD dashboard'],
  ] as const)('renders the role-specific %s dashboard', (role, heading) => {
    const onOpenTile = vi.fn();
    render(<StaffDashboard role={role} tiles={[tile]} onOpenTile={onOpenTile} />);

    expect(screen.getByRole('heading', { name: heading })).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Open Class attendance' }));
    expect(onOpenTile).toHaveBeenCalledWith(tile);
  });

  it('shows a useful empty state when no tiles are available', () => {
    render(<StudentDashboard tiles={[]} news={null} />);
    expect(screen.getByText('Dashboard details unavailable')).toBeInTheDocument();
  });
});
