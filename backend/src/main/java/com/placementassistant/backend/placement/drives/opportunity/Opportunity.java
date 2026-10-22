package com.placementassistant.backend.placement.drives.opportunity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "opportunity")
public class Opportunity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Enumerated(EnumType.STRING)
        @Column(name = "type", nullable = false)
        private OpportunityType type;

        @Column(name = "title", nullable = false, length = 200)
        private String title;

        @Column(name = "organisation", nullable = false, length = 150)
        private String organisation;

        @Column(name = "description", columnDefinition = "TEXT")
        private String description;

        @Column(name = "link", length = 500)
        private String link;

        @Column(name = "location", length = 150)
        private String location;

        @Column(name = "stipend_or_package", length = 100)
        private String stipendOrPackage;

        @Column(name = "start_date")
        private LocalDate startDate;

        @Column(name = "end_date")
        private LocalDate endDate;

        @Column(name = "deadline")
        private LocalDateTime deadline;

        @Column(name = "final_year_only", nullable = false)
        private boolean finalYearOnly;

        @Column(name = "deleted", nullable = false)
        private boolean deleted;

        protected Opportunity() {
        }

        public static Opportunity create(OpportunityRequest r) {
                Opportunity o = new Opportunity();
                o.apply(r);
                return o;
        }

        public void apply(OpportunityRequest r) {
                this.type = r.type();
                this.title = r.title().trim();
                this.organisation = r.organisation().trim();
                this.description = r.description();
                this.link = r.link();
                this.location = r.location();
                this.stipendOrPackage = r.stipendOrPackage();
                this.startDate = r.startDate();
                this.endDate = r.endDate();
                this.deadline = r.deadline();
                this.finalYearOnly = r.type() == OpportunityType.OFF_CAMPUS || Boolean.TRUE.equals(r.finalYearOnly());
        }

        public Long getId() { return id; }
        public OpportunityType getType() { return type; }
        public String getTitle() { return title; }
        public String getOrganisation() { return organisation; }
        public String getDescription() { return description; }
        public String getLink() { return link; }
        public String getLocation() { return location; }
        public String getStipendOrPackage() { return stipendOrPackage; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public LocalDateTime getDeadline() { return deadline; }
        public boolean isFinalYearOnly() { return finalYearOnly; }
        public boolean isDeleted() { return deleted; }
}
