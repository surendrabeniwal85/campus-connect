package com.placementassistant.backend.auth.controller;

import com.placementassistant.backend.auth.dto.LoginRequest;
import com.placementassistant.backend.auth.dto.LoginResponse;
import com.placementassistant.backend.auth.entity.RoleName;
import com.placementassistant.backend.auth.principal.AuthenticatedUserPrincipal;
import com.placementassistant.backend.auth.service.JwtTokenService;
import com.placementassistant.backend.common.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private static final String AUTHENTICATION_FAILURE_MESSAGE = "Invalid username or password.";

	private final AuthenticationManager authenticationManager;
	private final JwtTokenService jwtTokenService;

	public AuthController(
			AuthenticationManager authenticationManager,
			JwtTokenService jwtTokenService) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenService = jwtTokenService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(
			@Valid @RequestBody LoginRequest loginRequest,
			HttpServletRequest request) {
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

		AuthenticatedUserPrincipal principal =
				(AuthenticatedUserPrincipal) authentication.getPrincipal();
		if (!RoleName.allBelongToPortal(principal.roleNames(), loginRequest.portal())) {
			ApiErrorResponse error = new ApiErrorResponse(
					Instant.now(),
					HttpStatus.FORBIDDEN.value(),
					"ACCESS_DENIED",
					"Role is not permitted for the selected portal.",
					request.getRequestURI());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
		}
		return ResponseEntity.ok(new LoginResponse(
				this.jwtTokenService.issue(principal),
				"Bearer",
				this.jwtTokenService.expiresInSeconds(),
				principal.getName(),
				principal.roleNames()));
	}
}
