package com.campusconnect.backend.institute.classroom.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="created_at", nullable=false, updatable=false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(name="created_by") private Long createdBy;
    @Column(nullable=false) private boolean deleted = false;
    @PreUpdate protected void beforeUpdate() { updatedAt = LocalDateTime.now(); }
    public Long getId(){return id;} public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getUpdatedAt(){return updatedAt;} public Long getCreatedBy(){return createdBy;}
    public void setCreatedBy(Long value){createdBy=value;} public boolean isDeleted(){return deleted;}
    public void setDeleted(boolean value){deleted=value;}
}
