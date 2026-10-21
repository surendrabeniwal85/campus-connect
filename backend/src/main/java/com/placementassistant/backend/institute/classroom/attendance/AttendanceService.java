package com.placementassistant.backend.institute.classroom.attendance;
import com.placementassistant.backend.institute.classroom.timetable.TimetableSlot;
import com.placementassistant.backend.institute.classroom.timetable.TimetableSlotRepository;

import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.time.*; import java.util.*;
@Service public class AttendanceService {
 private static final ZoneId ZONE=ZoneId.of("Asia/Kolkata"); private static final int EDIT_WINDOW_HOURS=24;
 private final ClassSessionRepository sessions; private final AttendanceRecordRepository records; private final AttendanceCorrectionRepository corrections; private final TimetableSlotRepository slots;
 public AttendanceService(ClassSessionRepository s,AttendanceRecordRepository r,AttendanceCorrectionRepository c,TimetableSlotRepository slots){this.sessions=s;this.records=r;this.corrections=c;this.slots=slots;}
 public List<ClassSession> sessions(LocalDate date,Long teacher){return teacher==null?sessions.findAllBySessionDateAndDeletedFalse(date):sessions.findAllByTeacherProfileIdAndSessionDateAndDeletedFalse(teacher,date);}
 @Transactional public ClassSession createSession(Long slotId,LocalDate date,LocalTime start,LocalTime end){TimetableSlot slot=slots.findById(slotId).filter(x->!x.isDeleted()).orElseThrow(()->new NoSuchElementException("Timetable slot not found")); if(date==null)throw new IllegalArgumentException("Session date is required"); if(start!=null&&end!=null&&!end.isAfter(start))throw new IllegalArgumentException("End time must be after start time"); return sessions.save(new ClassSession(slotId,slot.getTeacherProfileId(),date,start,end,slot.getSubject(),slot.getSection()));}
 @Transactional public List<AttendanceRecord> mark(Long sessionId,Long teacherId,Map<Long,AttendanceStatus> marks){ClassSession s=sessions.findById(sessionId).orElseThrow(()->new NoSuchElementException("Class session not found")); if(!Objects.equals(s.getTeacherProfileId(),teacherId))throw new SecurityException("Only the assigned teacher can mark attendance"); if(s.isLocked()||isExpired(s))throw new IllegalStateException("Attendance editing window has closed"); if(marks==null||marks.isEmpty())throw new IllegalArgumentException("Attendance marks cannot be empty"); List<AttendanceRecord> out=new ArrayList<>(); for(var e:marks.entrySet()){if(e.getKey()==null||e.getValue()==null)throw new IllegalArgumentException("Student and status are required"); if(records.existsByClassSessionIdAndStudentIdAndDeletedFalse(sessionId,e.getKey()))throw new IllegalStateException("Duplicate attendance for student "+e.getKey()); out.add(records.save(new AttendanceRecord(sessionId,e.getKey(),e.getValue())));} return out;}
 public List<AttendanceRecord> recordsForSession(Long id){if(!sessions.existsById(id))throw new NoSuchElementException("Class session not found");return records.findAllByClassSessionIdAndDeletedFalse(id);}
 @Transactional public AttendanceCorrection requestCorrection(Long recordId,AttendanceStatus newStatus,String reason,Long requester){if(reason==null||reason.isBlank())throw new IllegalArgumentException("Correction reason is required"); AttendanceRecord r=records.findById(recordId).orElseThrow(()->new NoSuchElementException("Attendance record not found")); if(r.getStatus()==newStatus)throw new IllegalArgumentException("New status must differ from current status"); return corrections.save(new AttendanceCorrection(recordId,r.getStatus(),newStatus,reason.trim(),requester));}
 @Transactional public AttendanceCorrection reviewCorrection(Long correctionId,Long approver,boolean approve){AttendanceCorrection c=corrections.findById(correctionId).orElseThrow(()->new NoSuchElementException("Correction not found")); if(!"PENDING".equals(c.getState()))throw new IllegalStateException("Correction has already been reviewed"); if(approve){AttendanceRecord r=records.findById(c.getAttendanceRecordId()).orElseThrow(()->new NoSuchElementException("Attendance record not found"));r.setStatus(c.getRequestedStatus());c.approve(approver);}else c.reject(approver);return c;}
 public Map<String,Object> studentSummary(Long studentId){List<AttendanceRecord> rs=records.findAllByStudentIdAndDeletedFalse(studentId);long total=rs.size(),present=rs.stream().filter(r->r.getStatus()==AttendanceStatus.PRESENT||r.getStatus()==AttendanceStatus.LATE).count(); double pct=total==0?0.0:present*100.0/total;return Map.of("studentId",studentId,"totalSessions",total,"attendedSessions",present,"attendancePercent",Math.round(pct*100.0)/100.0,"shortage",pct<75.0);}
    private boolean isExpired(ClassSession s) {
        ZonedDateTime sessionEnd = LocalDateTime.of(
            s.getSessionDate(),
            s.getEndsAt() == null ? LocalTime.MAX : s.getEndsAt()
        ).atZone(ZONE).plusHours(EDIT_WINDOW_HOURS);

        return ZonedDateTime.now(ZONE).isAfter(sessionEnd);
    }
}
