package com.campusconnect.backend.placement.selection.candidate;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "live_session_update")
public class LiveSessionUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drive_id", nullable = false)
    private Long driveId;

    @Column(nullable = false)
    private String stage;

    @Column(name = "counts_json", columnDefinition = "TEXT")
    private String countsJson;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public LiveSessionUpdate() {}

    public LiveSessionUpdate(Long driveId, String stage, String countsJson, String note) {
        this.driveId = driveId;
        this.stage = stage;
        this.countsJson = countsJson;
        this.note = note;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDriveId() { return driveId; }
    public void setDriveId(Long driveId) { this.driveId = driveId; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getCountsJson() { return countsJson; }
    public void setCountsJson(String countsJson) { this.countsJson = countsJson; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}