import { Card, EmptyState } from '../../../design-system/components';
import type { DashboardTile } from './StudentDashboard';

import './hub.css';

export type StaffDashboardRole = 'TEACHER' | 'HOD';

export default function StaffDashboard({
  role,
  tiles,
  onOpenTile,
}: {
  role: StaffDashboardRole;
  tiles: DashboardTile[];
  onOpenTile?: (tile: DashboardTile) => void;
}) {
  const roleLabel = role === 'HOD' ? 'HOD' : 'Teacher';

  return (
    <section className="hub-dashboard" aria-labelledby="staff-dashboard-title">
      <h1 id="staff-dashboard-title">{roleLabel} dashboard</h1>
      {tiles.length === 0 ? (
        <EmptyState title="Dashboard details unavailable">
          {roleLabel} dashboard information will appear when the relevant institute services are connected.
        </EmptyState>
      ) : (
        <div className="hub-dashboard__tiles">
          {tiles.map((tile) => (
            <Card key={tile.id} className="hub-dashboard__tile">
              <h2>{tile.title}</h2>
              {tile.value && <p className="hub-dashboard__value">{tile.value}</p>}
              <p>{tile.description}</p>
              {onOpenTile && (
                <button type="button" onClick={() => onOpenTile(tile)}>
                  Open {tile.title}
                </button>
              )}
            </Card>
          ))}
        </div>
      )}
    </section>
  );
}
