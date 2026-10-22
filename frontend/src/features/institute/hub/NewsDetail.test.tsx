import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';

import NewsDetail from './NewsDetail';
import type { NewsItem } from './newsTypes';

const item: NewsItem = {
  id: 'drive-1',
  headline: 'Engineering hiring drive',
  type: 'DRIVE',
  publishedAt: '2026-10-20T10:00:00Z',
  summary: 'Applications are open.',
  body: 'Review the role requirements before applying.',
  deadline: '2026-10-30',
  actionLabel: 'Check opportunity',
};

describe('NewsDetail', () => {
  it('shows the full item and invokes back and action callbacks', () => {
    const onBack = vi.fn();
    const onAction = vi.fn();
    render(<NewsDetail item={item} onBack={onBack} onAction={onAction} />);

    expect(screen.getByRole('heading', { name: item.headline })).toBeInTheDocument();
    expect(screen.getByText(item.body!)).toBeInTheDocument();
    expect(screen.getByText(/Deadline:/)).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: '← Back to news' }));
    fireEvent.click(screen.getByRole('button', { name: item.actionLabel! }));

    expect(onBack).toHaveBeenCalledOnce();
    expect(onAction).toHaveBeenCalledWith(item);
  });

  it('falls back to the summary and omits the action when none is supplied', () => {
    const summaryItem = { ...item, body: undefined, actionLabel: undefined };
    render(<NewsDetail item={summaryItem} onBack={vi.fn()} />);

    expect(screen.getByText(item.summary)).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: item.actionLabel! })).not.toBeInTheDocument();
  });
});
