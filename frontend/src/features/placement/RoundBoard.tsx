import React, { useState, useEffect } from 'react';

interface Candidate {
  id: number;
  studentId: number;
  status: string;
}

interface RoundBoardProps {
  driveId: number;
  roundId: number;
}

export default function RoundBoard({ driveId, roundId }: RoundBoardProps) {
  const [candidates, setCandidates] = useState<Candidate[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetchCandidates();
  }, [driveId, roundId]);

  const fetchCandidates = async () => {
    try {
      const res = await fetch(`/api/v1/drives/${driveId}/rounds/${roundId}/candidates`);
      if (res.ok) {
        const data = await res.json();
        setCandidates(data);
      }
    } catch (err) {
      console.error('Failed to fetch candidates for round', err);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (candidateId: number, action: string) => {
    try {
      const res = await fetch(`/api/v1/rounds/${roundId}/${action}?candidateId=${candidateId}`, {
        method: 'POST',
      });
      if (res.status === 409) {
        alert('Invalid status transition!');
        return;
      }
      if (res.ok) {
        fetchCandidates();
      }
    } catch (err) {
      alert('Error updating candidate status');
    }
  };

  if (loading) return <div style={{ padding: '20px', color: '#718393', fontSize: '13px' }}>Loading round board...</div>;

  return (
    <div className="data-panel">
      <div className="panel-heading">
        <h3>TPO Round Management Board</h3>
        <span className="pill">Stage Control</span>
      </div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Student ID</th>
              <th>Status</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {candidates.map(candidate => (
              <tr key={candidate.id}>
                <td><strong>{candidate.studentId}</strong></td>
                <td>
                  <span className="status status-current">
                    {candidate.status}
                  </span>
                </td>
                <td style={{ textAlign: 'right' }}>
                  <button onClick={() => handleStatusChange(candidate.id, 'shortlist')} style={{ background: 'transparent', border: '0', color: '#2b7657', fontWeight: '700', fontSize: '11px', cursor: 'pointer', marginRight: '10px' }}>Shortlist</button>
                  <button onClick={() => handleStatusChange(candidate.id, 'advance')} style={{ background: 'transparent', border: '0', color: '#3b7665', fontWeight: '700', fontSize: '11px', cursor: 'pointer', marginRight: '10px' }}>Advance</button>
                  <button onClick={() => handleStatusChange(candidate.id, 'eliminate')} style={{ background: 'transparent', border: '0', color: '#a94442', fontWeight: '700', fontSize: '11px', cursor: 'pointer' }}>Eliminate</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}