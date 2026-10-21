package com.placementassistant.backend.institute.classroom.teacher;


import com.placementassistant.backend.institute.classroom.entity.AuditableEntity;
import jakarta.persistence.*;

@Entity @Table(name="teacher_profile", indexes={@Index(name="idx_teacher_department",columnList="department")})
public class TeacherProfile extends AuditableEntity {
 @Column(name="account_id",nullable=false,unique=true) private Long accountId;
 @Column(name="display_name",nullable=false,length=120) private String displayName;
 @Column(nullable=false,length=100) private String department;
 @Column(nullable=false,length=80) private String designation;
 @Column(name="is_hod",nullable=false) private boolean hod;
 protected TeacherProfile() {}
 public TeacherProfile(Long accountId,String displayName,String department,String designation,boolean hod){this.accountId=accountId;this.displayName=displayName;this.department=department;this.designation=designation;this.hod=hod;}
 public Long getAccountId(){return accountId;} public String getDisplayName(){return displayName;} public String getDepartment(){return department;} public String getDesignation(){return designation;} public boolean isHod(){return hod;}
 public void update(String name,String department,String designation,boolean hod){this.displayName=name;this.department=department;this.designation=designation;this.hod=hod;}
}
