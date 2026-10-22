import React, { useState, useEffect } from 'react';

interface CriteriaCheck {
  criterionName: string;
  message: string;
  status: 'PASS' | 'NEEDS_REVIEW' | 'MISSING';
}

interface ReadinessData {
  criteriaChecks: CriteriaCheck[];
  nextActions: string[];
}

interface ReadinessCardProps {
  studentId?: number;
  driveId?: number;
}

export default function ReadinessCard({ studentId, driveId }: ReadinessCardProps) {
  const [readiness, setReadiness] = useState<ReadinessData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetchReadiness();
  }, [studentId, driveId]);

  const fetchReadiness = async () => {
    try {
      const endpoint = driveId 
        ? `/api/v1/drives/${driveId}/readiness-check` 
        : `/api/v1/students/me/readiness`;
      
      const res = await fetch(endpoint);
      if (res.ok) {
        const data = await res.json();
        setReadiness(data);
      }
    } catch (err) {
      console.error('Failed to fetch readiness checklist', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div style={{ padding: '20px', color: '#718393', fontSize: '13px' }}>Loading readiness profile...</div>;
  if (!readiness) return <div style={{ padding: '20px', color: '#718393', fontSize: '13px' }}>No readiness data available.</div>;

  return (
    <div className="data-panel" style={{ maxWidth: '700px', margin: '0 auto' }}>
      <div className="panel-heading">
        <h3>Placement Readiness Checklist</h3>
        <span className="pill">Readiness Profile</span>
      </div>
      
      <div style={{ display: 'grid', gap: '12px' }}>
        {readiness.criteriaChecks?.map((check, idx) => (
          <div key={idx} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingBottom: '10px', borderBottom: '1px solid #edf0f2' }}>
            <div>
              <strong style={{ fontSize: '12px', color: '#172a3a', display: 'block', marginBottom: '2px' }}>{check.criterionName}</strong>
              <span style={{ fontSize: '11px', color: '#718393' }}>{check.message}</span>
            </div>
            <span className={`status ${check.status === 'PASS' ? 'status-done' : 'status-current'}`}>
              {check.status}
            </span>
          </div>
        ))}
      </div>

      <div style={{ marginTop: '20px', background: '#f7f9fa', padding: '16px', borderRadius: '12px', border: '1px solid #e6ebef' }}>
        <h4 style={{ fontSize: '12px', fontWeight: '700', margin: '0 0 8px', color: '#172a3a' }}>Recommended Next Actions:</h4>
        <ul style={{ margin: '0', paddingLeft: '16px', fontSize: '11px', color: '#526574', lineHeight: '1.6' }}>
          {readiness.nextActions?.map((action, idx) => (
            <li key={idx} style={{ marginBottom: '4px' }}>{action}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}