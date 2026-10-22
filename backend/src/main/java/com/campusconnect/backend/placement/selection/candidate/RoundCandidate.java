package com.campusconnect.backend.placement.selection.candidate;

import jakarta.persistence.*;

@Entity
@Table(name = "round_candidates")
public class RoundCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "round_id", nullable = false)
    private Long roundId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateStatus status = CandidateStatus.PENDING;

    public enum CandidateStatus {
        PENDING, SHORTLISTED, ADVANCED, ELIMINATED, SELECTED, REJECTED
    }

    public RoundCandidate() {}

    public RoundCandidate(Long roundId, Long studentId, CandidateStatus status) {
        this.roundId = roundId;
        this.studentId = studentId;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoundId() { return roundId; }
    public void setRoundId(Long roundId) { this.roundId = roundId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public CandidateStatus getStatus() { return status; }
    public void setStatus(CandidateStatus status) { this.status = status; }
}