package com.placementassistant.backend.placement.drives.opportunity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OpportunityResponse(Long id, OpportunityType type, String title, String organisation,
                String description, String link, String location, String stipendOrPackage,
                LocalDate startDate, LocalDate endDate, LocalDateTime deadline, boolean finalYearOnly) {

        static OpportunityResponse from(Opportunity o) {
                return new OpportunityResponse(o.getId(), o.getType(), o.getTitle(), o.getOrganisation(),
                                o.getDescription(), o.getLink(), o.getLocation(), o.getStipendOrPackage(),
                                o.getStartDate(), o.getEndDate(), o.getDeadline(), o.isFinalYearOnly());
        }
}
