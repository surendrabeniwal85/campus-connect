import { useEffect, useState } from "react";
import { classroomApi } from "./classroomApi";
import type { AttendanceSummary } from "./types";
import { PageState } from "./components/PageState";

export default function HodDashboard() {
  const [rows, setRows] = useState<AttendanceSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const load = async () => {
    setLoading(true); setError("");
    try { setRows(await classroomApi.hodDashboard()); }
    catch (e) { setError(e instanceof Error ? e.message : "Could not load HOD dashboard."); }
    finally { setLoading(false); }
  };
  useEffect(() => { void load(); }, []);
  return <main className="classroom-page">
    <h1>HOD Attendance Dashboard</h1>
    <p>Department attendance summary. Access must be restricted by the backend to the HOD's department.</p>
    <PageState loading={loading} error={error} empty={!loading && !error && rows.length === 0} onRetry={load} />
    {!loading && !error && rows.length > 0 && <div className="classroom-table-wrap">
      <table><thead><tr><th>Student</th><th>Subject</th><th>Sessions</th><th>Attendance</th><th>Shortage</th></tr></thead>
        <tbody>{rows.map((r, i) => <tr key={`${r.studentId ?? i}-${r.subject ?? ""}`}>
          <td>{r.studentName ?? `Student ${r.studentId ?? ""}`}</td><td>{r.subject ?? "Overall"}</td>
          <td>{r.totalSessions}</td><td>{r.percentage.toFixed(1)}%</td><td>{r.shortage ? "Yes" : "No"}</td>
        </tr>)}</tbody></table>
    </div>}
  </main>;
}
