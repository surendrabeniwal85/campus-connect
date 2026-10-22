import React, { useState, useEffect } from 'react';

interface LiveUpdate {
  id: number;
  stage: string;
  countsJson: string;
  note: string;
  createdAt: string;
}

export default function LiveBoard({ driveId }: { driveId: number }) {
  const [updates, setUpdates] = useState<LiveUpdate[]>([]);
  const [newNote, setNewNote] = useState('');
  const [stage, setStage] = useState('Round 1 Progress');

  useEffect(() => {
    fetchUpdates();
  }, [driveId]);

  const fetchUpdates = async () => {
    try {
      const res = await fetch(`/api/v1/drives/${driveId}/live`);
      if (res.ok) {
        const data = await res.json();
        setUpdates(data);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handlePostUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await fetch(`/api/v1/drives/${driveId}/live`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ stage, note: newNote, countsJson: '{"registered": 50, "cleared": 30}' })
      });
      if (res.ok) {
        setNewNote('');
        fetchUpdates();
      }
    } catch (err) {
      alert('Failed to post live update');
    }
  };

  return (
    <div className="data-panel">
      <div className="panel-heading">
        <h3>Live Drive Board & Funnel Updates</h3>
        <span className="pill">Real-time Stream</span>
      </div>
      
      <form onSubmit={handlePostUpdate} style={{ marginBottom: '24px', background: '#f7f9fa', padding: '18px', borderRadius: '14px', border: '1px solid #e6ebef' }}>
        <h4 style={{ margin: '0 0 12px', fontSize: '14px', fontWeight: '700' }}>Post Live Session Update</h4>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '12px', marginBottom: '12px' }}>
          <input 
            type="text" 
            value={stage} 
            onChange={e => setStage(e.target.value)} 
            placeholder="Stage Name (e.g. Round 1)"
            style={{ padding: '10px 12px', borderRadius: '8px', border: '1px solid #d8e5df', fontSize: '12px', width: '100%' }}
            required
          />
        </div>
        <textarea 
          value={newNote} 
          onChange={e => setNewNote(e.target.value)} 
          placeholder="Session Note or Status update..."
          style={{ padding: '10px 12px', borderRadius: '8px', border: '1px solid #d8e5df', fontSize: '12px', width: '100%', marginBottom: '12px', resize: 'vertical' }}
          rows={2}
          required
        ></textarea>
        <button type="submit" style={{ background: '#10283b', color: '#fff', border: '0', padding: '10px 18px', borderRadius: '8px', fontSize: '12px', fontWeight: '600', cursor: 'pointer' }}>
          Publish Update
        </button>
      </form>

      <div style={{ display: 'grid', gap: '12px' }}>
        <h4 style={{ margin: '0', fontSize: '14px', fontWeight: '700' }}>Timeline Feed</h4>
        {updates.length === 0 ? (
          <p className="muted" style={{ margin: '0' }}>No live updates posted yet for this drive.</p>
        ) : (
          updates.map(u => (
            <div key={u.id} style={{ borderLeft: '4px solid #66b493', paddingLeft: '14px', paddingBottom: '4px' }}>
              <span style={{ fontSize: '10px', fontWeight: '700', color: '#3b7665', textTransform: 'uppercase' }}>{u.stage}</span>
              <p style={{ margin: '4px 0', fontSize: '13px', color: '#172a3a', fontWeight: '500' }}>{u.note}</p>
              <small style={{ color: '#8392a0', fontSize: '10px' }}>{new Date(u.createdAt).toLocaleString()}</small>
            </div>
          ))
        )}
      </div>
    </div>
  );
}