package com.placementassistant.backend.institute.records.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "student_profile")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "roll_number", nullable = false, unique = true, length = 50)
    private String rollNumber;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 190)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "course", nullable = false, length = 100)
    private String course = "B.Tech";

    @Column(name = "department", nullable = false, length = 100)
    private String department = "Computer Science & Engineering";

    @Column(name = "branch", nullable = false, length = 100)
    private String branch = "CSE";

    @Column(name = "semester", nullable = false)
    private Integer semester = 7;

    @Column(name = "cgpa", precision = 4, scale = 2)
    private BigDecimal cgpa = BigDecimal.valueOf(8.52);

    @Column(name = "active_backlogs")
    private Integer activeBacklogs = 0;

    @Column(name = "class10_percent", precision = 5, scale = 2)
    private BigDecimal class10Percent = BigDecimal.valueOf(88.50);

    @Column(name = "class12_percent", precision = 5, scale = 2)
    private BigDecimal class12Percent = BigDecimal.valueOf(86.00);

    @Column(name = "passing_year")
    private Integer passingYear = 2027;

    @Column(name = "ats_score")
    private Integer atsScore = 82;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public StudentProfile() {
    }

    public StudentProfile(String rollNumber, String fullName, String email, String branch, Integer semester) {
        this.rollNumber = rollNumber;
        this.fullName = fullName;
        this.email = email;
        this.branch = branch;
        this.semester = semester;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public BigDecimal getCgpa() {
        return cgpa;
    }

    public void setCgpa(BigDecimal cgpa) {
        this.cgpa = cgpa;
    }

    public Integer getActiveBacklogs() {
        return activeBacklogs;
    }

    public void setActiveBacklogs(Integer activeBacklogs) {
        this.activeBacklogs = activeBacklogs;
    }

    public BigDecimal getClass10Percent() {
        return class10Percent;
    }

    public void setClass10Percent(BigDecimal class10Percent) {
        this.class10Percent = class10Percent;
    }

    public BigDecimal getClass12Percent() {
        return class12Percent;
    }

    public void setClass12Percent(BigDecimal class12Percent) {
        this.class12Percent = class12Percent;
    }

    public Integer getPassingYear() {
        return passingYear;
    }

    public void setPassingYear(Integer passingYear) {
        this.passingYear = passingYear;
    }

    public Integer getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(Integer atsScore) {
        this.atsScore = atsScore;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isFinalYear() {
        return semester != null && (semester == 7 || semester == 8);
    }
}
