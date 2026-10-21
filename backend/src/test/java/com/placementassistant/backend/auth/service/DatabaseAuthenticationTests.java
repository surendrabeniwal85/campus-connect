package com.campusconnect.backend.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.campusconnect.backend.auth.principal.AuthenticatedUserPrincipal;
import com.campusconnect.backend.auth.entity.Role;
import com.campusconnect.backend.auth.entity.User;
import com.campusconnect.backend.auth.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class DatabaseAuthenticationTests {

	private UserRepository userRepository;
	private DatabaseUserDetailsService userDetailsService;
	private PasswordHashingService passwordHashingService;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		userDetailsService = new DatabaseUserDetailsService(userRepository);
		passwordHashingService = new PasswordHashingService();
	}

	@Test
	void loadsEnabledUserAndMapsDatabaseRoleNamesDirectlyToAuthorities() {
		User user = enabledUserWithRole("ROLE_STUDENT");
		when(userRepository.findByEmailIgnoreCase("student@example.test"))
				.thenReturn(Optional.of(user));

		UserDetails details = userDetailsService.loadUserByUsername("student@example.test");

		assertThat(details.getUsername()).isEqualTo("student@example.test");
		assertThat(details.getPassword()).isEqualTo(user.getPasswordHash());
		assertThat(details.isEnabled()).isTrue();
		assertThat(details.getAuthorities()).extracting(GrantedAuthority::getAuthority)
				.containsExactly("ROLE_STUDENT");
	}

	@Test
	void disabledUserIsRejectedWithGenericMessage() {
		User user = enabledUserWithRole("ROLE_STUDENT");
		user.setEnabled(false);
		when(userRepository.findByEmailIgnoreCase("student@example.test"))
				.thenReturn(Optional.of(user));

		assertThatThrownBy(() -> userDetailsService.loadUserByUsername("student@example.test"))
				.isInstanceOf(UsernameNotFoundException.class)
				.hasMessage("Invalid username or password.");
	}

	@Test
	void unknownUserIsRejectedWithSameGenericMessage() {
		when(userRepository.findByEmailIgnoreCase("missing@example.test"))
				.thenReturn(Optional.empty());

		assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing@example.test"))
				.isInstanceOf(UsernameNotFoundException.class)
				.hasMessage("Invalid username or password.");
	}

	@Test
	void providerAuthenticatesUsingExistingPasswordHashingService() {
		String rawPassword = "valid account password";
		UserDetails userDetails = org.springframework.security.core.userdetails.User
				.withUsername("student@example.test")
				.password(passwordHashingService.hashPassword(rawPassword))
				.authorities("ROLE_STUDENT")
				.build();
		PasswordHashAuthenticationProvider provider =
				new PasswordHashAuthenticationProvider(username -> userDetails, passwordHashingService);
		UsernamePasswordAuthenticationToken request =
				UsernamePasswordAuthenticationToken.unauthenticated(
						"student@example.test",
						rawPassword);

		var authenticated = provider.authenticate(request);

		assertThat(authenticated.isAuthenticated()).isTrue();
		assertThat(authenticated.getPrincipal())
				.isEqualTo(new AuthenticatedUserPrincipal("student@example.test", java.util.List.of("ROLE_STUDENT")));
		assertThat(authenticated.getPrincipal()).isNotInstanceOf(UserDetails.class);
		assertThat(authenticated.getPrincipal().toString()).doesNotContain(userDetails.getPassword());
		assertThat(authenticated.getCredentials()).isNull();
		assertThat(authenticated.getAuthorities()).extracting(GrantedAuthority::getAuthority)
				.containsExactly("ROLE_STUDENT");
		assertThat(request.getCredentials()).isNull();
	}

	@Test
	void providerRejectsIncorrectAndUnknownCredentialsWithSameMessage() {
		String storedHash = passwordHashingService.hashPassword("correct password");
		UserDetails userDetails = org.springframework.security.core.userdetails.User
				.withUsername("student@example.test")
				.password(storedHash)
				.authorities("ROLE_STUDENT")
				.build();
		PasswordHashAuthenticationProvider provider =
				new PasswordHashAuthenticationProvider(username -> userDetails, passwordHashingService);
		PasswordHashAuthenticationProvider unknownUserProvider =
				new PasswordHashAuthenticationProvider(
						username -> {
							throw new UsernameNotFoundException("Internal lookup detail");
						},
						passwordHashingService);

		assertThatThrownBy(() -> provider.authenticate(
				UsernamePasswordAuthenticationToken.unauthenticated(
						"student@example.test",
						"incorrect password")))
				.isInstanceOf(BadCredentialsException.class)
				.hasMessage("Invalid username or password.");
		assertThatThrownBy(() -> unknownUserProvider.authenticate(
				UsernamePasswordAuthenticationToken.unauthenticated(
						"missing@example.test",
						"incorrect password")))
				.isInstanceOf(BadCredentialsException.class)
				.hasMessage("Invalid username or password.");
	}

	private User enabledUserWithRole(String roleName) {
		User user = new User(
				"Test Student",
				"student@example.test",
				passwordHashingService.hashPassword("stored password"));
		user.addRole(new Role(roleName));
		return user;
	}
}
