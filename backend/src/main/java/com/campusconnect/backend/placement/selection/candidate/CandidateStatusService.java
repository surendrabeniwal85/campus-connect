package com.campusconnect.backend.placement.selection.candidate;

import com.campusconnect.backend.platform.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CandidateStatusService {

    private final RoundCandidateRepository repository;
    private final NotificationService notificationService;

    public CandidateStatusService(RoundCandidateRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Transactional
    public RoundCandidate updateStatus(Long candidateId, RoundCandidate.CandidateStatus newStatus) {
        RoundCandidate candidate = repository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        RoundCandidate.CandidateStatus current = candidate.getStatus();

        if (current == RoundCandidate.CandidateStatus.PENDING && newStatus == RoundCandidate.CandidateStatus.SELECTED) {
            throw new IllegalStateException("Invalid status jump: PENDING cannot go directly to SELECTED");
        }

        candidate.setStatus(newStatus);
        RoundCandidate saved = repository.save(candidate);

        if (newStatus == RoundCandidate.CandidateStatus.ADVANCED || newStatus == RoundCandidate.CandidateStatus.SELECTED) {
            notificationService.send(
                "USER",
                String.valueOf(saved.getStudentId()),
                "Round Update",
                "You have successfully cleared the current round!",
                "/student/rounds",
                List.of(saved.getStudentId())
            );
        }

        return saved;
    }
}