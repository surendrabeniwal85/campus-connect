import { fireEvent, render, screen, within } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';

import NewsFeed from './NewsFeed';
import type { NewsItem, NewsType } from './newsTypes';

function makeItem(
  id: string,
  publishedAt: string,
  type: NewsType = 'DRIVE',
): NewsItem {
  return {
    id,
    headline: `News ${id}`,
    type,
    publishedAt,
    summary: `Summary ${id}`,
  };
}

function renderFeed({
  items = [],
  status = 'success',
  page = 1,
  totalPages = 1,
  selectedType = 'ALL',
  onTypeChange = vi.fn(),
  onPageChange = vi.fn(),
  onOpen = vi.fn(),
  onRetry = vi.fn(),
}: {
  items?: NewsItem[];
  status?: 'loading' | 'success' | 'empty' | 'error';
  page?: number;
  totalPages?: number;
  selectedType?: NewsType | 'ALL';
  onTypeChange?: (type: NewsType | 'ALL') => void;
  onPageChange?: (page: number) => void;
  onOpen?: (item: NewsItem) => void;
  onRetry?: () => void;
} = {}) {
  return render(
    <NewsFeed
      items={items}
      status={status}
      page={page}
      totalPages={totalPages}
      selectedType={selectedType}
      onTypeChange={onTypeChange}
      onPageChange={onPageChange}
      onOpen={onOpen}
      onRetry={onRetry}
    />,
  );
}

describe('NewsFeed', () => {
  it('orders items newest first and displays no more than ten cards', () => {
    const items = Array.from({ length: 11 }, (_, index) =>
      makeItem(String(index + 1), `2026-10-${String(index + 1).padStart(2, '0')}T10:00:00Z`),
    );

    renderFeed({ items });

    const cards = screen.getAllByRole('article');
    expect(cards).toHaveLength(10);
    expect(within(cards[0]).getByText('News 11')).toBeInTheDocument();
    expect(within(cards[9]).getByText('News 2')).toBeInTheDocument();
    expect(screen.queryByText('News 1')).not.toBeInTheDocument();
  });

  it('notifies the owner when the type filter changes', () => {
    const onTypeChange = vi.fn();
    renderFeed({ onTypeChange });

    fireEvent.change(screen.getByLabelText('Filter by type'), {
      target: { value: 'OFF_CAMPUS' },
    });

    expect(onTypeChange).toHaveBeenCalledWith('OFF_CAMPUS');
  });

  it('opens the selected news item and exposes page navigation state', () => {
    const item = makeItem('drive-1', '2026-10-20T10:00:00Z');
    const onOpen = vi.fn();
    const onPageChange = vi.fn();
    renderFeed({ items: [item], page: 2, totalPages: 3, onOpen, onPageChange });

    fireEvent.click(screen.getByRole('button', { name: /News drive-1/ }));
    fireEvent.click(screen.getByRole('button', { name: 'Previous' }));
    fireEvent.click(screen.getByRole('button', { name: 'Next' }));

    expect(onOpen).toHaveBeenCalledWith(item);
    expect(onPageChange).toHaveBeenNthCalledWith(1, 1);
    expect(onPageChange).toHaveBeenNthCalledWith(2, 3);
    expect(screen.getByText('Page 2 of 3')).toBeInTheDocument();
  });

  it('disables pagination at the first and last pages', () => {
    const { rerender } = render(
      <NewsFeed
        items={[]}
        status="success"
        page={1}
        totalPages={2}
        selectedType="ALL"
        onTypeChange={vi.fn()}
        onPageChange={vi.fn()}
        onOpen={vi.fn()}
      />,
    );
    expect(screen.getByRole('button', { name: 'Previous' })).toBeDisabled();

    rerender(
      <NewsFeed
        items={[]}
        status="success"
        page={2}
        totalPages={2}
        selectedType="ALL"
        onTypeChange={vi.fn()}
        onPageChange={vi.fn()}
        onOpen={vi.fn()}
      />,
    );
    expect(screen.getByRole('button', { name: 'Next' })).toBeDisabled();
  });

  it('renders loading, empty, and retryable error states', () => {
    const { rerender } = renderFeed({ status: 'loading' });
    expect(screen.getByText('Loading news')).toBeInTheDocument();

    rerender(
      <NewsFeed
        items={[]}
        status="empty"
        page={1}
        totalPages={0}
        selectedType="ALL"
        onTypeChange={vi.fn()}
        onPageChange={vi.fn()}
        onOpen={vi.fn()}
      />,
    );
    expect(screen.getByText('No news to show')).toBeInTheDocument();

    const onRetry = vi.fn();
    rerender(
      <NewsFeed
        items={[]}
        status="error"
        page={1}
        totalPages={0}
        selectedType="ALL"
        onTypeChange={vi.fn()}
        onPageChange={vi.fn()}
        onOpen={vi.fn()}
        onRetry={onRetry}
      />,
    );
    fireEvent.click(screen.getByRole('button', { name: 'Try again' }));
    expect(onRetry).toHaveBeenCalledOnce();
  });
});
