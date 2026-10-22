import React, { useState } from 'react';

interface EvaluatorFormProps {
  roundId: number;
}

export default function EvaluatorForm({ roundId }: EvaluatorFormProps) {
  const [studentId, setStudentId] = useState<string>('');
  const [score, setScore] = useState<string>('');
  const [comments, setComments] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await fetch(`/api/v1/rounds/${roundId}/evaluations`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ studentId: Number(studentId), score: Number(score), comments }),
      });
      if (res.ok) {
        alert('Evaluation saved successfully!');
        setStudentId('');
        setScore('');
        setComments('');
      } else {
        alert('Failed to save evaluation');
      }
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="data-panel" style={{ maxWidth: '600px', margin: '0 auto' }}>
      <div className="panel-heading">
        <h3>Evaluator Scoring Form</h3>
        <span className="pill">Scoring Module</span>
      </div>
      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '16px' }}>
        <div>
          <label style={{ display: 'block', fontSize: '11px', fontWeight: '700', color: '#526574', marginBottom: '6px' }}>Student ID</label>
          <input 
            type="number" 
            value={studentId} 
            onChange={(e) => setStudentId(e.target.value)} 
            required 
            style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #d8e5df', fontSize: '12px' }}
          />
        </div>
        <div>
          <label style={{ display: 'block', fontSize: '11px', fontWeight: '700', color: '#526574', marginBottom: '6px' }}>Score (out of 100)</label>
          <input 
            type="number" 
            value={score} 
            onChange={(e) => setScore(e.target.value)} 
            required 
            style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #d8e5df', fontSize: '12px' }}
          />
        </div>
        <div>
          <label style={{ display: 'block', fontSize: '11px', fontWeight: '700', color: '#526574', marginBottom: '6px' }}>Comments</label>
          <textarea 
            value={comments} 
            onChange={(e) => setComments(e.target.value)} 
            rows={3}
            style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #d8e5df', fontSize: '12px', resize: 'vertical' }}
            placeholder="Add reviewer comments..."
          ></textarea>
        </div>
        <button 
          type="submit" 
          style={{ background: '#10283b', color: '#fff', border: '0', padding: '12px', borderRadius: '8px', fontSize: '12px', fontWeight: '600', cursor: 'pointer' }}
        >
          Submit Evaluation
        </button>
      </form>
    </div>
  );
}