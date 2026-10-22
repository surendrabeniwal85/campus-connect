import React, { useState, useEffect } from 'react';
import { placementService } from './placementService';
import type { PlacementDrive, EligibilityResultDto } from './placementService';

interface PlacementDashboardProps {
  studentId: number;
}

export default function PlacementDashboard({ studentId }: PlacementDashboardProps) {
  const [drives, setDrives] = useState<PlacementDrive[]>([]);
  const [eligibilityMap, setEligibilityMap] = useState<Record<number, EligibilityResultDto>>({});
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    loadDrives();
  }, []);

  const loadDrives = async () => {
    try {
      const data = await placementService.getDrives();
      setDrives(data);
      data.forEach(async (drive) => {
        try {
          const result = await placementService.checkEligibility(drive.id, studentId);
          setEligibilityMap(prev => ({ ...prev, [drive.id]: result }));
        } catch (err) {
          console.error(`Error checking eligibility for drive ${drive.id}`, err);
        }
      });
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleApply = async (driveId: number) => {
    try {
      await placementService.applyToDrive(driveId, studentId);
      alert('Successfully applied to the drive!');
    } catch (err: any) {
      alert(err.message);
    }
  };

  if (loading) return <div style={{ padding: '20px', color: '#718393', fontSize: '13px' }}>Loading placement drives...</div>;

  return (
    <div>
      <div style={{ display: 'grid', gap: '16px' }}>
        {drives.map(drive => {
          const eligibility = eligibilityMap[drive.id];
          return (
            <div key={drive.id} className="data-panel">
              <div className="panel-heading">
                <div>
                  <span className="eyebrow">{drive.company?.name || 'Company Drive'}</span>
                  <h3 style={{ margin: '4px 0 0' }}>{drive.role}</h3>
                </div>
                <span className="pill">{drive.packageLpa ? `${drive.packageLpa} LPA` : 'Not Disclosed'}</span>
              </div>
              <p style={{ color: '#596e7e', fontSize: '12px', margin: '0 0 14px' }}>
                Deadline: {new Date(drive.deadline).toLocaleString()}
              </p>
              
              <div style={{ borderTop: '1px solid #edf0f2', paddingTop: '14px', marginTop: '14px' }}>
                <h4 style={{ fontSize: '12px', fontWeight: '700', margin: '0 0 8px', color: '#172a3a' }}>Explainable Eligibility Status:</h4>
                {eligibility ? (
                  <div>
                    <span className={`status ${eligibility.overallEligible ? 'status-done' : 'status-current'}`}>
                      {eligibility.overallEligible ? 'Eligible' : 'Not Eligible'}
                    </span>
                    <div className="table-wrap" style={{ marginTop: '10px' }}>
                      <table>
                        <thead>
                          <tr>
                            <th>Rule Name</th>
                            <th>Status</th>
                            <th>Reason</th>
                          </tr>
                        </thead>
                        <tbody>
                          {eligibility.evaluations?.map((evaluation, idx) => (
                            <tr key={idx}>
                              <td><strong>{evaluation.ruleName}</strong></td>
                              <td><span className={`status ${evaluation.status === 'PASS' ? 'status-done' : 'status-current'}`}>{evaluation.status}</span></td>
                              <td>{evaluation.reason}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                ) : (
                  <p style={{ fontSize: '11px', color: '#8392a0' }}>Evaluating eligibility rules...</p>
                )}
              </div>

              {eligibility?.overallEligible && (
                <button 
                  onClick={() => handleApply(drive.id)}
                  style={{ marginTop: '16px', background: '#3b7665', color: '#fff', border: '0', padding: '10px 16px', borderRadius: '8px', fontSize: '11px', fontWeight: '700', cursor: 'pointer' }}>
                  Apply Now →
                </button>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}