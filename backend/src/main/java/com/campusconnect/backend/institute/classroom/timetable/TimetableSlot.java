package com.campusconnect.backend.institute.classroom.timetable;


import com.campusconnect.backend.institute.classroom.entity.AuditableEntity; import jakarta.persistence.*;
@Entity @Table(name="timetable_slot",indexes={@Index(name="idx_slot_day_period",columnList="day_of_week,period_number")})
public class TimetableSlot extends AuditableEntity {
 @Column(name="teacher_profile_id",nullable=false) private Long teacherProfileId; @Column(name="room_id",nullable=false) private Long roomId;
 @Column(nullable=false,length=80) private String section; @Column(nullable=false,length=120) private String subject;
 @Column(name="day_of_week",nullable=false) private int dayOfWeek; @Column(name="period_number",nullable=false) private int periodNumber;
 protected TimetableSlot(){} public TimetableSlot(Long teacher,Long room,String section,String subject,int day,int period){this.teacherProfileId=teacher;this.roomId=room;this.section=section;this.subject=subject;this.dayOfWeek=day;this.periodNumber=period;}
 public Long getTeacherProfileId(){return teacherProfileId;} public Long getRoomId(){return roomId;} public String getSection(){return section;} public String getSubject(){return subject;} public int getDayOfWeek(){return dayOfWeek;} public int getPeriodNumber(){return periodNumber;}
}
