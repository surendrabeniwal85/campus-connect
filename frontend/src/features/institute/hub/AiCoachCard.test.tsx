import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import AiCoachCard, { type CoachRecommendation } from './AiCoachCard';

const recommendations: CoachRecommendation[] = [
  { id: 'one', action: 'Review your resume', reason: 'Your ATS score can improve.' },
  { id: 'two', action: 'Attend a practice session', reason: 'Technical practice is available.' },
  { id: 'three', action: 'Check drive requirements', reason: 'A matching drive is open.' },
  { id: 'four', action: 'Fourth action is hidden', reason: 'Only three are shown.' },
];

describe('AiCoachCard', () => {
  it('shows no more than three actions and explains each recommendation', () => {
    render(<AiCoachCard recommendations={recommendations} />);

    expect(screen.getAllByRole('listitem')).toHaveLength(3);
    expect(screen.getByText('Your ATS score can improve.')).toBeInTheDocument();
    expect(screen.queryByText('Fourth action is hidden')).not.toBeInTheDocument();
  });

  it('identifies missing facts without inventing recommendations', () => {
    render(<AiCoachCard missingFacts={['attendance', 'CGPA']} />);

    expect(screen.getByText('Not available yet: attendance, CGPA.')).toBeInTheDocument();
    expect(screen.getByText('Recommendations unavailable')).toBeInTheDocument();
  });
});
