import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';

import ResumePage, { type ResumeAnalysisView } from './ResumePage';

const analysis: ResumeAnalysisView = {
  score: 75,
  status: 'STRONG',
  breakdown: [
    {
      category: 'Target-role keywords',
      points: 28,
      maximum: 35,
      tip: 'Add role-specific skills supported by your experience.',
    },
  ],
};

describe('ResumePage', () => {
  it('rejects whitespace-only text and target role before submitting', () => {
    const onSubmitText = vi.fn();
    render(<ResumePage onSubmitText={onSubmitText} />);

    fireEvent.change(screen.getByLabelText('Target role'), { target: { value: '   ' } });
    fireEvent.change(screen.getByLabelText('Resume text'), { target: { value: '   ' } });
    fireEvent.submit(screen.getByRole('button', { name: 'Submit for ATS review' }).closest('form')!);

    expect(screen.getByRole('alert')).toHaveTextContent('Enter resume text and a target role.');
    expect(onSubmitText).not.toHaveBeenCalled();
  });

  it('submits pasted text with a trimmed target role and renders the score breakdown', async () => {
    const onSubmitText = vi.fn().mockResolvedValue(undefined);
    render(<ResumePage analysis={analysis} onSubmitText={onSubmitText} />);
    fireEvent.change(screen.getByLabelText('Target role'), {
      target: { value: '  Software Engineer  ' },
    });
    fireEvent.change(screen.getByLabelText('Resume text'), {
      target: { value: 'Synthetic resume text' },
    });

    fireEvent.click(screen.getByRole('button', { name: 'Submit for ATS review' }));

    await waitFor(() => {
      expect(onSubmitText).toHaveBeenCalledWith('Synthetic resume text', 'Software Engineer');
    });
    expect(screen.getByRole('heading', { name: 'ATS score: 75/100' })).toBeInTheDocument();
    expect(screen.getByText('Status: STRONG')).toBeInTheDocument();
    expect(screen.getByText('Target-role keywords: 28/35')).toBeInTheDocument();
    expect(screen.getByText('Add role-specific skills supported by your experience.')).toBeInTheDocument();
  });

  it('bounds the displayed score and reports a failed submission', async () => {
    const onSubmitText = vi.fn().mockRejectedValue(new Error('request failed'));
    render(
      <ResumePage
        analysis={{ ...analysis, score: 120 }}
        onSubmitText={onSubmitText}
      />,
    );
    fireEvent.change(screen.getByLabelText('Target role'), { target: { value: 'Engineer' } });
    fireEvent.change(screen.getByLabelText('Resume text'), { target: { value: 'Resume text' } });
    fireEvent.click(screen.getByRole('button', { name: 'Submit for ATS review' }));

    expect(screen.getByRole('heading', { name: 'ATS score: 100/100' })).toBeInTheDocument();
    expect(await screen.findByText('Resume submission failed. Please try again.')).toBeInTheDocument();
  });
});
