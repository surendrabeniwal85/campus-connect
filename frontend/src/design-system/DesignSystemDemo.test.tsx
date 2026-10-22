import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import DesignSystemDemo from './DesignSystemDemo';

describe('DesignSystemDemo', () => {
  it('switches between light and dark themes using its accessible control', () => {
    const { container } = render(<DesignSystemDemo />);
    const demo = container.querySelector('.ds-demo');

    expect(demo).toHaveAttribute('data-theme', 'light');
    fireEvent.click(screen.getByRole('button', { name: 'Switch to dark theme' }));
    expect(demo).toHaveAttribute('data-theme', 'dark');
    fireEvent.click(screen.getByRole('button', { name: 'Switch to light theme' }));
    expect(demo).toHaveAttribute('data-theme', 'light');
  });
});
