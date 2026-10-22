package com.placementassistant.backend.placement.drives.eligibility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.placementassistant.backend.institute.records.entity.StudentProfile;
import com.placementassistant.backend.institute.records.repository.StudentProfileRepository;
import com.placementassistant.backend.institute.records.service.StudentQueryService;
import com.placementassistant.backend.placement.drives.company.Company;
import com.placementassistant.backend.placement.drives.criteria.CriteriaRepository;
import com.placementassistant.backend.placement.drives.criteria.EligibilityCriteria;
import com.placementassistant.backend.placement.drives.drive.Drive;
import com.placementassistant.backend.placement.drives.drive.DriveRepository;
import com.placementassistant.backend.placement.drives.eligibility.OfferPathDtos.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OfferPathServiceTests {

    @Mock
    private DriveRepository driveRepository;

    @Mock
    private CriteriaRepository criteriaRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private StudentQueryService studentQueryService;

    private OfferPathService offerPathService;

    @BeforeEach
    void setUp() {
        offerPathService = new OfferPathService(
                driveRepository,
                criteriaRepository,
                studentProfileRepository,
                studentQueryService
        );
    }

    private Drive mockDrive(Long id, String companyName, String title) {
        Company c = mock(Company.class);
        lenient().when(c.getName()).thenReturn(companyName);
        Drive d = mock(Drive.class);
        lenient().when(d.getId()).thenReturn(id);
        lenient().when(d.getCompany()).thenReturn(c);
        lenient().when(d.getRoleTitle()).thenReturn(title);
        return d;
    }

    private StudentProfile createProfile(Long id, String name, String roll, String email, BigDecimal cgpa, int backlogs, String branch) {
        StudentProfile p = new StudentProfile();
        p.setId(id);
        p.setFullName(name);
        p.setRollNumber(roll);
        p.setEmail(email);
        p.setCgpa(cgpa);
        p.setActiveBacklogs(backlogs);
        p.setBranch(branch);
        p.setPassingYear(2027);
        p.setAtsScore(80);
        return p;
    }

    @Test
    void explainDriveEligibilityEvaluatesActualStudentFacts() {
        Long driveId = 100L;
        Drive drive = mockDrive(driveId, "Tech Corp", "Software Dev");
        when(driveRepository.findByIdAndDeletedFalse(driveId)).thenReturn(Optional.of(drive));

        EligibilityCriteria criteria = new EligibilityCriteria(driveId);
        EligibilityCriteria spyCrit = spy(criteria);
        doReturn(new BigDecimal("7.50")).when(spyCrit).getMinCgpa();
        doReturn(0).when(spyCrit).getMaxBacklogs();
        doReturn(new BigDecimal("75.00")).when(spyCrit).getMinAttendance();
        doReturn("CSE,IT").when(spyCrit).getAllowedBranches();

        when(criteriaRepository.findByDriveIdAndDeletedFalse(driveId)).thenReturn(Optional.of(spyCrit));

        StudentFacts facts = new StudentFacts(
                new BigDecimal("8.00"),
                0,
                new BigDecimal("85.00"),
                new BigDecimal("82.00"),
                new BigDecimal("88.00"),
                "CSE",
                2027,
                80
        );
        when(studentQueryService.getStudentFacts("student@institute.ac.in")).thenReturn(facts);

        EligibilityResult result = offerPathService.explainDriveEligibility(driveId, "student@institute.ac.in");

        assertThat(result.driveId()).isEqualTo(driveId);
        assertThat(result.eligible()).isTrue();
        assertThat(result.rules()).hasSize(4);
        assertThat(result.rules().stream().allMatch(r -> r.status() == RuleStatus.PASS)).isTrue();
    }

    @Test
    void simulateWhatIfUnlocksIneligibleDriveWhenOverrideSatisfiesCriteria() {
        Long driveId = 101L;
        Drive drive = mockDrive(driveId, "Dream Systems", "Cloud Engineer");
        when(driveRepository.findByIdAndDeletedFalse(driveId)).thenReturn(Optional.of(drive));

        EligibilityCriteria criteria = new EligibilityCriteria(driveId);
        EligibilityCriteria spyCrit = spy(criteria);
        doReturn(new BigDecimal("8.50")).when(spyCrit).getMinCgpa(); // Student has 8.00 (fails initially)
        doReturn(0).when(spyCrit).getMaxBacklogs();
        doReturn(new BigDecimal("75.00")).when(spyCrit).getMinAttendance();
        doReturn("CSE").when(spyCrit).getAllowedBranches();

        when(criteriaRepository.findByDriveIdAndDeletedFalse(driveId)).thenReturn(Optional.of(spyCrit));

        StudentFacts currentFacts = new StudentFacts(
                new BigDecimal("8.00"),
                0,
                new BigDecimal("85.00"),
                new BigDecimal("82.00"),
                new BigDecimal("80.00"),
                "CSE",
                2027,
                70
        );
        when(studentQueryService.getStudentFacts("student@institute.ac.in")).thenReturn(currentFacts);

        // Simulation override: Student tests what happens if CGPA rises to 8.60
        SimulationRequest req = new SimulationRequest(
                List.of(driveId),
                null,
                new BigDecimal("8.60"),
                null,
                null
        );

        SimulationResponse response = offerPathService.simulate(req, "student@institute.ac.in");

        assertThat(response.actualResults().get(0).eligible()).isFalse();
        assertThat(response.simulatedResults().get(0).eligible()).isTrue();
        assertThat(response.newlyUnlockedDrives()).hasSize(1);
        assertThat(response.newlyUnlockedDrives().get(0).companyName()).isEqualTo("Dream Systems");
        assertThat(response.newlyUnlockedDrives().get(0).changedRules()).hasSize(1);
        assertThat(response.newlyUnlockedDrives().get(0).changedRules().get(0).rule()).isEqualTo("Minimum CGPA");
        assertThat(response.newlyUnlockedDrives().get(0).changedRules().get(0).fromStatus()).isEqualTo("FAIL");
        assertThat(response.newlyUnlockedDrives().get(0).changedRules().get(0).toStatus()).isEqualTo("PASS");
    }

    @Test
    void oneRuleAwayIdentifiesStudentsWithExactlyOneUnmetRequirement() {
        Long driveId = 102L;
        Drive drive = mockDrive(driveId, "Consulting Partners", "Associate");
        when(driveRepository.findByIdAndDeletedFalse(driveId)).thenReturn(Optional.of(drive));

        EligibilityCriteria criteria = new EligibilityCriteria(driveId);
        EligibilityCriteria spyCrit = spy(criteria);
        doReturn(new BigDecimal("7.00")).when(spyCrit).getMinCgpa();
        doReturn(0).when(spyCrit).getMaxBacklogs();
        doReturn("CSE,IT").when(spyCrit).getAllowedBranches();

        when(criteriaRepository.findByDriveIdAndDeletedFalse(driveId)).thenReturn(Optional.of(spyCrit));

        // Student 1: Eligible (CGPA 7.5, Backlogs 0, Branch CSE)
        StudentProfile s1 = createProfile(1L, "Eligible Student", "CS-01", "s1@institute.ac.in", new BigDecimal("7.50"), 0, "CSE");

        // Student 2: Exactly 1 rule away (CGPA 6.80, Backlogs 0, Branch CSE) -> Fails only CGPA
        StudentProfile s2 = createProfile(2L, "One Rule Away Student", "CS-02", "s2@institute.ac.in", new BigDecimal("6.80"), 0, "CSE");

        // Student 3: Two rules away (CGPA 6.50, Backlogs 2, Branch CSE) -> Fails 2 rules (not counted in one-rule-away)
        StudentProfile s3 = createProfile(3L, "Two Rules Away Student", "CS-03", "s3@institute.ac.in", new BigDecimal("6.50"), 2, "CSE");

        when(studentProfileRepository.findAllByDeletedFalse()).thenReturn(List.of(s1, s2, s3));

        OneRuleAwayResponse res = offerPathService.getOneRuleAway(driveId);

        assertThat(res.totalAssessed()).isEqualTo(3);
        assertThat(res.eligibleCount()).isEqualTo(1);
        assertThat(res.oneRuleAwayCount()).isEqualTo(1);
        assertThat(res.candidates()).hasSize(1);
        assertThat(res.candidates().get(0).fullName()).isEqualTo("One Rule Away Student");
        assertThat(res.candidates().get(0).unmetRule()).isEqualTo("Minimum CGPA");
        assertThat(res.unmetRuleBreakdown().get("Minimum CGPA")).isEqualTo(1);
    }
}
