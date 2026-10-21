import React, { useState } from "react";
import { classroomApi } from "./classroomApi";
import type { AttendanceSummary } from "./types";
import { PageState } from "./components/PageState";

export default function MyAttendance() {
  const [studentId, setStudentId] = useState("");
  const [summary, setSummary] = useState<AttendanceSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const load = async (event: React.FormEvent) => {
    event.preventDefault(); setError(""); setSummary(null);
    if (!studentId.trim() || Number(studentId) <= 0) { setError("Enter a valid student ID."); return; }
    setLoading(true);
    try { setSummary(await classroomApi.attendanceSummary(Number(studentId))); }
    catch (e) { setError(e instanceof Error ? e.message : "Could not load attendance summary."); }
    finally { setLoading(false); }
  };
  return <main className="classroom-page">
    <h1>My Attendance</h1>
    <p>Shows overall and subject-wise attendance, monthly summary, and shortage below the configured threshold.</p>
    <form onSubmit={load} className="classroom-inline-form">
      <label>Student ID <input value={studentId} onChange={e => setStudentId(e.target.value)} inputMode="numeric" /></label>
      <button type="submit" disabled={loading}>{loading ? "Loading…" : "View summary"}</button>
    </form>
    <PageState loading={loading} error={error} />
    {summary && <section className="classroom-card" aria-label="Attendance summary">
      <h2>{summary.studentName ?? "Attendance summary"}</h2>
      <p>Total sessions: {summary.totalSessions}</p><p>Present: {summary.present}</p>
      <p>Absent: {summary.absent}</p><p>Late: {summary.late}</p>
      <p>Attendance: <strong>{summary.percentage.toFixed(1)}%</strong></p>
      <p>{summary.shortage ? "Attendance shortage: below required threshold" : "No attendance shortage"}</p>
    </section>}
  </main>;
}
