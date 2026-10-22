import { useState, useEffect } from 'react';
import type {
  DriveDto,
  OneRuleAwayResponseDto,
  OneRuleAwayCandidateDto,
  NudgeResponseDto,
} from '../auth/authApi';
import {
  fetchUpcomingDrives,
  fetchOneRuleAwaySummary,
  sendTargetedNudge,
} from '../auth/authApi';

interface OfferPathTpoConsoleProps {
  readonly token: string;
}

export default function OfferPathTpoConsole({ token }: OfferPathTpoConsoleProps) {
  const [drives, setDrives] = useState<DriveDto[]>([]);
  const [loadingDrives, setLoadingDrives] = useState(false);
  const [selectedDriveId, setSelectedDriveId] = useState<number | null>(null);

  // One-rule-away summary state
  const [summary, setSummary] = useState<OneRuleAwayResponseDto | null>(null);
  const [loadingSummary, setLoadingSummary] = useState(false);

  // Selected candidates for nudge (multi-select)
  const [selectedCandidates, setSelectedCandidates] = useState<string[]>([]);

  // Nudge modal & feedback state
  const [showNudgeModal, setShowNudgeModal] = useState(false);
  const [customNudgeMessage, setCustomNudgeMessage] = useState('');
  const [confirmDispatchChecked, setConfirmDispatchChecked] = useState(false);
  const [sendingNudge, setSendingNudge] = useState(false);
  const [nudgeResult, setNudgeResult] = useState<NudgeResponseDto | null>(null);

  // Load drives
  useEffect(() => {
    setLoadingDrives(true);
    fetchUpcomingDrives()
      .then((res) => {
        setDrives(res.content);
        if (res.content.length > 0 && selectedDriveId === null) {
          setSelectedDriveId(res.content[0].id);
        }
      })
      .catch(() => {})
      .finally(() => setLoadingDrives(false));
  }, []);

  // Fetch One-Rule-Away metrics for selected drive
  useEffect(() => {
    if (!token || !selectedDriveId) return;
    setLoadingSummary(true);
    setNudgeResult(null);

    fetchOneRuleAwaySummary(selectedDriveId, token)
      .then((data) => {
        setSummary(data);
        // By default select all candidates
        setSelectedCandidates(data.candidates.map((c) => c.rollNumber));
      })
      .catch(() => {})
      .finally(() => setLoadingSummary(false));
  }, [token, selectedDriveId]);

  const activeDrive = drives.find((d) => d.id === selectedDriveId) ?? drives[0];

  const handleToggleSelectAll = () => {
    if (!summary) return;
    if (selectedCandidates.length === summary.candidates.length) {
      setSelectedCandidates([]);
    } else {
      setSelectedCandidates(summary.candidates.map((c) => c.rollNumber));
    }
  };

  const handleToggleCandidate = (roll: string) => {
    setSelectedCandidates((prev) =>
      prev.includes(roll) ? prev.filter((r) => r !== roll) : [...prev, roll]
    );
  };

  const handleOpenNudgeModal = () => {
    if (!activeDrive) return;
    setCustomNudgeMessage(
      `Special Preparation Alert: You are currently 1 eligibility criterion away from qualifying for the ${activeDrive.companyName} (${activeDrive.roleTitle}) placement drive. Please review your Offer Path checklist in the portal and connect with your faculty mentor.`
    );
    setConfirmDispatchChecked(false);
    setShowNudgeModal(true);
  };

  const handleDispatchNudge = async () => {
    if (!token || !selectedDriveId) return;
    setSendingNudge(true);
    try {
      const res = await sendTargetedNudge(selectedDriveId, token, customNudgeMessage);
      setNudgeResult(res);
      setShowNudgeModal(false);
    } catch (err: unknown) {
      alert(err instanceof Error ? err.message : 'Failed to dispatch nudge.');
    } finally {
      setSendingNudge(false);
    }
  };

  return (
    <div className="tpo-offer-path-console" style={{ display: 'grid', gap: '24px' }}>

      {/* Header Banner */}
      <section
        className="pa-card"
        style={{
          padding: '24px 28px',
          background: 'linear-gradient(135deg, rgba(15, 118, 110, 0.08) 0%, rgba(55, 48, 163, 0.08) 100%)',
          borderColor: 'rgba(15, 118, 110, 0.25)',
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
          <div>
            <span
              style={{
                fontSize: '11px',
                fontWeight: 700,
                color: 'var(--pa-color-teal)',
                letterSpacing: '0.12em',
                textTransform: 'uppercase',
              }}
            >
              TPO & Recruiter Operations Console
            </span>
            <h2 className="pa-display-heading" style={{ margin: '4px 0 6px', fontSize: '24px' }}>
              Offer Path: One-Rule-Away Cohort Analytics
            </h2>
            <p style={{ margin: 0, color: 'var(--pa-color-muted)', fontSize: '14px', maxWidth: '750px' }}>
              Detect students who are failing exactly <strong style={{ color: 'var(--pa-color-text)' }}>one mandatory criteria</strong>.
              Dispatch targeted preparation guidance and CRT nudges before drive deadlines close.
            </p>
          </div>

          {/* Drive Selector Dropdown */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
            <label htmlFor="tpo-drive-selector" style={{ fontSize: '12px', fontWeight: 700, color: 'var(--pa-color-muted)' }}>
              Select Active Recruitment Drive:
            </label>
            <select
              id="tpo-drive-selector"
              value={selectedDriveId ?? ''}
              onChange={(e) => setSelectedDriveId(Number(e.target.value))}
              style={{
                padding: '8px 14px',
                borderRadius: '8px',
                border: '1px solid var(--pa-color-border)',
                background: 'var(--pa-color-surface)',
                color: 'var(--pa-color-text)',
                fontWeight: 600,
                fontSize: '13px',
                cursor: 'pointer',
              }}
            >
              {loadingDrives ? (
                <option>Loading drives…</option>
              ) : (
                drives.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.companyName} — {d.roleTitle} ({d.ctcLpa ? `${d.ctcLpa} LPA` : 'Competitive'})
                  </option>
                ))
              )}
            </select>
          </div>
        </div>
      </section>

      {/* Nudge Feedback Alert */}
      {nudgeResult && (
        <div
          style={{
            padding: '14px 18px',
            borderRadius: '10px',
            background: 'rgba(16, 185, 129, 0.12)',
            border: '1px solid #10b981',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          <div>
            <strong style={{ color: '#10b981' }}>✓ Targeted Nudge Dispatched Successfully!</strong>
            <div style={{ fontSize: '13px', color: 'var(--pa-color-text)', marginTop: '2px' }}>
              {nudgeResult.message} ({nudgeResult.sentCount} students notified for {nudgeResult.driveTitle}).
            </div>
          </div>
          <button
            type="button"
            className="quick-cred-btn"
            onClick={() => setNudgeResult(null)}
            style={{ fontSize: '12px', padding: '4px 10px' }}
          >
            Dismiss
          </button>
        </div>
      )}

      {/* 5.4 KPI Metrics Cards */}
      <section
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
          gap: '16px',
        }}
      >
        <article className="pa-card" style={{ padding: '20px' }}>
          <span style={{ fontSize: '11px', fontWeight: 700, color: 'var(--pa-color-muted)', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
            Total Cohort Assessed
          </span>
          <div style={{ fontFamily: 'var(--pa-font-display)', fontSize: '32px', fontWeight: 700, margin: '6px 0 2px' }}>
            {loadingSummary ? '…' : summary?.totalAssessed ?? 0}
          </div>
          <small style={{ color: 'var(--pa-color-muted)', fontSize: '11px' }}>
            Authoritative registered student facts evaluated
          </small>
        </article>

        <article className="pa-card" style={{ padding: '20px' }}>
          <span style={{ fontSize: '11px', fontWeight: 700, color: '#10b981', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
            Currently Eligible
          </span>
          <div style={{ fontFamily: 'var(--pa-font-display)', fontSize: '32px', fontWeight: 700, color: '#10b981', margin: '6px 0 2px' }}>
            {loadingSummary ? '…' : summary?.eligibleCount ?? 0}
          </div>
          <small style={{ color: 'var(--pa-color-muted)', fontSize: '11px' }}>
            Meet 100% of required thresholds
          </small>
        </article>

        <article
          className="pa-card"
          style={{
            padding: '20px',
            borderColor: 'var(--pa-color-saffron)',
            background: 'rgba(217, 119, 6, 0.04)',
          }}
        >
          <span style={{ fontSize: '11px', fontWeight: 700, color: 'var(--pa-color-saffron)', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
            Exactly One Rule Away
          </span>
          <div style={{ fontFamily: 'var(--pa-font-display)', fontSize: '32px', fontWeight: 700, color: 'var(--pa-color-saffron)', margin: '6px 0 2px' }}>
            {loadingSummary ? '…' : summary?.oneRuleAwayCount ?? 0}
          </div>
          <small style={{ color: 'var(--pa-color-muted)', fontSize: '11px' }}>
            High-potential candidates needing 1 boost
          </small>
        </article>

        <article className="pa-card" style={{ padding: '20px' }}>
          <span style={{ fontSize: '11px', fontWeight: 700, color: '#ef4444', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
            Missing Required Data
          </span>
          <div style={{ fontFamily: 'var(--pa-font-display)', fontSize: '32px', fontWeight: 700, color: '#ef4444', margin: '6px 0 2px' }}>
            {loadingSummary ? '…' : summary?.missingDataCount ?? 0}
          </div>
          <small style={{ color: 'var(--pa-color-muted)', fontSize: '11px' }}>
            Incomplete profile/academic facts requiring update
          </small>
        </article>
      </section>

      {/* Unmet Rule Breakdown & Action Bar */}
      {summary && (
        <section className="pa-card" style={{ padding: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', flexWrap: 'wrap', gap: '12px' }}>
            <div>
              <h3 className="pa-display-heading" style={{ margin: 0, fontSize: '18px' }}>
                Unmet Requirements Distribution
              </h3>
              <small style={{ color: 'var(--pa-color-muted)' }}>
                Shows which specific criteria are blocking students from becoming eligible.
              </small>
            </div>

            {summary.candidates.length > 0 && (
              <button
                type="button"
                className="pa-button-primary"
                style={{ padding: '0 20px', display: 'flex', alignItems: 'center', gap: '8px' }}
                onClick={handleOpenNudgeModal}
                disabled={selectedCandidates.length === 0}
              >
                <span>📢 Dispatch Targeted Nudge ({selectedCandidates.length} Selected)</span>
              </button>
            )}
          </div>

          {/* Grouped Breakdown Bars */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '12px' }}>
            {Object.entries(summary.unmetRuleBreakdown).map(([ruleName, count]) => (
              <div
                key={ruleName}
                style={{
                  padding: '12px 16px',
                  borderRadius: '10px',
                  background: 'var(--pa-color-page)',
                  border: '1px solid var(--pa-color-border)',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                }}
              >
                <div>
                  <strong>{ruleName}</strong>
                  <div style={{ fontSize: '11px', color: 'var(--pa-color-muted)' }}>
                    Primary blocking criterion
                  </div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <span
                    style={{
                      padding: '3px 10px',
                      borderRadius: '999px',
                      background: 'rgba(217, 119, 6, 0.15)',
                      color: 'var(--pa-color-saffron)',
                      fontWeight: 700,
                      fontSize: '12px',
                    }}
                  >
                    {count} {count === 1 ? 'student' : 'students'}
                  </span>
                </div>
              </div>
            ))}
          </div>

          {/* Candidate Table */}
          <div style={{ marginTop: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
              <span style={{ fontSize: '13px', fontWeight: 700 }}>
                High-Potential Candidates ({summary.candidates.length}):
              </span>
              <button
                type="button"
                className="quick-cred-btn"
                onClick={handleToggleSelectAll}
                style={{ fontSize: '11px', padding: '3px 10px' }}
              >
                {selectedCandidates.length === summary.candidates.length ? 'Deselect All' : 'Select All'}
              </button>
            </div>

            <div className="data-table-wrap">
              <table className="data-table">
                <thead>
                  <tr>
                    <th style={{ width: '40px' }}>Select</th>
                    <th>Roll Number</th>
                    <th>Student Name</th>
                    <th>Department</th>
                    <th>Unmet Rule</th>
                    <th>Observed Value</th>
                    <th>Required Threshold</th>
                  </tr>
                </thead>
                <tbody>
                  {summary.candidates.length === 0 ? (
                    <tr>
                      <td colSpan={7} style={{ textAlign: 'center', color: 'var(--pa-color-muted)', padding: '24px' }}>
                        No students currently in the one-rule-away group for this drive.
                      </td>
                    </tr>
                  ) : (
                    summary.candidates.map((candidate: OneRuleAwayCandidateDto) => {
                      const isSelected = selectedCandidates.includes(candidate.rollNumber);
                      return (
                        <tr
                          key={candidate.rollNumber}
                          style={{
                            background: isSelected ? 'rgba(55, 48, 163, 0.04)' : undefined,
                          }}
                        >
                          <td>
                            <input
                              type="checkbox"
                              checked={isSelected}
                              onChange={() => handleToggleCandidate(candidate.rollNumber)}
                              aria-label={`Select ${candidate.fullName}`}
                            />
                          </td>
                          <td><strong>{candidate.rollNumber}</strong></td>
                          <td>{candidate.fullName}</td>
                          <td>{candidate.email}</td>
                          <td>
                            <span style={{ color: 'var(--pa-color-saffron)', fontWeight: 600 }}>
                              {candidate.unmetRule}
                            </span>
                          </td>
                          <td>
                            <span style={{ color: '#ef4444', fontWeight: 700 }}>
                              {candidate.actualValue}
                            </span>
                          </td>
                          <td>
                            <span style={{ color: '#10b981', fontWeight: 700 }}>
                              {candidate.requiredValue}
                            </span>
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </section>
      )}

      {/* =====================================================================
          5.5 TARGETED NUDGE CONFIRMATION MODAL
          ===================================================================== */}
      {showNudgeModal && activeDrive && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(0, 0, 0, 0.65)',
            backdropFilter: 'blur(4px)',
            display: 'grid',
            placeItems: 'center',
            padding: '20px',
            zIndex: 1000,
          }}
          onClick={() => setShowNudgeModal(false)}
        >
          <div
            className="pa-card"
            style={{
              maxWidth: '560px',
              width: '100%',
              padding: '28px',
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <div>
                <span style={{ fontSize: '11px', color: 'var(--pa-color-teal)', fontWeight: 700, textTransform: 'uppercase' }}>
                  Targeted Notification Dispatch
                </span>
                <h3 className="pa-display-heading" style={{ margin: '2px 0 0', fontSize: '20px' }}>
                  Confirm Preparation Alert
                </h3>
              </div>
              <button
                type="button"
                className="theme-toggle-btn"
                onClick={() => setShowNudgeModal(false)}
                aria-label="Close modal"
              >
                ✕
              </button>
            </div>

            <div style={{ display: 'grid', gap: '14px', fontSize: '13px' }}>
              <div style={{ padding: '10px 14px', borderRadius: '8px', background: 'var(--pa-color-page)', border: '1px solid var(--pa-color-border)' }}>
                <div><strong>Drive:</strong> {activeDrive.companyName} — {activeDrive.roleTitle}</div>
                <div><strong>Target Recipients:</strong> {selectedCandidates.length} high-potential students</div>
              </div>

              <div>
                <label htmlFor="nudge-message-textarea" style={{ fontWeight: 600, display: 'block', marginBottom: '6px' }}>
                  Personalized Nudge Message Preview:
                </label>
                <textarea
                  id="nudge-message-textarea"
                  rows={4}
                  value={customNudgeMessage}
                  onChange={(e) => setCustomNudgeMessage(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '10px',
                    borderRadius: '8px',
                    border: '1px solid var(--pa-color-border)',
                    background: 'var(--pa-color-surface)',
                    color: 'var(--pa-color-text)',
                    fontFamily: 'var(--pa-font-ui)',
                    fontSize: '13px',
                    resize: 'vertical',
                  }}
                />
                <small style={{ color: 'var(--pa-color-muted)' }}>
                  This alert will be delivered directly to the students&apos; Placement Assistant portal notification inbox.
                </small>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginTop: '6px' }}>
                <input
                  id="confirm-nudge-checkbox"
                  type="checkbox"
                  checked={confirmDispatchChecked}
                  onChange={(e) => setConfirmDispatchChecked(e.target.checked)}
                />
                <label htmlFor="confirm-nudge-checkbox" style={{ fontSize: '12px', cursor: 'pointer' }}>
                  I confirm dispatching this targeted preparation alert to {selectedCandidates.length} selected students.
                </label>
              </div>

              <div style={{ display: 'flex', gap: '12px', marginTop: '12px' }}>
                <button
                  type="button"
                  className="pa-button-primary"
                  style={{
                    flex: 1,
                    opacity: confirmDispatchChecked && !sendingNudge ? 1 : 0.6,
                    cursor: confirmDispatchChecked && !sendingNudge ? 'pointer' : 'not-allowed',
                  }}
                  disabled={!confirmDispatchChecked || sendingNudge}
                  onClick={handleDispatchNudge}
                >
                  {sendingNudge ? 'Dispatching Notifications…' : '🚀 Send Targeted Nudge Now'}
                </button>
                <button
                  type="button"
                  className="quick-cred-btn"
                  onClick={() => setShowNudgeModal(false)}
                  disabled={sendingNudge}
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
