import { useEffect, useState } from "react";
import { classroomApi } from "./classroomApi";
import type { Question } from "./types";
import { PageState } from "./components/PageState";

export default function QuestionBank() {
  const [questions, setQuestions] = useState<Question[]>([]);
  const [company, setCompany] = useState("");
  const [round, setRound] = useState("");
  const [topic, setTopic] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const load = async () => {
    setLoading(true); setError("");
    try { setQuestions(await classroomApi.questions({ company, round, topic })); }
    catch (e) { setError(e instanceof Error ? e.message : "Could not load questions."); }
    finally { setLoading(false); }
  };
  useEffect(() => { void load(); }, []);
  return <main className="classroom-page">
    <h1>Question Bank</h1>
    <form className="classroom-filter-form" onSubmit={e => { e.preventDefault(); void load(); }}>
      <label>Company <input value={company} onChange={e => setCompany(e.target.value)} /></label>
      <label>Round <select value={round} onChange={e => setRound(e.target.value)}>
        <option value="">All rounds</option><option>APTITUDE</option><option>TECHNICAL</option><option>MANAGERIAL</option><option>HR</option>
      </select></label>
      <label>Topic <input value={topic} onChange={e => setTopic(e.target.value)} /></label>
      <button type="submit" disabled={loading}>Filter</button>
    </form>
    <PageState loading={loading} error={error} empty={!loading && !error && questions.length === 0} onRetry={load} />
    {!loading && !error && questions.length > 0 && <div className="classroom-list">
      {questions.filter(q => q.approved).map(q => <article className="classroom-card" key={q.id}>
        <p>{q.company} · {q.round} · {q.topic} · {q.difficulty}</p><h2>{q.questionText}</h2>
      </article>)}
    </div>}
    <p className="classroom-help">Students must only receive approved questions; enforce approval and role checks on the backend too.</p>
  </main>;
}
