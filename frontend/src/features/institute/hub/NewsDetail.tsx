import type { NewsItem } from './newsTypes';

import { Button, Card } from '../../../design-system/components';

import './hub.css';

export default function NewsDetail({
  item,
  onBack,
  onAction,
}: {
  item: NewsItem;
  onBack: () => void;
  onAction?: (item: NewsItem) => void;
}) {
  return (
    <article className="hub-news-detail">
      <Button variant="quiet" onClick={onBack}>← Back to news</Button>
      <Card>
        <p className="eyebrow">{item.type.replaceAll('_', ' ')}</p>
        <h1>{item.headline}</h1>
        <p>
          <time dateTime={item.publishedAt}>
            {new Date(item.publishedAt).toLocaleDateString()}
          </time>
        </p>
        <p>{item.body ?? item.summary}</p>
        {item.deadline && (
          <p>
            Deadline: <time dateTime={item.deadline}>{new Date(item.deadline).toLocaleDateString()}</time>
          </p>
        )}
        {onAction && item.actionLabel && (
          <Button onClick={() => onAction(item)}>{item.actionLabel}</Button>
        )}
      </Card>
    </article>
  );
}
