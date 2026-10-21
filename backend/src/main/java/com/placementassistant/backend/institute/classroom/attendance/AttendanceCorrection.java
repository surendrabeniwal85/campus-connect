package com.placementassistant.backend.institute.classroom.attendance;


import com.placementassistant.backend.institute.classroom.entity.AuditableEntity; import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="attendance_correction") public class AttendanceCorrection extends AuditableEntity {
 @Column(name="attendance_record_id",nullable=false) private Long attendanceRecordId; @Enumerated(EnumType.STRING) @Column(name="old_status",nullable=false,length=12) private AttendanceStatus oldStatus;
 @Enumerated(EnumType.STRING) @Column(name="requested_status",nullable=false,length=12) private AttendanceStatus requestedStatus; @Column(nullable=false,length=500) private String reason;
 @Column(name="requested_by",nullable=false) private Long requestedBy; @Column(name="approved_by") private Long approvedBy; @Column(nullable=false,length=12) private String state="PENDING"; @Column(name="reviewed_at") private LocalDateTime reviewedAt;
 protected AttendanceCorrection(){} public AttendanceCorrection(Long record,AttendanceStatus oldS,AttendanceStatus newS,String reason,Long requester){this.attendanceRecordId=record;this.oldStatus=oldS;this.requestedStatus=newS;this.reason=reason;this.requestedBy=requester;}
 public Long getAttendanceRecordId(){return attendanceRecordId;} public AttendanceStatus getOldStatus(){return oldStatus;} public AttendanceStatus getRequestedStatus(){return requestedStatus;} public String getReason(){return reason;} public Long getRequestedBy(){return requestedBy;} public Long getApprovedBy(){return approvedBy;} public String getState(){return state;} public LocalDateTime getReviewedAt(){return reviewedAt;}
 public void approve(Long approver){this.approvedBy=approver;this.state="APPROVED";this.reviewedAt=LocalDateTime.now();} public void reject(Long approver){this.approvedBy=approver;this.state="REJECTED";this.reviewedAt=LocalDateTime.now();}
}
