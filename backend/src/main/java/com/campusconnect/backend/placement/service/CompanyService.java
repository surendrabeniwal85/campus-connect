package com.campusconnect.backend.placement.service;

import com.campusconnect.backend.placement.entity.Company;
import com.campusconnect.backend.placement.repository.CompanyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company createCompany(Company company) {
        if (companyRepository.existsByName(company.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Company with this name already exists");
        }
        return companyRepository.save(company);
    }
}