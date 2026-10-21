package com.placementassistant.backend.institute.classroom;
import com.placementassistant.backend.institute.classroom.dto.ApiError; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestControllerAdvice(basePackages="com.placementassistant.backend.institute.classroom") public class ClassroomExceptionHandler {
 @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<ApiError> badInput(IllegalArgumentException e){return ResponseEntity.badRequest().body(new ApiError(400,e.getMessage(),Map.of()));}
 @ExceptionHandler(SecurityException.class) public ResponseEntity<ApiError> forbidden(SecurityException e){return ResponseEntity.status(403).body(new ApiError(403,e.getMessage(),Map.of()));}
 @ExceptionHandler(NoSuchElementException.class) public ResponseEntity<ApiError> missing(NoSuchElementException e){return ResponseEntity.status(404).body(new ApiError(404,e.getMessage(),Map.of()));}
 @ExceptionHandler(IllegalStateException.class) public ResponseEntity<ApiError> conflict(IllegalStateException e){return ResponseEntity.status(409).body(new ApiError(409,e.getMessage(),Map.of()));}
}
