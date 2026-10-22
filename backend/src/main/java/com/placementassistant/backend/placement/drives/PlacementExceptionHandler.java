package com.placementassistant.backend.placement.drives;

import com.placementassistant.backend.common.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.placementassistant.backend.placement")
public class PlacementExceptionHandler {

        @ExceptionHandler(PlacementNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> notFound(PlacementNotFoundException e, HttpServletRequest r) {
                return build(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage(), r);
        }

        @ExceptionHandler(PlacementConflictException.class)
        public ResponseEntity<ApiErrorResponse> conflict(PlacementConflictException e, HttpServletRequest r) {
                return build(HttpStatus.CONFLICT, "CONFLICT", e.getMessage(), r);
        }

        private ResponseEntity<ApiErrorResponse> build(HttpStatus s, String code, String msg, HttpServletRequest r) {
                return ResponseEntity.status(s)
                                .body(new ApiErrorResponse(Instant.now(), s.value(), code, msg, r.getRequestURI()));
        }
}
