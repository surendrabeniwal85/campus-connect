package com.placementassistant.backend.auth.controller;

import com.placementassistant.backend.auth.dto.CreateAccountRequest;
import com.placementassistant.backend.auth.dto.UserResponse;
import com.placementassistant.backend.auth.entity.Role;
import com.placementassistant.backend.auth.entity.User;
import com.placementassistant.backend.auth.repository.RoleRepository;
import com.placementassistant.backend.auth.repository.UserRepository;
import com.placementassistant.backend.auth.service.PasswordHashingService;
import com.placementassistant.backend.common.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashingService passwordHashingService;

    public AccountController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHashingService = passwordHashingService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listAccounts(Authentication authentication) {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = users.stream().map(user -> {
            List<String> roleNames = user.getRoles().stream().map(Role::getName).toList();
            String portal = roleNames.stream().anyMatch(r -> r.contains("STUDENT") || r.contains("TEACHER") || r.contains("HOD"))
                    ? "INSTITUTE" : "PLACEMENT";
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getDisplayName(),
                    roleNames,
                    portal,
                    user.isEnabled(),
                    user.getCreatedAt() != null ? user.getCreatedAt().toString() : Instant.now().toString()
            );
        }).toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody CreateAccountRequest createRequest,
            HttpServletRequest request) {
        String email = createRequest.email().trim().toLowerCase();
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(
                    Instant.now(),
                    HttpStatus.CONFLICT.value(),
                    "USER_ALREADY_EXISTS",
                    "An account with this email address already exists.",
                    request.getRequestURI()));
        }

        String roleName = createRequest.role().startsWith("ROLE_")
                ? createRequest.role()
                : "ROLE_" + createRequest.role();

        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(
                    Instant.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "INVALID_ROLE",
                    "Invalid role: " + roleName,
                    request.getRequestURI()));
        }

        String hashedPassword = passwordHashingService.hashPassword(createRequest.password());
        User user = new User(createRequest.displayName().trim(), email, hashedPassword);
        user.addRole(roleOpt.get());
        User savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Account created successfully.",
                "userId", savedUser.getId(),
                "email", savedUser.getEmail()
        ));
    }

    @PutMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> toggleStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body,
            HttpServletRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(
                    Instant.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "USER_NOT_FOUND",
                    "Account not found.",
                    request.getRequestURI()));
        }

        User user = userOpt.get();
        boolean newEnabled = body.getOrDefault("enabled", !user.isEnabled());
        user.setEnabled(newEnabled);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Account status updated.",
                "id", id,
                "enabled", newEnabled
        ));
    }
}
