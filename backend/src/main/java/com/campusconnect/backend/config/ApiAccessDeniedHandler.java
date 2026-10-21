package com.campusconnect.backend.config;

import com.campusconnect.backend.common.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.ObjectMapper;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	public ApiAccessDeniedHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void handle(
			HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException exception) throws IOException {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		this.objectMapper.writeValue(response.getOutputStream(), errorResponse(request));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodAccessDenied(
			AccessDeniedException exception,
			HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse(request));
	}

	private ApiErrorResponse errorResponse(HttpServletRequest request) {
		return new ApiErrorResponse(
				Instant.now(),
				HttpStatus.FORBIDDEN.value(),
				"ACCESS_DENIED",
				"You do not have permission to access this resource.",
				request.getRequestURI());
	}
}
