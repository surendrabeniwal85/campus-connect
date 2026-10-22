package com.placementassistant.backend.institute.hub.controller;

import com.placementassistant.backend.institute.records.service.StudentQueryService;
import com.placementassistant.backend.placement.drives.eligibility.StudentFacts;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/resume")
public class ResumeAtsController {

    private final StudentQueryService studentQueryService;

    public ResumeAtsController(StudentQueryService studentQueryService) {
        this.studentQueryService = studentQueryService;
    }

    public record ResumeAnalyzeRequest(String resumeText) {}

    public record SectionScore(String section, int score, int maxScore, String feedback) {}

    public record ResumeAtsResponse(
            int overallScore,
            String status, // NOT_UPLOADED, NEEDS_WORK, GOOD, STRONG
            List<SectionScore> breakdown,
            List<String> strengths,
            List<String> improvements,
            List<String> aiCoachNextActions
    ) {}

    @PostMapping("/analyze")
    public ResponseEntity<ResumeAtsResponse> analyzeResume(
            @RequestBody ResumeAnalyzeRequest request,
            Authentication authentication) {
        String text = request != null && request.resumeText() != null ? request.resumeText().trim() : "";
        if (text.isBlank()) {
            return ResponseEntity.ok(new ResumeAtsResponse(
                    0,
                    "NOT_UPLOADED",
                    List.of(),
                    List.of(),
                    List.of("Paste or upload your resume text to see your ATS score and personalized coaching tips."),
                    List.of("Upload your latest resume to calculate your baseline ATS score.",
                            "Review target company criteria to match required keywords.",
                            "Keep your attendance above 75% to maintain placement eligibility.")
            ));
        }

        String lower = text.toLowerCase(Locale.ROOT);
        List<SectionScore> breakdown = new ArrayList<>();
        List<String> strengths = new ArrayList<>();
        List<String> improvements = new ArrayList<>();

        // 1. Contact Details (Max 10)
        int contactScore = 0;
        boolean hasEmail = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").matcher(text).find();
        boolean hasPhone = Pattern.compile("\\+?\\d[\\d -]{8,14}\\d").matcher(text).find();
        boolean hasLink = lower.contains("linkedin.com") || lower.contains("github.com") || lower.contains("portfolio");
        if (hasEmail) contactScore += 4;
        if (hasPhone) contactScore += 3;
        if (hasLink) contactScore += 3;
        breakdown.add(new SectionScore("Contact Information", contactScore, 10,
                contactScore == 10 ? "Full contact details found (email, phone, links)." : "Ensure email, phone number, and LinkedIn or GitHub links are clearly stated."));
        if (contactScore >= 7) strengths.add("Clear and complete contact header.");
        else improvements.add("Add active professional links (LinkedIn or GitHub) to your header.");

        // 2. Key Sections (Max 30)
        int sectionScore = 0;
        boolean hasEducation = lower.contains("education") || lower.contains("academic") || lower.contains("degree");
        boolean hasExperience = lower.contains("experience") || lower.contains("projects") || lower.contains("project");
        boolean hasSkills = lower.contains("skills") || lower.contains("technologies") || lower.contains("competencies");
        boolean hasCertifications = lower.contains("certificat") || lower.contains("achievements") || lower.contains("awards") || lower.contains("extracurricular");
        if (hasEducation) sectionScore += 8;
        if (hasExperience) sectionScore += 10;
        if (hasSkills) sectionScore += 8;
        if (hasCertifications) sectionScore += 4;
        breakdown.add(new SectionScore("Key Sections", sectionScore, 30,
                sectionScore >= 26 ? "All core resume sections detected." : "Consider adding separate Education, Projects/Experience, and Skills sections."));
        if (sectionScore >= 24) strengths.add("Well-structured resume with recognized standard sections.");
        else improvements.add("Organize resume into distinct Education, Projects, and Technical Skills headings.");

        // 3. Keywords for Target Role (Max 35)
        int keywordScore = 0;
        String[] keywords = {
                "java", "spring", "spring boot", "sql", "mysql", "react", "typescript", "javascript",
                "python", "rest", "api", "git", "docker", "algorithms", "data structures", "html", "css"
        };
        int matchedKeywords = 0;
        for (String kw : keywords) {
            if (lower.contains(kw)) {
                matchedKeywords++;
            }
        }
        keywordScore = Math.min(35, matchedKeywords * 4);
        breakdown.add(new SectionScore("Technical Keywords", keywordScore, 35,
                matchedKeywords >= 6 ? "Strong technical keyword density (" + matchedKeywords + " skills identified)." : "Include more core technical keywords such as Java, Spring, SQL, React, Git, Data Structures."));
        if (keywordScore >= 24) strengths.add("Strong alignment with modern campus tech stack keywords.");
        else improvements.add("Include more programming languages, databases, and frameworks in your Skills section.");

        // 4. Measurable Results (Max 10)
        int metricsScore = 0;
        boolean hasPercentages = text.contains("%");
        boolean hasNumbers = Pattern.compile("\\b\\d+(k|\\+|ms|s|gb|users|records|queries)?\\b", Pattern.CASE_INSENSITIVE).matcher(text).find();
        if (hasPercentages) metricsScore += 5;
        if (hasNumbers) metricsScore += 5;
        breakdown.add(new SectionScore("Measurable Results", metricsScore, 10,
                metricsScore >= 8 ? "Good inclusion of metrics and quantifiable project outcomes." : "Quantify your achievements (e.g., 'improved query speed by 30%', 'handled 500+ users')."));
        if (metricsScore >= 5) strengths.add("Includes measurable results and impact indicators.");
        else improvements.add("Add numbers and percentages to demonstrate real impact in your projects.");

        // 5. Length & Format (Max 10)
        int lengthScore = 0;
        int wordCount = text.split("\\s+").length;
        if (wordCount >= 150 && wordCount <= 800) {
            lengthScore = 10;
        } else if (wordCount > 800) {
            lengthScore = 6;
        } else {
            lengthScore = Math.max(3, wordCount / 30);
        }
        breakdown.add(new SectionScore("Length & Concision", lengthScore, 10,
                lengthScore == 10 ? "Ideal length for a campus placement resume (" + wordCount + " words)." : "Aim for 300 to 600 words covering 1 single page."));

        // 6. Action Verbs (Max 5)
        int verbScore = 0;
        String[] verbs = {"developed", "designed", "implemented", "engineered", "optimized", "built", "created", "led", "automated"};
        int verbCount = 0;
        for (String v : verbs) {
            if (lower.contains(v)) verbCount++;
        }
        verbScore = Math.min(5, verbCount * 2);
        breakdown.add(new SectionScore("Action Verbs", verbScore, 5,
                verbScore >= 4 ? "Strong use of action verbs." : "Start bullet points with impactful verbs like Engineered, Optimized, Implemented."));

        int totalScore = contactScore + sectionScore + keywordScore + metricsScore + lengthScore + verbScore;
        totalScore = Math.min(100, Math.max(0, totalScore));

        String status = totalScore >= 75 ? "STRONG" : (totalScore >= 50 ? "GOOD" : "NEEDS_WORK");

        // Real data AI Coach 3 next actions
        List<String> coachActions = new ArrayList<>();
        StudentFacts facts = null;
        if (authentication != null && authentication.isAuthenticated()) {
            facts = studentQueryService.getStudentFacts(authentication.getName());
        }

        if (totalScore < 75) {
            coachActions.add("Enhance your resume keywords: add Spring Boot, SQL, and Git to lift your ATS score from " + totalScore + " to 75+ (STRONG).");
        } else {
            coachActions.add("Resume ATS score is STRONG (" + totalScore + "/100). Focus on technical round questions in the Question Bank.");
        }

        if (facts != null && facts.activeBacklogs() != null && facts.activeBacklogs() > 0) {
            coachActions.add("You have " + facts.activeBacklogs() + " active backlog(s). Check criteria before applying to strict 0-backlog drives.");
        } else {
            coachActions.add("Check eligibility for upcoming drives: Northstar Systems and BluePeak Technologies are open for applications.");
        }

        coachActions.add("Practice Aptitude and Technical mock questions in the Classroom Question Bank to prepare for Round 1 evaluations.");

        return ResponseEntity.ok(new ResumeAtsResponse(
                totalScore,
                status,
                breakdown,
                strengths,
                improvements,
                coachActions
        ));
    }
}
