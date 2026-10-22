
import { useState } from "react";

import TeacherDashboard from "./TeacherDashboard";
import HodDashboard from "./HodDashboard";
import Timetable from "./Timetable";
import MarkAttendance from "./MarkAttendance";
import MyAttendance from "./MyAttendance";
import CrtPage from "./CrtPage";
import QuestionBank from "./QuestionBank";

import "./classroom.css";

export const classroomRoutes = [
  {
    path: "/institute/classroom/teacher",
    element: <TeacherDashboard />,
    title: "Teacher Dashboard",
  },
  {
    path: "/institute/classroom/hod",
    element: <HodDashboard />,
    title: "HOD Attendance Dashboard",
  },
  {
    path: "/institute/classroom/timetable",
    element: <Timetable />,
    title: "Timetable",
  },
  {
    path: "/institute/classroom/mark-attendance",
    element: <MarkAttendance />,
    title: "Mark Attendance",
  },
  {
    path: "/institute/classroom/my-attendance",
    element: <MyAttendance />,
    title: "My Attendance",
  },
  {
    path: "/institute/classroom/crt",
    element: <CrtPage />,
    title: "CRT Sessions",
  },
  {
    path: "/institute/classroom/question-bank",
    element: <QuestionBank />,
    title: "Question Bank",
  },
];

const classroomPages = [
  { title: "My Attendance", component: MyAttendance },
  { title: "Mark Attendance", component: MarkAttendance },
  { title: "Timetable", component: Timetable },
  { title: "Question Bank", component: QuestionBank },
  { title: "Teacher Dashboard", component: TeacherDashboard },
  { title: "HOD Attendance Dashboard", component: HodDashboard },
  { title: "CRT Sessions", component: CrtPage },
];

export default function ClassroomRoutes() {
  const [activePage, setActivePage] = useState("My Attendance");

  const selectedPage = classroomPages.find(
    (page) => page.title === activePage
  );

  const PageComponent = selectedPage?.component;

  return (
    <section className="classroom-module">
      <nav
        className="classroom-navigation"
        aria-label="Attendance pages"
      >
        {classroomPages.map((page) => (
          <button
            key={page.title}
            type="button"
            className={
              activePage === page.title ? "active" : ""
            }
            onClick={() => setActivePage(page.title)}
          >
            {page.title}
          </button>
        ))}
      </nav>

      <div className="classroom-page-content">
        {PageComponent && <PageComponent />}
      </div>
    </section>
  );
}
