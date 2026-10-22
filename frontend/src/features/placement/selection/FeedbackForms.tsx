import React, { useState } from 'react';

export default function FeedbackForms({ driveId }: { driveId: number }) {
  const [source, setSource] = useState('STUDENT');
  const [rating, setRating] = useState('5');
  const [comment, setComment] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await fetch('/api/v1/feedback', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ driveId, source, rating: Number(rating), comment })
      });
      if (res.ok) {
        alert('Feedback submitted successfully!');
        setComment('');
      }
    } catch (err) {
      alert('Error submitting feedback');
    }
  };

  return (
    <div className="data-panel" style={{ maxWidth: '600px', margin: '0 auto' }}>
      <div className="panel-heading">
        <h3>Drive Feedback & Ratings</h3>
        <span className="pill">Multi-Source</span>
      </div>
      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '16px' }}>
        <div>
          <label style={{ display: 'block', fontSize: '11px', fontWeight: '700', color: '#526574', marginBottom: '6px' }}>Feedback Source</label>
          <select 
            value={source} 
            onChange={e => setSource(e.target.value)}
            style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #d8e5df', fontSize: '12px', background: '#fff' }}
          >
            <option value="STUDENT">Student</option>
            <option value="TPO">TPO</option>
            <option value="COMPANY">Company</option>
          </select>
        </div>
        <div>
          <label style={{ display: 'block', fontSize: '11px', fontWeight: '700', color: '#526574', marginBottom: '6px' }}>Rating (1 to 5)</label>
          <input 
            type="number" 
            min="1" 
            max="5" 
            value={rating} 
            onChange={e => setRating(e.target.value)}
            style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #d8e5df', fontSize: '12px' }}
            required
          />
        </div>
        <div>
          <label style={{ display: 'block', fontSize: '11px', fontWeight: '700', color: '#526574', marginBottom: '6px' }}>Comments</label>
          <textarea 
            value={comment} 
            onChange={e => setComment(e.target.value)}
            rows={3}
            style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #d8e5df', fontSize: '12px', resize: 'vertical' }}
            placeholder="Share your experience..."
          ></textarea>
        </div>
        <button type="submit" style={{ background: '#3b7665', color: '#fff', border: '0', padding: '12px', borderRadius: '8px', fontSize: '12px', fontWeight: '600', cursor: 'pointer' }}>
          Submit Feedback
        </button>
      </form>
    </div>
  );
}