package com.campusconnect.backend.placement.selection.candidate;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drive_feedback")
public class DriveFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drive_id", nullable = false)
    private Long driveId;

    @Column(nullable = false)
    private String source; // STUDENT, TPO, COMPANY

    @Column(nullable = false)
    private Integer rating; // 1 to 5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DriveFeedback() {}

    public DriveFeedback(Long driveId, String source, Integer rating, String comment) {
        this.driveId = driveId;
        this.source = source;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDriveId() { return driveId; }
    public void setDriveId(Long driveId) { this.driveId = driveId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}