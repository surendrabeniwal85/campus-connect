package com.campusconnect.backend.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

import com.campusconnect.backend.auth.entity.Role;
import com.campusconnect.backend.auth.entity.User;
import com.campusconnect.backend.auth.repository.RoleRepository;
import com.campusconnect.backend.auth.repository.UserRepository;
import com.campusconnect.backend.auth.service.PasswordHashingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthControllerTests.ProtectedRouteConfiguration.class)
class AuthControllerTests {

	private static final String EMAIL = "login-test@example.test";
	private static final String RAW_PASSWORD = "correct login password";
	private static final String ROLE_NAME = "ROLE_LOGIN_TEST";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordHashingService passwordHashingService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private String storedHash;

	@BeforeEach
	void setUpUser() {
		removeTestUser();
		Role role = roleRepository.saveAndFlush(new Role(ROLE_NAME));
		storedHash = passwordHashingService.hashPassword(RAW_PASSWORD);
		User user = new User("Login Test", EMAIL, storedHash);
		user.addRole(role);
		userRepository.saveAndFlush(user);
	}

	@AfterEach
	void removeTestUser() {
		jdbcTemplate.update(
				"delete from user_role where user_id in (select id from app_user where email = ?)",
				EMAIL);
		userRepository.findByEmailIgnoreCase(EMAIL).ifPresent(userRepository::delete);
		roleRepository.findByName(ROLE_NAME).ifPresent(roleRepository::delete);
	}

	@Test
	void validLoginReturnsOnlyEmailAndRolesAndPersistsSession() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
						.with(csrf())
						.contentType("application/json")
						.content("""
								{"email":"login-test@example.test","password":"correct login password"}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/json"))
				.andExpect(jsonPath("$.email").value(EMAIL))
				.andExpect(jsonPath("$.roles[0]").value(ROLE_NAME))
				.andExpect(jsonPath("$.*", hasSize(2)))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.passwordHash").doesNotExist())
				.andExpect(jsonPath("$.credentials").doesNotExist())
				.andReturn();

		String body = result.getResponse().getContentAsString();
		assertThat(body).doesNotContain(RAW_PASSWORD, storedHash);

		MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
		assertThat(session).isNotNull();
		mockMvc.perform(get("/api/v1/auth/test-protected").session(session))
				.andExpect(status().isOk())
				.andExpect(content().string("authenticated"));
	}

	@Test
	void logoutRequiresAuthenticationAndCsrfThenInvalidatesSession() throws Exception {
		MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
						.with(csrf())
						.contentType("application/json")
						.content("""
								{"email":"login-test@example.test","password":"correct login password"}
								"""))
				.andExpect(status().isOk())
				.andReturn();
		MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
		assertThat(session).isNotNull();

		mockMvc.perform(post("/api/v1/auth/logout").session(session))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/api/v1/auth/logout")
						.session(session)
						.with(csrf()))
				.andExpect(status().isNoContent())
				.andExpect(content().string(""));

		mockMvc.perform(get("/api/v1/auth/test-protected").session(session))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void unauthenticatedLogoutIsRejectedEvenWithValidCsrf() throws Exception {
		mockMvc.perform(post("/api/v1/auth/logout")
						.with(csrf()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void incorrectUnknownAndDisabledUsersReceiveSameGeneric401Envelope() throws Exception {
		String incorrectPasswordBody = login("""
				{"email":"login-test@example.test","password":"incorrect password"}
				""");
		String unknownUserBody = login("""
				{"email":"missing@example.test","password":"incorrect password"}
				""");

		User disabledUser = userRepository.findByEmailIgnoreCase(EMAIL).orElseThrow();
		disabledUser.setEnabled(false);
		userRepository.saveAndFlush(disabledUser);
		String disabledUserBody = login("""
				{"email":"login-test@example.test","password":"correct login password"}
				""");

		for (String body : new String[] {incorrectPasswordBody, unknownUserBody, disabledUserBody}) {
			assertThat(body).contains("\"status\":401", "\"code\":\"AUTHENTICATION_FAILED\"",
					"\"message\":\"Invalid username or password.\"");
			assertThat(body).doesNotContain(EMAIL, RAW_PASSWORD, storedHash, "disabled", "not found");
		}
		assertThat(unknownUserBody).contains("\"code\":\"AUTHENTICATION_FAILED\"",
				"\"message\":\"Invalid username or password.\"");
		assertThat(disabledUserBody).contains("\"code\":\"AUTHENTICATION_FAILED\"",
				"\"message\":\"Invalid username or password.\"");
	}

	@Test
	void invalidLoginRequestReturnsValidationEnvelope() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.with(csrf())
						.contentType("application/json")
						.content("{\"email\":\"not-an-email\",\"password\":\" \"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
				.andExpect(jsonPath("$.path").value("/api/v1/auth/login"))
				.andExpect(jsonPath("$.*", hasSize(5)));
	}

	@Test
	void csrfBootstrapIsPublicAndStateChangingRequestsStillRequireCsrf() throws Exception {
		mockMvc.perform(get("/api/v1/auth/csrf"))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		mockMvc.perform(post("/api/v1/auth/login")
						.with(csrf())
						.contentType("application/json")
						.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}

	@Test
	void stateChangingRequestWithoutCsrfTokenReturnsCommonForbiddenEnvelope() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType("application/json")
						.content("{}"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.status").value(403))
				.andExpect(jsonPath("$.code").value("CSRF_FAILED"))
				.andExpect(jsonPath("$.message").value("CSRF token is missing or invalid."))
				.andExpect(jsonPath("$.path").value("/api/v1/auth/login"))
				.andExpect(jsonPath("$.*", hasSize(5)));

		mockMvc.perform(post("/api/v1/auth/login")
						.with(csrf().useInvalidToken())
						.contentType("application/json")
						.content("{}"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("CSRF_FAILED"))
				.andExpect(jsonPath("$.*", hasSize(5)));
	}

	private String login(String json) throws Exception {
		return mockMvc.perform(post("/api/v1/auth/login")
						.with(csrf())
						.contentType("application/json")
						.content(json))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"))
				.andExpect(jsonPath("$.message").value("Invalid username or password."))
				.andExpect(jsonPath("$.path").value("/api/v1/auth/login"))
				.andExpect(jsonPath("$.*", hasSize(5)))
				.andReturn()
				.getResponse()
				.getContentAsString();
	}

	@TestConfiguration
	static class ProtectedRouteConfiguration {

		@Bean
		ProtectedRoute protectedRoute() {
			return new ProtectedRoute();
		}
	}

	@RestController
	static class ProtectedRoute {

		@GetMapping("/api/v1/auth/test-protected")
		String getProtected() {
			return "authenticated";
		}
	}
}
