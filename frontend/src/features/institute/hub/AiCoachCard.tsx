import { Card, EmptyState } from '../../../design-system/components';

import './hub.css';

export type CoachRecommendation = {
  id: string;
  action: string;
  reason: string;
};

export default function AiCoachCard({
  recommendations,
  missingFacts = [],
}: {
  recommendations?: CoachRecommendation[];
  missingFacts?: string[];
}) {
  return (
    <Card className="hub-ai-coach" aria-labelledby="ai-coach-title">
      <p className="eyebrow">RULE-BASED GUIDANCE</p>
      <h2 id="ai-coach-title">AI Coach</h2>
      {missingFacts.length > 0 && (
        <p className="hub-ai-coach__missing">
          Not available yet: {missingFacts.join(', ')}.
        </p>
      )}
      {!recommendations || recommendations.length === 0 ? (
        <EmptyState title="Recommendations unavailable">
          Guidance will be shown when enough placement and readiness information is available.
        </EmptyState>
      ) : (
        <ol className="hub-ai-coach__recommendations">
          {recommendations.slice(0, 3).map((recommendation) => (
            <li key={recommendation.id}>
              <h3>{recommendation.action}</h3>
              <p>{recommendation.reason}</p>
            </li>
          ))}
        </ol>
      )}
    </Card>
  );
}
