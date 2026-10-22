export type AttendanceStatus = "PRESENT" | "ABSENT" | "LATE";

export interface TeacherProfile {
  id: number;
  accountId: number;
  name: string;
  department: string;
  designation: string;
  isHod: boolean;
}

export interface TimetableSlot {
  id: number;
  section: string;
  subject: string;
  teacherId: number;
  teacherName?: string;
  roomId: number;
  roomName?: string;
  dayOfWeek: string;
  periodNumber: number;
  startTime?: string;
  endTime?: string;
}

export interface ClassSession {
  id: number;
  subject: string;
  section: string;
  sessionDate: string;
  teacherId: number;
  attendanceLocked?: boolean;
}

export interface AttendanceRow {
  studentId: number;
  studentName: string;
  rollNumber: string;
  status: AttendanceStatus;
}

export interface AttendanceSummary {
  studentId?: number;
  studentName?: string;
  totalSessions: number;
  present: number;
  absent: number;
  late: number;
  percentage: number;
  shortage: boolean;
  subject?: string;
  month?: string;
}

export interface CrtSession {
  id: number;
  title: string;
  sessionDate: string;
  trainer: string;
  targetBranch: string;
  semester: number;
}

export interface Question {
  id: number;
  company: string;
  round: "APTITUDE" | "TECHNICAL" | "MANAGERIAL" | "HR";
  topic: string;
  difficulty: "EASY" | "MEDIUM" | "HARD";
  questionText: string;
  approved: boolean;
}
