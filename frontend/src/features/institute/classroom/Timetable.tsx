import { useEffect, useState } from "react";
import { classroomApi } from "./classroomApi";
import type { TimetableSlot } from "./types";
import { PageState } from "./components/PageState";

export default function Timetable() {
  const [slots, setSlots] = useState<TimetableSlot[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const load = async () => {
    setLoading(true); setError("");
    try { setSlots(await classroomApi.timetable()); }
    catch (e) { setError(e instanceof Error ? e.message : "Could not load timetable."); }
    finally { setLoading(false); }
  };
  useEffect(() => { void load(); }, []);
  return <main className="classroom-page">
    <h1>Timetable</h1>
    <p>Slots are checked by the server for teacher, room and section clashes.</p>
    <PageState loading={loading} error={error} empty={!loading && !error && slots.length === 0} onRetry={load} />
    {!loading && !error && slots.length > 0 && <div className="classroom-table-wrap">
      <table><thead><tr><th>Day</th><th>Period</th><th>Time</th><th>Section</th><th>Subject</th><th>Teacher</th><th>Room</th></tr></thead>
      <tbody>{slots.map(s => <tr key={s.id}><td>{s.dayOfWeek}</td><td>{s.periodNumber}</td>
        <td>{s.startTime ?? "—"}–{s.endTime ?? "—"}</td><td>{s.section}</td><td>{s.subject}</td>
        <td>{s.teacherName ?? s.teacherId}</td><td>{s.roomName ?? s.roomId}</td></tr>)}</tbody></table>
    </div>}
  </main>;
}
