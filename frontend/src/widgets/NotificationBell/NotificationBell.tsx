import React, { useState, useEffect } from 'react';

interface NotificationItem {
  id: number;
  title: string;
  body: string;
  link?: string;
  createdAt: string;
}

export default function NotificationBell({ userId }: { userId: number }) {
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    const eventSource = new EventSource(`/api/v1/notifications/stream?userId=${userId}`);
    
    eventSource.addEventListener('notification', (event) => {
      const newNotif = JSON.parse(event.data);
      setNotifications(prev => [newNotif, ...prev]);
      setUnreadCount(count => count + 1);
    });

    fetch('/api/v1/notifications')
      .then(res => res.json())
      .then(data => setNotifications(data))
      .catch(err => console.error('Failed to load notifications', err));

    return () => {
      eventSource.close();
    };
  }, [userId]);

  return (
    <div style={{ position: 'relative', display: 'inline-block' }}>
      <button 
        onClick={() => { setIsOpen(!isOpen); setUnreadCount(0); }}
        style={{ background: 'transparent', border: '0', cursor: 'pointer', fontSize: '18px', padding: '6px', position: 'relative' }}
      >
        🔔
        {unreadCount > 0 && (
          <span style={{ position: 'absolute', top: '0', right: '0', background: '#e53e3e', color: '#fff', fontSize: '9px', fontWeight: '700', padding: '2px 5px', borderRadius: '99px' }}>
            {unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div style={{ position: 'absolute', right: '0', marginTop: '10px', width: '300px', background: '#fff', borderRadius: '14px', boxShadow: '0 12px 28px rgba(24,53,74,0.15)', border: '1px solid #e6ebef', zIndex: 100, overflow: 'hidden' }}>
          <div style={{ padding: '14px 16px', borderBottom: '1px solid #edf0f2', fontWeight: '700', fontSize: '12px', color: '#172a3a' }}>Notifications & Updates</div>
          <div style={{ maxHeight: '250px', overflowY: 'auto' }}>
            {notifications.length === 0 ? (
              <div style={{ padding: '20px', fontSize: '11px', color: '#8392a0', textAlign: 'center' }}>No notifications yet</div>
            ) : (
              notifications.map((n, idx) => (
                <div key={idx} style={{ padding: '12px 16px', borderBottom: '1px solid #f7f9fa', fontSize: '11px' }}>
                  <p style={{ fontWeight: '700', color: '#172a3a', margin: '0 0 3px' }}>{n.title}</p>
                  <p style={{ color: '#526574', margin: '0' }}>{n.body}</p>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}