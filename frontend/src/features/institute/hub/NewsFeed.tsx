import { useMemo } from 'react';

import {
  Button,
  EmptyState,
  ErrorState,
  LoadingState,
} from '../../../design-system/components';
import NewsCard from '../../../widgets/NewsCard';
import {
  NEWS_PAGE_SIZE,
  isNewsType,
  NEWS_TYPES,
  type NewsFeedStatus,
  type NewsItem,
  type NewsType,
} from './newsTypes';

import './hub.css';

export default function NewsFeed({
  items,
  status,
  page,
  totalPages,
  selectedType,
  onTypeChange,
  onPageChange,
  onOpen,
  onRetry,
}: {
  items: NewsItem[];
  status: NewsFeedStatus;
  page: number;
  totalPages: number;
  selectedType: NewsType | 'ALL';
  onTypeChange: (type: NewsType | 'ALL') => void;
  onPageChange: (page: number) => void;
  onOpen: (item: NewsItem) => void;
  onRetry?: () => void;
}) {
  function handleTypeChange(value: string) {
    if (value === 'ALL' || isNewsType(value)) {
      onTypeChange(value);
    }
  }

  const newestFirst = useMemo(
    () => [...items].sort((left, right) =>
      Date.parse(right.publishedAt) - Date.parse(left.publishedAt)),
    [items],
  );

  return (
    <section className="hub-news" aria-labelledby="hub-news-title">
      <div className="hub-news__heading">
        <div>
          <p className="eyebrow">CAMPUS UPDATES</p>
          <h2 id="hub-news-title">News and opportunities</h2>
        </div>
        <label>
          <span>Filter by type</span>
          <select
            value={selectedType}
            onChange={(event) => handleTypeChange(event.target.value)}
          >
            <option value="ALL">All updates</option>
            {NEWS_TYPES.map((type) => (
              <option value={type} key={type}>{type.replaceAll('_', ' ')}</option>
            ))}
          </select>
        </label>
      </div>

      {status === 'loading' && <LoadingState label="Loading news" />}
      {status === 'error' && (
        <ErrorState message="News could not be loaded." onRetry={onRetry} />
      )}
      {status === 'empty' && (
        <EmptyState title="No news to show">
          Try another category or check back later.
        </EmptyState>
      )}
      {status === 'success' && (
        <>
          <div className="hub-news__list">
            {newestFirst.slice(0, NEWS_PAGE_SIZE).map((item) => (
              <NewsCard item={item} onOpen={onOpen} key={item.id} />
            ))}
          </div>
          <nav className="hub-news__pagination" aria-label="News pages">
            <Button
              variant="secondary"
              disabled={page <= 1}
              onClick={() => onPageChange(page - 1)}
            >
              Previous
            </Button>
            <span aria-live="polite">Page {page} of {Math.max(totalPages, 1)}</span>
            <Button
              variant="secondary"
              disabled={page >= totalPages}
              onClick={() => onPageChange(page + 1)}
            >
              Next
            </Button>
          </nav>
        </>
      )}
    </section>
  );
}
