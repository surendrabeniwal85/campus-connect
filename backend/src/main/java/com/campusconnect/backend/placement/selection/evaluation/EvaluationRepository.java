package com.campusconnect.backend.placement.selection.candidate;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByRoundId(Long roundId);
    List<Evaluation> findByRoundIdAndStudentId(Long roundId, Long studentId);
}