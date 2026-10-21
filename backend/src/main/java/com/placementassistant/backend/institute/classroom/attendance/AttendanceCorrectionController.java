package com.placementassistant.backend.institute.classroom.attendance;
import com.placementassistant.backend.institute.classroom.dto.*; import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import org.springframework.http.HttpStatus;
@RestController @RequestMapping("/api/v1/attendance") public class AttendanceCorrectionController {private final AttendanceService service;public AttendanceCorrectionController(AttendanceService s){service=s;}
 @PostMapping("/{recordId}/corrections") @ResponseStatus(HttpStatus.CREATED) public AttendanceCorrection request(@PathVariable Long recordId,@Valid @RequestBody CorrectionRequest r){return service.requestCorrection(recordId,r.requestedStatus(),r.reason(),r.requestedByAccountId());}
 @PatchMapping("/corrections/{correctionId}") public AttendanceCorrection review(@PathVariable Long correctionId,@Valid @RequestBody ReviewCorrectionRequest r){return service.reviewCorrection(correctionId,r.approverAccountId(),r.approve());}
 @GetMapping("/students/{studentId}/summary") public java.util.Map<String,Object> summary(@PathVariable Long studentId){return service.studentSummary(studentId);}
}
