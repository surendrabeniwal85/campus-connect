package com.campusconnect.backend.config;

import com.campusconnect.backend.common.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class CsrfAccessDeniedHandler implements AccessDeniedHandler {

	private static final String CSRF_FAILURE_CODE = "CSRF_FAILED";
	private static final String CSRF_FAILURE_MESSAGE = "CSRF token is missing or invalid.";

	private final ObjectMapper objectMapper;
	private final AccessDeniedHandler defaultHandler = new AccessDeniedHandlerImpl();

	public CsrfAccessDeniedHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void handle(
			HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException exception) throws IOException, ServletException {
		if (!(exception instanceof MissingCsrfTokenException)
				&& !(exception instanceof InvalidCsrfTokenException)) {
			this.defaultHandler.handle(request, response, exception);
			return;
		}

		ApiErrorResponse error = new ApiErrorResponse(
				Instant.now(),
				HttpStatus.FORBIDDEN.value(),
				CSRF_FAILURE_CODE,
				CSRF_FAILURE_MESSAGE,
				request.getRequestURI());
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		this.objectMapper.writeValue(response.getOutputStream(), error);
	}
}
