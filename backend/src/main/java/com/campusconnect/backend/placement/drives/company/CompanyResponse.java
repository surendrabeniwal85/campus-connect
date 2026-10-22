package com.campusconnect.backend.placement.drives.company;

public record CompanyResponse(Long id, String name, String industry, String website) {

        static CompanyResponse from(Company company) {
                return new CompanyResponse(company.getId(), company.getName(),
                                company.getIndustry(), company.getWebsite());
        }
}
