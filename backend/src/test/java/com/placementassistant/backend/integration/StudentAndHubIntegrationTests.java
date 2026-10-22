package com.placementassistant.backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.placementassistant.backend.JwtTestPropertySupport;
import com.placementassistant.backend.auth.principal.AuthenticatedUserPrincipal;
import com.placementassistant.backend.auth.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class StudentAndHubIntegrationTests extends JwtTestPropertySupport {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtTokenService jwtTokenService;

	private String createToken(String email, String role) {
		return jwtTokenService.issue(new AuthenticatedUserPrincipal(email, List.of(role)));
	}

	@Test
	void studentProfileMeReturnsSeedStudentData() throws Exception {
		String token = createToken("student@institute.ac.in", "ROLE_STUDENT");

		mockMvc.perform(get("/api/v1/students/me")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("student@institute.ac.in"))
				.andExpect(jsonPath("$.fullName").value("Surendra Beniwal"))
				.andExpect(jsonPath("$.rollNumber").value("CS2026-001"))
				.andExpect(jsonPath("$.cgpa").value(8.52))
				.andExpect(jsonPath("$.activeBacklogs").value(0))
				.andExpect(jsonPath("$.atsScore").value(82))
				.andExpect(jsonPath("$.finalYear").value(true));
	}

	@Test
	void studentProfileUnauthorizedWithoutToken() throws Exception {
		mockMvc.perform(get("/api/v1/students/me"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void newsFeedFiltersForFinalYearStudent() throws Exception {
		String token = createToken("student@institute.ac.in", "ROLE_STUDENT");

		mockMvc.perform(get("/api/v1/news")
				.header("Authorization", "Bearer " + token)
				.param("studentGraduationYear", "2027")
				.param("currentCgpa", "8.5")
				.param("department", "CSE"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	void resumeAtsAnalysisScoresCorrectly() throws Exception {
		String token = createToken("student@institute.ac.in", "ROLE_STUDENT");

		String resumeJson = """
				{
				  "resumeText": "Experienced software engineer with Java, Python, Spring Boot, MySQL, REST API, Git, Docker. Developed distributed microservices resulting in 40% performance improvement and reduced latency by 35ms. B.Tech Computer Science from Institute."
				}
				""";

		mockMvc.perform(post("/api/v1/resume/analyze")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(resumeJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.overallScore").isNumber())
				.andExpect(jsonPath("$.overallScore").value(org.hamcrest.Matchers.greaterThan(50)))
				.andExpect(jsonPath("$.breakdown").isArray())
				.andExpect(jsonPath("$.strengths").isArray())
				.andExpect(jsonPath("$.aiCoachNextActions").isArray());
	}

	@Test
	void studentSelfRegistrationRequiresValidStudentEmail() throws Exception {
		String invalidRegisterJson = """
				{
				  "fullName": "New Student",
				  "rollNumber": "CS2026-999",
				  "email": "invalid-email-domain@other.com",
				  "password": "Password123!"
				}
				""";

		mockMvc.perform(post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidRegisterJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Student registration requires an @institute.ac.in institutional email."));
	}

	@Test
	void studentSelfRegistrationSucceedsWithInstituteEmail() throws Exception {
		String registerJson = """
				{
				  "fullName": "Aman Verma",
				  "rollNumber": "CS2026-042",
				  "email": "aman.verma@institute.ac.in",
				  "department": "Computer Science and Engineering",
				  "branch": "CSE",
				  "semester": 7,
				  "password": "SecurePassword123!"
				}
				""";

		mockMvc.perform(post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(registerJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.email").value("aman.verma@institute.ac.in"));
	}
}
