import type { ReactNode } from 'react';

import { Card, EmptyState } from '../../../design-system/components';

import './hub.css';

export type DashboardTile = {
  id: string;
  title: string;
  description: string;
  value?: string;
};

export default function StudentDashboard({
  tiles,
  news,
  onOpenTile,
  roleLabel = 'Student',
}: {
  tiles: DashboardTile[];
  news: ReactNode;
  onOpenTile?: (tile: DashboardTile) => void;
  roleLabel?: string;
}) {
  return (
    <section className="hub-dashboard" aria-labelledby="student-dashboard-title">
      {news}
      <h1 id="student-dashboard-title">{roleLabel} dashboard</h1>
      {tiles.length === 0 ? (
        <EmptyState title="Dashboard details unavailable">
          Dashboard information will appear when your institute connects its services.
        </EmptyState>
      ) : (
        <div className="hub-dashboard__tiles">
          {tiles.map((tile) => (
            <Card key={tile.id} className="hub-dashboard__tile">
              <h2>{tile.title}</h2>
              {tile.value && <p className="hub-dashboard__value">{tile.value}</p>}
              <p>{tile.description}</p>
              {onOpenTile ? (
                <button type="button" onClick={() => onOpenTile(tile)}>Open {tile.title}</button>
              ) : null}
            </Card>
          ))}
        </div>
      )}
    </section>
  );
}
