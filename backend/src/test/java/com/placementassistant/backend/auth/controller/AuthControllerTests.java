package com.placementassistant.backend.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

import com.placementassistant.backend.JwtTestPropertySupport;
import com.placementassistant.backend.auth.entity.Role;
import com.placementassistant.backend.auth.entity.User;
import com.placementassistant.backend.auth.repository.RoleRepository;
import com.placementassistant.backend.auth.repository.UserRepository;
import com.placementassistant.backend.auth.service.JwtTokenService;
import com.placementassistant.backend.auth.service.PasswordHashingService;
import java.time.Instant;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthControllerTests.ProtectedRouteConfiguration.class)
class AuthControllerTests extends JwtTestPropertySupport {

	private static final String EMAIL = "login-test@example.test";
	private static final String RAW_PASSWORD = "correct login password";
	private static final String ROLE_NAME = "ROLE_STUDENT";

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

	@Autowired
	private JwtEncoder jwtEncoder;

	@Autowired
	private JwtDecoder jwtDecoder;

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
	void validLoginReturnsBearerTokenAndDoesNotCreateSession() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
						.contentType("application/json")
						.content("""
								{"portal":"INSTITUTE","email":"login-test@example.test","password":"correct login password"}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/json"))
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.expiresIn").value(900))
				.andExpect(jsonPath("$.email").value(EMAIL))
				.andExpect(jsonPath("$.roles[0]").value(ROLE_NAME))
				.andExpect(jsonPath("$.*", hasSize(5)))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.passwordHash").doesNotExist())
				.andExpect(jsonPath("$.credentials").doesNotExist())
				.andReturn();

		String body = result.getResponse().getContentAsString();
		String token = extractToken(body);
		Jwt decodedToken = jwtDecoder.decode(token);
		assertThat(body).doesNotContain(RAW_PASSWORD, storedHash);
		assertThat(decodedToken.getExpiresAt())
				.isEqualTo(decodedToken.getIssuedAt().plusSeconds(JwtTokenService.TOKEN_TTL_SECONDS));
		assertThat(token).matches("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+");
		assertThat(result.getResponse().getHeader("Set-Cookie")).isNull();
		assertThat(result.getRequest().getSession(false)).isNull();

		MvcResult protectedResult = mockMvc.perform(get("/api/v1/auth/test-protected")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(content().string(ROLE_NAME))
				.andReturn();
		assertThat(protectedResult.getRequest().getSession(false)).isNull();

		mockMvc.perform(get("/api/v1/auth/test-admin")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
				.andExpect(jsonPath("$.status").value(403))
				.andExpect(jsonPath("$.*", hasSize(5)));
	}

	@Test
	void missingBearerTokenReturnsStandardUnauthorizedEnvelope() throws Exception {
		mockMvc.perform(get("/api/v1/auth/test-protected"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status").value(401))
				.andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"))
				.andExpect(jsonPath("$.path").value("/api/v1/auth/test-protected"))
				.andExpect(jsonPath("$.*", hasSize(5)));
	}

	@Test
	void loginRejectsRoleFromTheOtherPortal() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType("application/json")
						.content("""
								{"portal":"PLACEMENT","email":"login-test@example.test","password":"correct login password"}
								"""))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
				.andExpect(jsonPath("$.status").value(403))
				.andExpect(jsonPath("$.message").value("Role is not permitted for the selected portal."));
	}

	@Test
	void tamperedAndExpiredTokensAreRejected() throws Exception {
		String validToken = extractToken(loginSuccessful(RAW_PASSWORD));
		String[] tokenParts = validToken.split("\\.");
		tokenParts[2] = (tokenParts[2].charAt(0) == 'A' ? "B" : "A") + tokenParts[2].substring(1);
		String tamperedToken = String.join(".", tokenParts);

		mockMvc.perform(get("/api/v1/auth/test-protected")
						.header("Authorization", "Bearer not-a-jwt"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));

		mockMvc.perform(get("/api/v1/auth/test-protected")
						.header("Authorization", "Bearer " + tamperedToken))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));

		mockMvc.perform(get("/api/v1/auth/test-protected")
						.header("Authorization", "Bearer " + expiredToken()))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
	}

	@Test
	void tokensWithUnexpectedIssuerAudienceOrMissingRolesAreRejected() throws Exception {
		mockMvc.perform(get("/api/v1/auth/test-protected")
						.header("Authorization", "Bearer " + token("unexpected-issuer", List.of("campus-connect-api"), true)))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/v1/auth/test-protected")
						.header("Authorization", "Bearer " + token("campus-connect-api", List.of("unexpected-audience"), true)))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/v1/auth/test-protected")
						.header("Authorization", "Bearer " + token("campus-connect-api", List.of("campus-connect-api"), false)))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/v1/auth/test-protected")
						.header("Authorization", "Bearer " + futureIssuedToken()))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));

		Instant issuedAt = Instant.now();
		mockMvc.perform(get("/api/v1/auth/test-protected")
						.header("Authorization", "Bearer " + token(
								"campus-connect-api",
								List.of("campus-connect-api"),
								true,
								issuedAt,
								issuedAt.plusSeconds(JwtTokenService.TOKEN_TTL_SECONDS + 1))))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
	}

	@Test
	void incorrectUnknownAndDisabledUsersReceiveSameGeneric401Envelope() throws Exception {
		String incorrectPasswordBody = login("""
				{"portal":"INSTITUTE","email":"login-test@example.test","password":"incorrect password"}
				""");
		String unknownUserBody = login("""
				{"portal":"INSTITUTE","email":"missing@example.test","password":"incorrect password"}
				""");

		User disabledUser = userRepository.findByEmailIgnoreCase(EMAIL).orElseThrow();
		disabledUser.setEnabled(false);
		userRepository.saveAndFlush(disabledUser);
		String disabledUserBody = login("""
				{"portal":"INSTITUTE","email":"login-test@example.test","password":"correct login password"}
				""");

		for (String body : new String[] {incorrectPasswordBody, unknownUserBody, disabledUserBody}) {
			assertThat(body).contains("\"status\":401", "\"code\":\"AUTHENTICATION_FAILED\"",
					"\"message\":\"Invalid username or password.\"");
			assertThat(body).doesNotContain(EMAIL, RAW_PASSWORD, storedHash, "disabled", "not found");
		}
		assertThat(unknownUserBody).contains("\"code\":\"AUTHENTICATION_FAILED\"");
		assertThat(disabledUserBody).contains("\"code\":\"AUTHENTICATION_FAILED\"");
	}

	@Test
	void invalidLoginRequestReturnsValidationEnvelope() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType("application/json")
						.content("{\"portal\":\"INSTITUTE\",\"email\":\"not-an-email\",\"password\":\" \"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
				.andExpect(jsonPath("$.path").value("/api/v1/auth/login"))
				.andExpect(jsonPath("$.*", hasSize(5)));
	}

	@Test
	void loginRequiresPortal() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType("application/json")
						.content("""
								{"email":"login-test@example.test","password":"correct login password"}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}

