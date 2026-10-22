package com.campusconnect.backend.placement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "drive_rounds", uniqueConstraints = @UniqueConstraint(columnNames = {"drive_id", "roundOrder"}))
public class DriveRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_id", nullable = false)
    private PlacementDrive drive;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoundType roundType;

    @NotNull
    private Integer roundOrder;

    private String mode;
    private LocalDateTime roundTime;

    public enum RoundType {
        APTITUDE, TECHNICAL, MANAGERIAL, HR
    }

    public DriveRound() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PlacementDrive getDrive() { return drive; }
    public void setDrive(PlacementDrive drive) { this.drive = drive; }

    public RoundType getRoundType() { return roundType; }
    public void setRoundType(RoundType roundType) { this.roundType = roundType; }

    public Integer getRoundOrder() { return roundOrder; }
    public void setRoundOrder(Integer roundOrder) { this.roundOrder = roundOrder; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public LocalDateTime getRoundTime() { return roundTime; }
    public void setRoundTime(LocalDateTime roundTime) { this.roundTime = roundTime; }
}