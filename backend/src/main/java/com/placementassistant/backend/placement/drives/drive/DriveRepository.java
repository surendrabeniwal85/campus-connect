package com.placementassistant.backend.placement.drives.drive;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriveRepository extends JpaRepository<Drive, Long> {

        Optional<Drive> findByIdAndDeletedFalse(Long id);

        Page<Drive> findByDeletedFalse(Pageable pageable);

        Page<Drive> findByStatusAndDriveDateGreaterThanEqualAndDeletedFalse(
                        DriveStatus status, LocalDate from, Pageable pageable);

        List<Drive> findByDriveDateBetweenAndDeletedFalseOrderByDriveDate(LocalDate from, LocalDate to);
}
