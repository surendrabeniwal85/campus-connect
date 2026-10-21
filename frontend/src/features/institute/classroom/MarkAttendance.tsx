import { useEffect, useState } from "react";
import { classroomApi } from "./classroomApi";
import type { AttendanceRow, AttendanceStatus, ClassSession } from "./types";
import { PageState } from "./components/PageState";

const demoStudents: AttendanceRow[] = [
  { studentId: 1, studentName: "Student One", rollNumber: "DEMO-001", status: "PRESENT" },
  { studentId: 2, studentName: "Student Two", rollNumber: "DEMO-002", status: "PRESENT" },
];

export default function MarkAttendance() {
  const [sessions, setSessions] = useState<ClassSession[]>([]);
  const [sessionId, setSessionId] = useState("");
  const [rows, setRows] = useState<AttendanceRow[]>(demoStudents);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const load = async () => {
    setLoading(true); setError("");
    try {
      const data = await classroomApi.sessions(new Date().toISOString().slice(0, 10));
      setSessions(data);
      if (data.length) setSessionId(String(data[0].id));
    } catch (e) { setError(e instanceof Error ? e.message : "Could not load sessions."); }
    finally { setLoading(false); }
  };
  useEffect(() => { void load(); }, []);
  const updateStatus = (studentId: number, status: AttendanceStatus) =>
    setRows(current => current.map(row => row.studentId === studentId ? { ...row, status } : row));
  const save = async () => {
    if (!sessionId) { setError("Select a class session first."); return; }
    setSaving(true); setError(""); setNotice("");
    try {
      await classroomApi.markAttendance(Number(sessionId), rows);
      setNotice("Attendance saved successfully.");
    } catch (e) { setError(e instanceof Error ? e.message : "Could not save attendance."); }
    finally { setSaving(false); }
  };
  return <main className="classroom-page">
    <h1>Mark Attendance</h1>
    <PageState loading={loading} error={error && !sessions.length ? error : ""} onRetry={load} />
    {!loading && <>
      <label className="classroom-field">Class session
        <select value={sessionId} onChange={e => setSessionId(e.target.value)}>
          <option value="">Select session</option>
          {sessions.map(s => <option value={s.id} key={s.id}>{s.subject} — {s.section} — {s.sessionDate}</option>)}
        </select>
      </label>
      {sessions.length === 0 && <p role="status">No sessions are available. Create a session before marking attendance.</p>}
      <div className="classroom-table-wrap"><table><thead><tr><th>Roll number</th><th>Student</th><th>Status</th></tr></thead>
        <tbody>{rows.map(row => <tr key={row.studentId}><td>{row.rollNumber}</td><td>{row.studentName}</td><td>
          <select aria-label={`Status for ${row.studentName}`} value={row.status} onChange={e => updateStatus(row.studentId, e.target.value as AttendanceStatus)}>
            <option value="PRESENT">Present</option><option value="ABSENT">Absent</option><option value="LATE">Late</option>
          </select>
        </td></tr>)}</tbody></table></div>
      {error && <p role="alert">{error}</p>}{notice && <p role="status">{notice}</p>}
      <button disabled={saving || !sessionId} onClick={() => void save()}>{saving ? "Saving…" : "Save attendance"}</button>
    </>}
  </main>;
}
