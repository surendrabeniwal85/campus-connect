package com.campusconnect.backend.institute.classroom.crt;


import com.campusconnect.backend.institute.classroom.entity.AuditableEntity; import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="crt_session") public class CrtSession extends AuditableEntity {
 @Column(nullable=false,length=160) private String title; @Column(name="session_date",nullable=false) private LocalDate sessionDate; @Column(nullable=false,length=120) private String trainer;
 @Column(name="target_branch",nullable=false,length=80) private String targetBranch; @Column(name="target_semester",nullable=false) private int targetSemester; @Column(name="teacher_profile_id") private Long teacherProfileId;
 protected CrtSession(){} public CrtSession(String title,LocalDate date,String trainer,String branch,int semester,Long teacher){this.title=title;this.sessionDate=date;this.trainer=trainer;this.targetBranch=branch;this.targetSemester=semester;this.teacherProfileId=teacher;} public String getTitle(){return title;} public LocalDate getSessionDate(){return sessionDate;} public String getTrainer(){return trainer;} public String getTargetBranch(){return targetBranch;} public int getTargetSemester(){return targetSemester;} public Long getTeacherProfileId(){return teacherProfileId;}
}
