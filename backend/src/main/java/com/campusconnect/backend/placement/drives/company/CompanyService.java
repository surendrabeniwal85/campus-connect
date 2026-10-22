package com.campusconnect.backend.placement.drives.company;

import com.campusconnect.backend.placement.drives.PlacementConflictException;
import com.campusconnect.backend.placement.drives.PlacementNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

        private final CompanyRepository repository;

        public CompanyService(CompanyRepository repository) {
                this.repository = repository;
        }

        @Transactional(readOnly = true)
        public Page<CompanyResponse> list(Pageable pageable) {
                return repository.findByDeletedFalse(pageable).map(CompanyResponse::from);
        }

        @Transactional(readOnly = true)
        public CompanyResponse get(Long id) {
                return CompanyResponse.from(find(id));
        }

        @Transactional
        public CompanyResponse create(CompanyRequest request) {
                String name = request.name().trim();
                if (repository.existsByNameIgnoreCaseAndDeletedFalse(name)) {
                        throw new PlacementConflictException("A company with this name already exists.");
                }
                Company saved = repository.save(new Company(name, request.industry(), request.website()));
                return CompanyResponse.from(saved);
        }

        @Transactional
        public CompanyResponse update(Long id, CompanyRequest request) {
                Company company = find(id);
                String name = request.name().trim();
                if (!company.getName().equalsIgnoreCase(name)
                                && repository.existsByNameIgnoreCaseAndDeletedFalse(name)) {
                        throw new PlacementConflictException("A company with this name already exists.");
                }
                company.setName(name);
                company.setIndustry(request.industry());
                company.setWebsite(request.website());
                return CompanyResponse.from(company);
        }

        private Company find(Long id) {
                return repository.findByIdAndDeletedFalse(id)
                                .orElseThrow(() -> new PlacementNotFoundException("Company not found."));
        }
}
