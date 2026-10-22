package com.campusconnect.backend.placement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "drive_criteria")
public class DriveCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_id", nullable = false)
    private PlacementDrive drive;

    private Double minCgpa;
    private Integer maxBacklogs;
    private Double minClass10Percentage;
    private Double minClass12Percentage;
    private Double minAttendance;
    private String allowedBranches;
    private Integer passingYear;

    public DriveCriteria() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PlacementDrive getDrive() { return drive; }
    public void setDrive(PlacementDrive drive) { this.drive = drive; }

    public Double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(Double minCgpa) { this.minCgpa = minCgpa; }

    public Integer getMaxBacklogs() { return maxBacklogs; }
    public void setMaxBacklogs(Integer maxBacklogs) { this.maxBacklogs = maxBacklogs; }

    public Double getMinClass10Percentage() { return minClass10Percentage; }
    public void setMinClass10Percentage(Double minClass10Percentage) { this.minClass10Percentage = minClass10Percentage; }

    public Double getMinClass12Percentage() { return minClass12Percentage; }
    public void setMinClass12Percentage(Double minClass12Percentage) { this.minClass12Percentage = minClass12Percentage; }

    public Double getMinAttendance() { return minAttendance; }
    public void setMinAttendance(Double minAttendance) { this.minAttendance = minAttendance; }

    public String getAllowedBranches() { return allowedBranches; }
    public void setAllowedBranches(String allowedBranches) { this.allowedBranches = allowedBranches; }

    public Integer getPassingYear() { return passingYear; }
    public void setPassingYear(Integer passingYear) { this.passingYear = passingYear; }
}