import type { NewsItem } from '../features/institute/hub/newsTypes';

import './news-card.css';

export default function NewsCard({
  item,
  onOpen,
}: {
  item: NewsItem;
  onOpen: (item: NewsItem) => void;
}) {
  return (
    <article className="news-card">
      <button className="news-card__open" type="button" onClick={() => onOpen(item)}>
        <span className="news-card__meta">
          <span className={`news-card__type news-card__type--${item.type.toLowerCase()}`}>
            {item.type.replaceAll('_', ' ')}
          </span>
          <time dateTime={item.publishedAt}>
            {new Date(item.publishedAt).toLocaleDateString()}
          </time>
        </span>
        <span className="news-card__headline">{item.headline}</span>
        <span className="news-card__summary">{item.summary}</span>
        {item.deadline && (
          <span className="news-card__deadline">
            Deadline: <time dateTime={item.deadline}>{new Date(item.deadline).toLocaleDateString()}</time>
          </span>
        )}
        <span className="news-card__link">Read details <span aria-hidden="true">→</span></span>
      </button>
    </article>
  );
}
