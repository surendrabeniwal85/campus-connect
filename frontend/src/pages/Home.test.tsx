import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';

import Home from './Home';

describe('Home', () => {
  it('presents the product, portal doors, and page sections', () => {
    render(<Home />);

    expect(screen.getByText(/Powered by AI/)).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Institute portal' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Placement portal' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Features' })).toHaveAttribute('href', '#features');
    expect(screen.getByRole('link', { name: 'About' })).toHaveAttribute('href', '#about');
    expect(screen.getByRole('link', { name: 'Contact' })).toHaveAttribute('href', '#contact');
  });

  it('keeps portal actions disabled until a route callback is provided', () => {
    render(<Home />);

    expect(screen.getByRole('button', { name: /Continue to Institute/ })).toBeDisabled();
    expect(screen.getByRole('button', { name: /Continue to Placement/ })).toBeDisabled();
    expect(screen.getByText('Portal sign-in routes are not connected yet.')).toBeInTheDocument();
  });

  it('calls the corresponding portal callback when a portal is selected', () => {
    const onPortalSelect = vi.fn();
    render(<Home onPortalSelect={onPortalSelect} />);

    fireEvent.click(screen.getByRole('button', { name: /Continue to Institute/ }));
    fireEvent.click(screen.getByRole('button', { name: /Continue to Placement/ }));

    expect(onPortalSelect).toHaveBeenNthCalledWith(1, 'institute');
    expect(onPortalSelect).toHaveBeenNthCalledWith(2, 'placement');
    expect(screen.queryByText('Portal sign-in routes are not connected yet.')).not.toBeInTheDocument();
  });
});
