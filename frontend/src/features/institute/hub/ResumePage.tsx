import { useState, type FormEvent } from 'react';

import {
  Button,
  Card,
  EmptyState,
  ErrorState,
  LoadingState,
} from '../../../design-system/components';

import './hub.css';

export type ResumeAnalysisView = {
  score: number;
  status: 'NEEDS_WORK' | 'GOOD' | 'STRONG';
  breakdown: { category: string; points: number; maximum: number; tip: string }[];
};

export default function ResumePage({
  analysis,
  isSubmitting = false,
  error,
  onSubmitText,
}: {
  analysis?: ResumeAnalysisView;
  isSubmitting?: boolean;
  error?: string;
  onSubmitText: (text: string, targetRole: string) => Promise<void> | void;
}) {
  const [resumeText, setResumeText] = useState('');
  const [targetRole, setTargetRole] = useState('');
  const [validationError, setValidationError] = useState('');
  const [submissionError, setSubmissionError] = useState('');
  const [localSubmitting, setLocalSubmitting] = useState(false);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!resumeText.trim() || !targetRole.trim()) {
      setValidationError('Enter resume text and a target role.');
      return;
    }
    setValidationError('');
    setSubmissionError('');
    setLocalSubmitting(true);
    try {
      await onSubmitText(resumeText, targetRole.trim());
    } catch {
      setSubmissionError('Resume submission failed. Please try again.');
    } finally {
      setLocalSubmitting(false);
    }
  }

  const submitting = isSubmitting || localSubmitting;

  return (
    <section className="hub-resume" aria-labelledby="resume-title">
      <p className="eyebrow">PLACEMENT PREPARATION</p>
      <h1 id="resume-title">Resume and ATS review</h1>
      <p>Paste resume text for analysis. Your text is not displayed in summary views.</p>

      <Card>
        <form className="hub-resume__form" onSubmit={submit}>
          <label htmlFor="resume-target-role">Target role</label>
          <input
            id="resume-target-role"
            value={targetRole}
            onChange={(event) => setTargetRole(event.target.value)}
            required
            maxLength={120}
          />
          <label htmlFor="resume-text">Resume text</label>
          <textarea
            id="resume-text"
            value={resumeText}
            onChange={(event) => setResumeText(event.target.value)}
            required
            rows={10}
            maxLength={50000}
          />
          {validationError && <p className="hub-resume__error" role="alert">{validationError}</p>}
          {(error || submissionError) && (
            <ErrorState message={error || submissionError} />
          )}
          <Button type="submit" disabled={submitting}>
            {submitting ? 'Submitting…' : 'Submit for ATS review'}
          </Button>
        </form>
      </Card>

      {submitting && <LoadingState label="Analyzing resume" />}
      {analysis && (
        <Card className="hub-resume__analysis">
          <h2>ATS score: {Math.max(0, Math.min(100, analysis.score))}/100</h2>
          <p>Status: {analysis.status.replaceAll('_', ' ')}</p>
          <ul>
            {analysis.breakdown.map((item) => (
              <li key={item.category}>
                <strong>{item.category}: {item.points}/{item.maximum}</strong>
                <p>{item.tip}</p>
              </li>
            ))}
          </ul>
        </Card>
      )}
      {!analysis && !submitting && (
        <EmptyState title="No resume analysis yet">
          Submit pasted resume text to view your score breakdown and tips.
        </EmptyState>
      )}
    </section>
  );
}
