import { useEffect, useState } from "react";
import { classroomApi } from "./classroomApi";
import type { CrtSession } from "./types";
import { PageState } from "./components/PageState";

export default function CrtPage() {
  const [sessions, setSessions] = useState<CrtSession[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const load = async () => {
    setLoading(true); setError("");
    try { setSessions(await classroomApi.crtSessions()); }
    catch (e) { setError(e instanceof Error ? e.message : "Could not load CRT sessions."); }
    finally { setLoading(false); }
  };
  useEffect(() => { void load(); }, []);
  return <main className="classroom-page">
    <h1>CRT Sessions & Attendance</h1>
    <p>CRT participation is tracked separately from class attendance.</p>
    <PageState loading={loading} error={error} empty={!loading && !error && sessions.length === 0} onRetry={load} />
    {!loading && !error && sessions.length > 0 && <div className="classroom-list">
      {sessions.map(s => <article className="classroom-card" key={s.id}>
        <h2>{s.title}</h2><p>Date: {s.sessionDate}</p><p>Trainer: {s.trainer}</p>
        <p>Target: {s.targetBranch}, semester {s.semester}</p>
      </article>)}
    </div>}
  </main>;
}
