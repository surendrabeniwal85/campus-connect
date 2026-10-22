export type NewsType =
  | 'DRIVE'
  | 'TALK'
  | 'HACKATHON'
  | 'INTERNSHIP'
  | 'OFF_CAMPUS'
  | 'ROUND_UPDATE';

export type NewsItem = {
  id: string;
  headline: string;
  type: NewsType;
  publishedAt: string;
  summary: string;
  deadline?: string;
  body?: string;
  actionLabel?: string;
};

export type NewsFeedStatus = 'loading' | 'success' | 'empty' | 'error';

export const NEWS_TYPES: readonly NewsType[] = [
  'DRIVE',
  'TALK',
  'HACKATHON',
  'INTERNSHIP',
  'OFF_CAMPUS',
  'ROUND_UPDATE',
];

export const NEWS_PAGE_SIZE = 10;

export function isNewsType(value: string): value is NewsType {
  return NEWS_TYPES.some((type) => type === value);
}
