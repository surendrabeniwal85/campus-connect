package com.placementassistant.backend.auth.service;

import com.placementassistant.backend.auth.principal.AuthenticatedUserPrincipal;
import java.util.List;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class PasswordHashAuthenticationProvider implements AuthenticationProvider {

	private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password.";

	private final UserDetailsService userDetailsService;
	private final PasswordHashingService passwordHashingService;
	private final PasswordHashUpgradeService passwordHashUpgradeService;

	public PasswordHashAuthenticationProvider(
			UserDetailsService userDetailsService,
			PasswordHashingService passwordHashingService,
			PasswordHashUpgradeService passwordHashUpgradeService) {
		this.userDetailsService = userDetailsService;
		this.passwordHashingService = passwordHashingService;
		this.passwordHashUpgradeService = passwordHashUpgradeService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		if (!(authentication instanceof UsernamePasswordAuthenticationToken request)
				|| !(authentication.getPrincipal() instanceof String username)
				|| !(authentication.getCredentials() instanceof String rawPassword)) {
			throw invalidCredentials();
		}

		request.eraseCredentials();

		UserDetails userDetails;
		try {
			userDetails = userDetailsService.loadUserByUsername(username);
		} catch (UsernameNotFoundException exception) {
			throw invalidCredentials();
		}

		if (!passwordHashingService.verifyPassword(rawPassword, userDetails.getPassword())) {
			throw invalidCredentials();
		}
		if (passwordHashingService.needsRehash(userDetails.getPassword())) {
			String upgradedHash = passwordHashingService.hashPassword(rawPassword);
			this.passwordHashUpgradeService.upgradePasswordHash(
					userDetails.getUsername(),
					userDetails.getPassword(),
					upgradedHash);
		}

		List<String> roleNames = userDetails.getAuthorities().stream()
				.map(authority -> authority.getAuthority())
				.toList();
		AuthenticatedUserPrincipal principal =
				new AuthenticatedUserPrincipal(userDetails.getUsername(), roleNames);
		UsernamePasswordAuthenticationToken authenticated =
				UsernamePasswordAuthenticationToken.authenticated(
						principal,
						null,
						roleNames.stream()
								.map(SimpleGrantedAuthority::new)
								.toList());
		authenticated.setDetails(authentication.getDetails());
		return authenticated;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private BadCredentialsException invalidCredentials() {
		return new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE);
	}
}
