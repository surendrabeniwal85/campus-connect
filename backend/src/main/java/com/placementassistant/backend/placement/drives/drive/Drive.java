package com.placementassistant.backend.placement.drives.drive;

import com.placementassistant.backend.placement.drives.company.Company;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "placement_drive")
public class Drive {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "company_id", nullable = false)
        private Company company;

        @Column(name = "role_title", nullable = false, length = 150)
        private String roleTitle;

        @Enumerated(EnumType.STRING)
        @Column(name = "employment_type", nullable = false)
        private EmploymentType employmentType = EmploymentType.FULL_TIME;

        @Column(name = "package_text", length = 100)
        private String packageText;

        @Column(name = "ctc_lpa", precision = 6, scale = 2)
        private BigDecimal ctcLpa;

        @Column(name = "stipend_per_month", precision = 10, scale = 2)
        private BigDecimal stipendPerMonth;

        @Column(name = "has_ppo", nullable = false)
        private boolean hasPpo;

        @Column(name = "ppo_ctc_lpa", precision = 6, scale = 2)
        private BigDecimal ppoCtcLpa;

        @Column(name = "bond_months")
        private Integer bondMonths;

        @Column(name = "bond_details", columnDefinition = "TEXT")
        private String bondDetails;

        @Column(name = "job_location", length = 150)
        private String jobLocation;

        @Column(name = "other_benefits", columnDefinition = "TEXT")
        private String otherBenefits;

        @Column(name = "venue", length = 200)
        private String venue;

        @Column(name = "reporting_time")
        private LocalDateTime reportingTime;

        @Column(name = "instructions", columnDefinition = "TEXT")
        private String instructions;

        @Column(name = "drive_date", nullable = false)
        private LocalDate driveDate;

        @Column(name = "deadline", nullable = false)
        private LocalDateTime deadline;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false)
        private DriveStatus status = DriveStatus.DRAFT;

        @Column(name = "deleted", nullable = false)
        private boolean deleted;

        protected Drive() {
        }

        public static Drive create(Company company, DriveRequest request) {
                Drive drive = new Drive();
                drive.apply(company, request);
                return drive;
        }

        public void apply(Company company, DriveRequest r) {
                this.company = company;
                this.roleTitle = r.roleTitle().trim();
                this.employmentType = r.employmentType() == null ? EmploymentType.FULL_TIME : r.employmentType();
                this.packageText = r.packageText();
                this.ctcLpa = r.ctcLpa();
                this.stipendPerMonth = r.stipendPerMonth();
                this.hasPpo = Boolean.TRUE.equals(r.hasPpo());
                this.ppoCtcLpa = r.ppoCtcLpa();
                this.bondMonths = r.bondMonths();
                this.bondDetails = r.bondDetails();
                this.jobLocation = r.jobLocation();
                this.otherBenefits = r.otherBenefits();
                this.venue = r.venue();
                this.reportingTime = r.reportingTime();
                this.instructions = r.instructions();
                this.driveDate = r.driveDate();
                this.deadline = r.deadline();
                this.status = r.status() == null ? DriveStatus.DRAFT : r.status();
        }

        public Long getId() { return id; }
        public Company getCompany() { return company; }
        public String getRoleTitle() { return roleTitle; }
        public EmploymentType getEmploymentType() { return employmentType; }
        public String getPackageText() { return packageText; }
        public BigDecimal getCtcLpa() { return ctcLpa; }
        public BigDecimal getStipendPerMonth() { return stipendPerMonth; }
        public boolean isHasPpo() { return hasPpo; }
        public BigDecimal getPpoCtcLpa() { return ppoCtcLpa; }
        public Integer getBondMonths() { return bondMonths; }
        public String getBondDetails() { return bondDetails; }
        public String getJobLocation() { return jobLocation; }
        public String getOtherBenefits() { return otherBenefits; }
        public String getVenue() { return venue; }
        public LocalDateTime getReportingTime() { return reportingTime; }
        public String getInstructions() { return instructions; }
        public LocalDate getDriveDate() { return driveDate; }
        public LocalDateTime getDeadline() { return deadline; }
        public DriveStatus getStatus() { return status; }
        public boolean isDeleted() { return deleted; }
}
