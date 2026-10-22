package com.placementassistant.backend.auth.controller;

import com.placementassistant.backend.auth.dto.*;
import com.placementassistant.backend.auth.entity.Portal;
import com.placementassistant.backend.auth.entity.Role;
import com.placementassistant.backend.auth.entity.RoleName;
import com.placementassistant.backend.auth.entity.User;
import com.placementassistant.backend.auth.principal.AuthenticatedUserPrincipal;
import com.placementassistant.backend.auth.repository.RoleRepository;
import com.placementassistant.backend.auth.repository.UserRepository;
import com.placementassistant.backend.auth.service.JwtTokenService;
import com.placementassistant.backend.auth.service.PasswordHashingService;
import com.placementassistant.backend.common.ApiErrorResponse;
import com.placementassistant.backend.institute.records.entity.StudentProfile;
import com.placementassistant.backend.institute.records.repository.StudentProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private static final String AUTHENTICATION_FAILURE_MESSAGE = "Invalid username or password.";

	private final AuthenticationManager authenticationManager;
	private final JwtTokenService jwtTokenService;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final StudentProfileRepository studentProfileRepository;
	private final PasswordHashingService passwordHashingService;

	// In-memory token store: token -> {email, expiresAt}
	private record ResetTokenInfo(String email, Instant expiresAt) {}
	private final Map<String, ResetTokenInfo> resetTokens = new ConcurrentHashMap<>();

	public AuthController(
			AuthenticationManager authenticationManager,
			JwtTokenService jwtTokenService,
			UserRepository userRepository,
			RoleRepository roleRepository,
			StudentProfileRepository studentProfileRepository,
			PasswordHashingService passwordHashingService) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenService = jwtTokenService;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.studentProfileRepository = studentProfileRepository;
		this.passwordHashingService = passwordHashingService;
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

	@PostMapping("/register")
	@Transactional
	public ResponseEntity<?> register(
			@Valid @RequestBody RegisterRequest registerRequest,
			HttpServletRequest request) {
		String email = registerRequest.email().trim().toLowerCase();
		if (!email.endsWith("@institute.ac.in")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(
					Instant.now(),
					HttpStatus.BAD_REQUEST.value(),
					"INVALID_EMAIL_DOMAIN",
					"Student registration requires an @institute.ac.in institutional email.",
					request.getRequestURI()));
		}
		if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(
					Instant.now(),
					HttpStatus.CONFLICT.value(),
					"USER_ALREADY_EXISTS",
					"An account with this email address already exists.",
					request.getRequestURI()));
		}
		if (studentProfileRepository.findByRollNumberAndDeletedFalse(registerRequest.rollNumber().trim()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(
					Instant.now(),
					HttpStatus.CONFLICT.value(),
					"ROLL_NUMBER_EXISTS",
					"An account with this roll number already exists.",
					request.getRequestURI()));
		}

		String hashedPassword = passwordHashingService.hashPassword(registerRequest.password());
		User user = new User(registerRequest.fullName().trim(), email, hashedPassword);
		Role studentRole = roleRepository.findByName("ROLE_STUDENT")
				.orElseGet(() -> roleRepository.save(new Role("ROLE_STUDENT")));
		user.addRole(studentRole);
		User savedUser = userRepository.save(user);

		StudentProfile profile = new StudentProfile();
		profile.setUserId(savedUser.getId());
		profile.setRollNumber(registerRequest.rollNumber().trim());
		profile.setFullName(registerRequest.fullName().trim());
		profile.setEmail(email);
		if (registerRequest.department() != null && !registerRequest.department().isBlank()) {
			profile.setDepartment(registerRequest.department().trim());
		}
		if (registerRequest.branch() != null && !registerRequest.branch().isBlank()) {
			profile.setBranch(registerRequest.branch().trim());
		}
		if (registerRequest.semester() != null && registerRequest.semester() > 0) {
			profile.setSemester(registerRequest.semester());
		}
		studentProfileRepository.save(profile);

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("status", 201);
		result.put("message", "Student account registered successfully.");
		result.put("email", email);
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(
			@Valid @RequestBody ForgotPasswordRequest forgotRequest,
			HttpServletRequest request) {
		String email = forgotRequest.email().trim().toLowerCase();
		Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
		if (userOpt.isEmpty()) {
			// Do not leak user existence, or give a friendly message
			return ResponseEntity.ok(Map.of(
					"message", "If an account with this email exists, a reset token has been generated.",
					"resetToken", "DEMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
			));
		}

		String token = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		resetTokens.put(token, new ResetTokenInfo(email, Instant.now().plusSeconds(900))); // 15 mins

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("message", "Password reset token generated successfully.");
		result.put("resetToken", token);
		result.put("email", email);
		result.put("note", "In demo environment, the reset token is provided directly above for immediate verification.");
		return ResponseEntity.ok(result);
	}

	@PostMapping("/reset-password")
	@Transactional
	public ResponseEntity<?> resetPassword(
			@Valid @RequestBody ResetPasswordRequest resetRequest,
			HttpServletRequest request) {
		String token = resetRequest.token().trim();
		ResetTokenInfo info = resetTokens.get(token);
		if (info == null || Instant.now().isAfter(info.expiresAt())) {
			resetTokens.remove(token);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(
					Instant.now(),
					HttpStatus.BAD_REQUEST.value(),
					"INVALID_OR_EXPIRED_TOKEN",
					"The reset token is invalid or has expired.",
					request.getRequestURI()));
		}

		Optional<User> userOpt = userRepository.findByEmailIgnoreCase(info.email());
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(
					Instant.now(),
					HttpStatus.NOT_FOUND.value(),
					"USER_NOT_FOUND",
					"User account not found.",
					request.getRequestURI()));
		}

		User user = userOpt.get();
		String newHashed = passwordHashingService.hashPassword(resetRequest.newPassword());
		user.setPasswordHash(newHashed);
		userRepository.save(user);
		resetTokens.remove(token);

		return ResponseEntity.ok(Map.of("message", "Password has been successfully updated. Please sign in with your new password."));
	}

	@GetMapping("/me")
	@Transactional(readOnly = true)
	public ResponseEntity<?> getCurrentUser(Authentication authentication, HttpServletRequest request) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(
					Instant.now(),
					HttpStatus.UNAUTHORIZED.value(),
					"UNAUTHORIZED",
					"You must be signed in to view your profile.",
					request.getRequestURI()));
		}

		String email = authentication.getName();
		Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		User user = userOpt.get();
		List<String> roleNames = user.getRoles().stream().map(Role::getName).toList();
		String portal = roleNames.stream().anyMatch(r -> r.contains("STUDENT") || r.contains("TEACHER") || r.contains("HOD"))
				? "INSTITUTE" : "PLACEMENT";

		return ResponseEntity.ok(new UserResponse(
				user.getId(),
				user.getEmail(),
				user.getDisplayName(),
				roleNames,
				portal,
				user.isEnabled(),
				user.getCreatedAt() != null ? user.getCreatedAt().toString() : Instant.now().toString()
		));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
	}
}
