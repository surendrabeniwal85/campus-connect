import { useEffect, useState } from "react";

import { drivesApi } from "./drivesApi";
import PageState from "./PageState";
import type { Drive } from "./types";

function money(drive: Drive): string {
  if (drive.employmentType === "FULL_TIME" && drive.ctcLpa != null) return `${drive.ctcLpa} LPA`;
  if (drive.stipendPerMonth != null) return `${drive.stipendPerMonth} per month`;
  return drive.packageText ?? "Not announced";
}

function when(value: string): string {
  return new Date(value).toLocaleString(undefined, { dateStyle: "medium", timeStyle: "short" });
}

export default function DriveList() {
  const [drives, setDrives] = useState<Drive[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    drivesApi
      .upcomingDrives()
      .then((page) => setDrives(page.content))
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  return (
    <section className="drives-page">
      <h2>Upcoming drives</h2>
      <PageState loading={loading} error={error} empty={drives.length === 0} emptyText="No upcoming drives yet.">
        <div className="drives-grid">
          {drives.map((drive) => (
            <article className="drive-tile" key={drive.id}>
              <p className="drive-company">{drive.companyName}</p>
              <h3>{drive.roleTitle}</h3>
              <p className="drive-money">{money(drive)}</p>
              <ul className="drive-facts">
                <li>Drive date: {drive.driveDate}</li>
                <li>Apply before: {when(drive.deadline)}</li>
                {drive.jobLocation && <li>Location: {drive.jobLocation}</li>}
                {drive.hasPpo && <li>PPO: {drive.ppoCtcLpa ? `${drive.ppoCtcLpa} LPA` : "yes"}</li>}
                {drive.bondMonths ? <li>Bond: {drive.bondMonths} months</li> : null}
              </ul>
            </article>
          ))}
        </div>
      </PageState>
    </section>
  );
}
