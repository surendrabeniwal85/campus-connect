package com.campusconnect.backend.institute.classroom.attendance;


import com.campusconnect.backend.institute.classroom.entity.AuditableEntity; import jakarta.persistence.*;
@Entity @Table(name="attendance_record",uniqueConstraints=@UniqueConstraint(name="uk_attendance_session_student",columnNames={"class_session_id","student_id"}),indexes=@Index(name="idx_attendance_student",columnList="student_id"))
public class AttendanceRecord extends AuditableEntity {
 @Column(name="class_session_id",nullable=false) private Long classSessionId; @Column(name="student_id",nullable=false) private Long studentId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=12) private AttendanceStatus status;
 protected AttendanceRecord(){} public AttendanceRecord(Long session,Long student,AttendanceStatus status){this.classSessionId=session;this.studentId=student;this.status=status;} public Long getClassSessionId(){return classSessionId;} public Long getStudentId(){return studentId;} public AttendanceStatus getStatus(){return status;} public void setStatus(AttendanceStatus status){this.status=status;}
}
