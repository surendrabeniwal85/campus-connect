package com.campusconnect.backend.placement.selection.candidate;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoundCandidateRepository extends JpaRepository<RoundCandidate, Long> {
    List<RoundCandidate> findByRoundId(Long roundId);
}