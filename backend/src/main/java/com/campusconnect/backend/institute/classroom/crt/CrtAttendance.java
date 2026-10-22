package com.campusconnect.backend.institute.classroom.crt;
import com.campusconnect.backend.institute.classroom.attendance.AttendanceStatus;


import com.campusconnect.backend.institute.classroom.entity.AuditableEntity; import jakarta.persistence.*;
@Entity @Table(name="crt_attendance",uniqueConstraints=@UniqueConstraint(name="uk_crt_student_session",columnNames={"crt_session_id","student_id"})) public class CrtAttendance extends AuditableEntity {
 @Column(name="crt_session_id",nullable=false) private Long crtSessionId; @Column(name="student_id",nullable=false) private Long studentId; @Enumerated(EnumType.STRING) @Column(nullable=false,length=12) private AttendanceStatus status;
 protected CrtAttendance(){} public CrtAttendance(Long session,Long student,AttendanceStatus status){this.crtSessionId=session;this.studentId=student;this.status=status;} public Long getCrtSessionId(){return crtSessionId;} public Long getStudentId(){return studentId;} public AttendanceStatus getStatus(){return status;}
}
