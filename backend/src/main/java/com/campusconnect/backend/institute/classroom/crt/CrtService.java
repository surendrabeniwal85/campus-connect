package com.campusconnect.backend.institute.classroom.crt;
import com.campusconnect.backend.institute.classroom.attendance.AttendanceStatus;


import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.time.LocalDate; import java.util.*;
@Service public class CrtService {private final CrtSessionRepository sessions;private final CrtAttendanceRepository attendance; public CrtService(CrtSessionRepository s,CrtAttendanceRepository a){sessions=s;attendance=a;}
 public List<CrtSession> list(){return sessions.findAllByDeletedFalse();}
 @Transactional public CrtSession create(String title,LocalDate date,String trainer,String branch,int semester,Long teacher){if(title==null||title.isBlank()||date==null||trainer==null||trainer.isBlank()||branch==null||branch.isBlank()||semester<1||semester>8)throw new IllegalArgumentException("Invalid CRT session details");return sessions.save(new CrtSession(title.trim(),date,trainer.trim(),branch.trim(),semester,teacher));}
 @Transactional public CrtAttendance mark(Long session,Long student,AttendanceStatus status){if(!sessions.existsById(session))throw new NoSuchElementException("CRT session not found");if(attendance.existsByCrtSessionIdAndStudentIdAndDeletedFalse(session,student))throw new IllegalStateException("Duplicate CRT attendance");return attendance.save(new CrtAttendance(session,student,status));}
 public Map<String,Object> studentSummary(Long student){List<CrtAttendance> a=attendance.findAllByStudentIdAndDeletedFalse(student);long total=a.size(),attended=a.stream().filter(x->x.getStatus()==AttendanceStatus.PRESENT||x.getStatus()==AttendanceStatus.LATE).count();return Map.of("studentId",student,"totalCrtSessions",total,"attendedCrtSessions",attended,"crtPercent",total==0?0.0:Math.round(attended*10000.0/total)/100.0);}
}
