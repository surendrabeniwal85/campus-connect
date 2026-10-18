package com.campusconnect.backend.common;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String VALIDATION_MESSAGE = "One or more fields are invalid.";
	private static final String MALFORMED_JSON_MESSAGE = "The request body is invalid.";
	private static final String INTERNAL_ERROR_MESSAGE = "An unexpected error occurred.";

	@ExceptionHandler({
			MethodArgumentNotValidException.class,
			HandlerMethodValidationException.class,
			BindException.class
	})
	public ResponseEntity<ApiErrorResponse> handleValidationException(
			Exception exception,
			HttpServletRequest request) {
		return errorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", VALIDATION_MESSAGE, request);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleMalformedJson(
			HttpMessageNotReadableException exception,
			HttpServletRequest request) {
		return errorResponse(HttpStatus.BAD_REQUEST, "MALFORMED_JSON", MALFORMED_JSON_MESSAGE, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
			Exception exception,
			HttpServletRequest request) {
		return errorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"INTERNAL_SERVER_ERROR",
				INTERNAL_ERROR_MESSAGE,
				request);
	}

	private ResponseEntity<ApiErrorResponse> errorResponse(
			HttpStatus status,
			String code,
			String message,
			HttpServletRequest request) {
		ApiErrorResponse body = new ApiErrorResponse(
				Instant.now(),
				status.value(),
				code,
				message,
				request.getRequestURI());
		return ResponseEntity.status(status).body(body);
	}
}