	private String login(String json) throws Exception {
		return mockMvc.perform(post("/api/v1/auth/login")
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

	private String loginSuccessful(String password) throws Exception {
		return mockMvc.perform(post("/api/v1/auth/login")
						.contentType("application/json")
						.content("""
								{"portal":"INSTITUTE","email":"login-test@example.test","password":"%s"}
								""".formatted(password)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
	}

	private String expiredToken() {
		Instant issuedAt = Instant.now().minusSeconds(1800);
		return token("campus-connect-api", List.of("campus-connect-api"), true,
				issuedAt, issuedAt.plusSeconds(900));
	}

	private String futureIssuedToken() {
		Instant issuedAt = Instant.now().plusSeconds(1);
		return token("campus-connect-api", List.of("campus-connect-api"), true,
				issuedAt, issuedAt.plusSeconds(900));
	}

	private String token(String issuer, List<String> audience, boolean includeRoles) {
		Instant issuedAt = Instant.now();
		return token(issuer, audience, includeRoles, issuedAt, issuedAt.plusSeconds(900));
	}

	private String token(
			String issuer,
			List<String> audience,
			boolean includeRoles,
			Instant issuedAt,
			Instant expiresAt) {
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(issuer)
				.subject(EMAIL)
				.audience(audience)
				.issuedAt(issuedAt)
				.expiresAt(expiresAt)
				.build();
		if (includeRoles) {
			claims = JwtClaimsSet.from(claims)
					.claim("roles", List.of(ROLE_NAME))
					.build();
		}
		return jwtEncoder.encode(JwtEncoderParameters.from(
				JwsHeader.with(MacAlgorithm.HS256).build(),
				claims)).getTokenValue();
	}

	private static String extractToken(String responseBody) {
		Matcher matcher = Pattern.compile("\"accessToken\":\"([^\"]+)\"").matcher(responseBody);
		assertThat(matcher.find()).isTrue();
		return matcher.group(1);
	}

	@TestConfiguration
	@EnableMethodSecurity
	static class ProtectedRouteConfiguration {

		@Bean
		ProtectedRoute protectedRoute() {
			return new ProtectedRoute();
		}
	}

	@RestController
	static class ProtectedRoute {

		@GetMapping("/api/v1/auth/test-protected")
		String getProtected(org.springframework.security.core.Authentication authentication) {
			return authentication.getAuthorities().stream()
					.map(authority -> authority.getAuthority())
					.filter(authority -> authority.startsWith("ROLE_"))
					.findFirst()
					.orElse("");
		}

		@PreAuthorize("hasRole('ADMIN')")
		@GetMapping("/api/v1/auth/test-admin")
		String getAdminOnly() {
			return "admin";
		}
	}
}
