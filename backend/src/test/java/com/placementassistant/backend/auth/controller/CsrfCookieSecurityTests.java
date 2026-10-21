package com.campusconnect.backend.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.campusconnect.backend.auth.entity.Role;
import com.campusconnect.backend.auth.entity.User;
import com.campusconnect.backend.auth.repository.RoleRepository;
import com.campusconnect.backend.auth.repository.UserRepository;
import com.campusconnect.backend.auth.service.PasswordHashingService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(CsrfCookieSecurityTests.ProtectedRouteConfiguration.class)
class CsrfCookieSecurityTests {

	private static final String EMAIL = "csrf-cookie-test@example.test";
	private static final String RAW_PASSWORD = "csrf cookie test password";
	private static final String ROLE_NAME = "ROLE_CSRF_COOKIE_TEST";

	@LocalServerPort
	private int port;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordHashingService passwordHashingService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUpUser() {
		removeTestUser();
		Role role = roleRepository.saveAndFlush(new Role(ROLE_NAME));
		User user = new User("CSRF Cookie Test", EMAIL, passwordHashingService.hashPassword(RAW_PASSWORD));
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
	void csrfCookieIsReadableAndAuthenticatedSessionCookieIsHttpOnly() throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> bootstrap = client.send(
				HttpRequest.newBuilder(uri("/api/v1/auth/csrf")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(bootstrap.statusCode()).isEqualTo(200);
		List<String> csrfSetCookies = bootstrap.headers().allValues("Set-Cookie");
		String csrfSetCookie = csrfSetCookies.stream()
				.filter(value -> value.startsWith("XSRF-TOKEN="))
				.findFirst()
				.orElseThrow();
		assertThat(csrfSetCookie).doesNotContainIgnoringCase("HttpOnly");
		String csrfValue = csrfSetCookie.substring(
				"XSRF-TOKEN=".length(),
				csrfSetCookie.indexOf(';'));

		HttpRequest loginRequest = HttpRequest.newBuilder(uri("/api/v1/auth/login"))
				.header("Content-Type", APPLICATION_JSON.toString())
				.header("Cookie", "XSRF-TOKEN=" + csrfValue)
				.header("X-XSRF-TOKEN", csrfValue)
				.POST(HttpRequest.BodyPublishers.ofString("""
						{"email":"csrf-cookie-test@example.test","password":"csrf cookie test password"}
						"""))
				.build();
		HttpResponse<String> login = client.send(
				loginRequest,
				HttpResponse.BodyHandlers.ofString());

		assertThat(login.statusCode()).isEqualTo(200);
		assertThat(login.body()).contains(EMAIL, ROLE_NAME)
				.doesNotContain(RAW_PASSWORD);
		List<String> loginSetCookies = login.headers().allValues("Set-Cookie");
		String sessionSetCookie = loginSetCookies.stream()
				.filter(value -> value.startsWith("JSESSIONID="))
				.findFirst()
				.orElseThrow();
		assertThat(sessionSetCookie).containsIgnoringCase("HttpOnly");
	}

	@Test
	void logoutClearsCsrfAndSessionCookiesAndInvalidatesAuthentication() throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> bootstrap = client.send(
				HttpRequest.newBuilder(uri("/api/v1/auth/csrf")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		String csrfCookie = bootstrap.headers().allValues("Set-Cookie").stream()
				.filter(value -> value.startsWith("XSRF-TOKEN="))
				.findFirst()
				.orElseThrow()
				.split(";", 2)[0];
		String csrfValue = csrfCookie.substring("XSRF-TOKEN=".length());

		HttpRequest loginRequest = HttpRequest.newBuilder(uri("/api/v1/auth/login"))
				.header("Content-Type", APPLICATION_JSON.toString())
				.header("Cookie", csrfCookie)
				.header("X-XSRF-TOKEN", csrfValue)
				.POST(HttpRequest.BodyPublishers.ofString("""
						{"email":"csrf-cookie-test@example.test","password":"csrf cookie test password"}
						"""))
				.build();
		HttpResponse<String> login = client.send(
				loginRequest,
				HttpResponse.BodyHandlers.ofString());
		assertThat(login.statusCode()).isEqualTo(200);
		String sessionCookie = login.headers().allValues("Set-Cookie").stream()
				.filter(value -> value.startsWith("JSESSIONID="))
				.findFirst()
				.orElseThrow()
				.split(";", 2)[0];

		HttpResponse<String> authenticatedBeforeLogout = client.send(
				HttpRequest.newBuilder(uri("/api/v1/protected-test-route"))
						.header("Cookie", sessionCookie)
						.GET()
						.build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(authenticatedBeforeLogout.statusCode()).isEqualTo(200);

		HttpResponse<String> logout = client.send(
				HttpRequest.newBuilder(uri("/api/v1/auth/logout"))
						.header("Cookie", csrfCookie + "; " + sessionCookie)
						.header("X-XSRF-TOKEN", csrfValue)
						.POST(HttpRequest.BodyPublishers.noBody())
						.build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(logout.statusCode()).isEqualTo(204);
		assertThat(logout.body()).isEmpty();
		assertThat(logout.headers().allValues("Set-Cookie"))
				.anySatisfy(value -> assertThat(value)
						.startsWith("JSESSIONID=")
						.containsIgnoringCase("Expires=Thu, 01 Jan 1970"))
				.anySatisfy(value -> assertThat(value)
						.startsWith("XSRF-TOKEN=")
						.containsIgnoringCase("Expires=Thu, 01 Jan 1970"));

		HttpResponse<String> authenticatedAfterLogout = client.send(
				HttpRequest.newBuilder(uri("/api/v1/protected-test-route"))
						.header("Cookie", sessionCookie)
						.GET()
						.build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(authenticatedAfterLogout.statusCode()).isEqualTo(401);
	}

	private URI uri(String path) {
		return URI.create("http://localhost:" + this.port + path);
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

		@GetMapping("/api/v1/protected-test-route")
		String getProtected() {
			return "authenticated";
		}
	}
}
