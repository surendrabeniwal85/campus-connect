package com.campusconnect.backend.auth.controller;

import com.campusconnect.backend.auth.dto.LoginRequest;
import com.campusconnect.backend.auth.dto.LoginResponse;
import com.campusconnect.backend.auth.principal.AuthenticatedUserPrincipal;
import com.campusconnect.backend.common.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private static final String AUTHENTICATION_FAILURE_MESSAGE = "Invalid username or password.";

	private final AuthenticationManager authenticationManager;
	private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
	private final SecurityContextRepository securityContextRepository;
	private final LogoutHandler logoutHandler;
	private final SecurityContextHolderStrategy securityContextHolderStrategy =
			SecurityContextHolder.getContextHolderStrategy();

	public AuthController(
			AuthenticationManager authenticationManager,
			SessionAuthenticationStrategy sessionAuthenticationStrategy,
			SecurityContextRepository securityContextRepository,
			LogoutHandler logoutHandler) {
		this.authenticationManager = authenticationManager;
		this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
		this.securityContextRepository = securityContextRepository;
		this.logoutHandler = logoutHandler;
	}

	@GetMapping("/csrf")
	public ResponseEntity<Void> csrf(CsrfToken csrfToken) {
		csrfToken.getToken();
		return ResponseEntity.ok().build();
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
			Authentication authentication,
			HttpServletRequest request,
			HttpServletResponse response) {
		this.logoutHandler.logout(request, response, authentication);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(
			@Valid @RequestBody LoginRequest loginRequest,
			HttpServletRequest request,
			HttpServletResponse response) {
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					UsernamePasswordAuthenticationToken.unauthenticated(
							loginRequest.email(),
							loginRequest.password()));
		} catch (AuthenticationException exception) {
			ApiErrorResponse error = new ApiErrorResponse(
					Instant.now(),
					HttpStatus.UNAUTHORIZED.value(),
					"AUTHENTICATION_FAILED",
					AUTHENTICATION_FAILURE_MESSAGE,
					request.getRequestURI());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}

		sessionAuthenticationStrategy.onAuthentication(authentication, request, response);
		SecurityContext context = securityContextHolderStrategy.createEmptyContext();
		context.setAuthentication(authentication);
		securityContextHolderStrategy.setContext(context);
		securityContextRepository.saveContext(context, request, response);

		AuthenticatedUserPrincipal principal =
				(AuthenticatedUserPrincipal) authentication.getPrincipal();
		return ResponseEntity.ok(new LoginResponse(principal.getName(), principal.roleNames()));
	}
}
