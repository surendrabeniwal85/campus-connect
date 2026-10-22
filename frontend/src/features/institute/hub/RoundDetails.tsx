import { EmptyState } from '../../../design-system/components';

import './hub.css';

export type PlacementRound = {
  name: 'Aptitude' | 'Technical' | 'Managerial' | 'HR';
  progress?: number;
  status?: string;
};

const roundNames: PlacementRound['name'][] = [
  'Aptitude',
  'Technical',
  'Managerial',
  'HR',
];

export default function RoundDetails({
  rounds,
}: {
  rounds?: PlacementRound[];
}) {
  if (!rounds) {
    return (
      <section aria-labelledby="round-details-title">
        <h2 id="round-details-title">Placement rounds</h2>
        <EmptyState title="Round progress unavailable">
          Your placement-round status will appear when the placement service is connected.
        </EmptyState>
      </section>
    );
  }

  const roundByName = new Map(rounds.map((round) => [round.name, round]));

  return (
    <section className="hub-rounds" aria-labelledby="round-details-title">
      <h2 id="round-details-title">Placement rounds</h2>
      <ol className="hub-rounds__list">
        {roundNames.map((name) => {
          const round = roundByName.get(name);
          const progress = round?.progress === undefined
            ? undefined
            : Math.max(0, Math.min(100, round.progress));

          return (
            <li className="hub-rounds__item" key={name}>
              <h3>{name}</h3>
              {round ? (
                <>
                  <p>Status: {round.status || 'Status not provided'}</p>
                  {progress !== undefined && (
                    <div
                      className="hub-rounds__progress"
                      role="progressbar"
                      aria-label={`${name} progress`}
                      aria-valuemin={0}
                      aria-valuemax={100}
                      aria-valuenow={progress}
                    >
                      <span style={{ width: `${progress}%` }} />
                    </div>
                  )}
                </>
              ) : (
                <p>Progress not provided</p>
              )}
            </li>
          );
        })}
      </ol>
    </section>
  );
}
