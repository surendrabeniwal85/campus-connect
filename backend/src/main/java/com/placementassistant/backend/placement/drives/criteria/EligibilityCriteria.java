package com.placementassistant.backend.placement.drives.criteria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "eligibility_rule")
public class EligibilityCriteria {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "drive_id", nullable = false, unique = true)
        private Long driveId;

        @Column(name = "min_cgpa", precision = 4, scale = 2)
        private BigDecimal minCgpa;

        @Column(name = "max_backlogs")
        private Integer maxBacklogs;

        @Column(name = "min_class10", precision = 5, scale = 2)
        private BigDecimal minClass10;

        @Column(name = "min_class12", precision = 5, scale = 2)
        private BigDecimal minClass12;

        @Column(name = "min_attendance", precision = 5, scale = 2)
        private BigDecimal minAttendance;

        @Column(name = "allowed_branches", length = 255)
        private String allowedBranches;

        @Column(name = "passing_year")
        private Integer passingYear;

        @Column(name = "min_ats_score")
        private Integer minAtsScore;

        @Column(name = "deleted", nullable = false)
        private boolean deleted;

        protected EligibilityCriteria() {
        }

        public EligibilityCriteria(Long driveId) {
                this.driveId = driveId;
        }

        public void apply(CriteriaRequest r) {
                this.minCgpa = r.minCgpa();
                this.maxBacklogs = r.maxBacklogs();
                this.minClass10 = r.minClass10();
                this.minClass12 = r.minClass12();
                this.minAttendance = r.minAttendance();
                this.allowedBranches = r.allowedBranches();
                this.passingYear = r.passingYear();
                this.minAtsScore = r.minAtsScore();
        }

        public Long getId() { return id; }
        public Long getDriveId() { return driveId; }
        public BigDecimal getMinCgpa() { return minCgpa; }
        public Integer getMaxBacklogs() { return maxBacklogs; }
        public BigDecimal getMinClass10() { return minClass10; }
        public BigDecimal getMinClass12() { return minClass12; }
        public BigDecimal getMinAttendance() { return minAttendance; }
        public String getAllowedBranches() { return allowedBranches; }
        public Integer getPassingYear() { return passingYear; }
        public Integer getMinAtsScore() { return minAtsScore; }
        public boolean isDeleted() { return deleted; }
}
