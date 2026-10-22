package com.campusconnect.backend.placement.drives.company;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

        boolean existsByNameIgnoreCaseAndDeletedFalse(String name);

        Optional<Company> findByIdAndDeletedFalse(Long id);

        Page<Company> findByDeletedFalse(Pageable pageable);
}
