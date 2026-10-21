package com.campusconnect.backend.institute.classroom.attendance;


import com.campusconnect.backend.institute.classroom.entity.AuditableEntity; import jakarta.persistence.*; import java.time.*;
@Entity @Table(name="class_session",indexes={@Index(name="idx_session_date_teacher",columnList="session_date,teacher_profile_id")})
public class ClassSession extends AuditableEntity {
 @Column(name="timetable_slot_id",nullable=false) private Long timetableSlotId; @Column(name="teacher_profile_id",nullable=false) private Long teacherProfileId;
 @Column(name="session_date",nullable=false) private LocalDate sessionDate; @Column(name="starts_at") private LocalTime startsAt; @Column(name="ends_at") private LocalTime endsAt;
 @Column(nullable=false,length=120) private String subject; @Column(nullable=false,length=80) private String section; @Column(nullable=false) private boolean locked=false;
 protected ClassSession(){} public ClassSession(Long slot,Long teacher,LocalDate date,LocalTime start,LocalTime end,String subject,String section){this.timetableSlotId=slot;this.teacherProfileId=teacher;this.sessionDate=date;this.startsAt=start;this.endsAt=end;this.subject=subject;this.section=section;}
 public Long getTimetableSlotId(){return timetableSlotId;} public Long getTeacherProfileId(){return teacherProfileId;} public LocalDate getSessionDate(){return sessionDate;} public LocalTime getStartsAt(){return startsAt;} public LocalTime getEndsAt(){return endsAt;} public String getSubject(){return subject;} public String getSection(){return section;} public boolean isLocked(){return locked;} public void lock(){locked=true;}
}
