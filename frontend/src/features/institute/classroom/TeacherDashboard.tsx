import { useEffect, useState } from "react";
import { classroomApi } from "./classroomApi";
import type { ClassSession } from "./types";
import { PageState } from "./components/PageState";

export default function TeacherDashboard() {
  const [sessions, setSessions] = useState<ClassSession[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const load = async () => {
    setLoading(true); setError("");
    try { setSessions(await classroomApi.sessions(new Date().toISOString().slice(0, 10))); }
    catch (e) { setError(e instanceof Error ? e.message : "Could not load sessions."); }
    finally { setLoading(false); }
  };
  useEffect(() => { void load(); }, []);
  return <main className="classroom-page">
    <h1>Teacher Dashboard</h1>
    <p>Today's class sessions and attendance tasks.</p>
    <PageState loading={loading} error={error} empty={!loading && !error && sessions.length === 0} onRetry={load} />
    {!loading && !error && sessions.length > 0 && <div className="classroom-list">
      {sessions.map(s => <article className="classroom-card" key={s.id}>
        <h2>{s.subject}</h2><p>Section: {s.section}</p><p>Date: {s.sessionDate}</p>
        <p>{s.attendanceLocked ? "Attendance locked" : "Attendance open"}</p>
      </article>)}
    </div>}
  </main>;
}
