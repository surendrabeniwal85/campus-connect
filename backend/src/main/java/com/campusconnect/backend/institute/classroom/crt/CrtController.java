package com.campusconnect.backend.institute.classroom.crt;
import com.campusconnect.backend.institute.classroom.dto.*; import com.campusconnect.backend.institute.classroom.attendance.AttendanceStatus; import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import org.springframework.http.HttpStatus; import java.util.*;
@RestController @RequestMapping("/api/v1/crt") public class CrtController {private final CrtService service;public CrtController(CrtService s){service=s;}
 @GetMapping("/sessions") public List<CrtSession> list(){return service.list();}
 @PostMapping("/sessions") @ResponseStatus(HttpStatus.CREATED) public CrtSession create(@Valid @RequestBody CreateCrtRequest r){return service.create(r.title(),r.sessionDate(),r.trainer(),r.targetBranch(),r.targetSemester(),r.teacherProfileId());}
 @PostMapping("/sessions/{sessionId}/attendance") @ResponseStatus(HttpStatus.CREATED) public CrtAttendance mark(@PathVariable Long sessionId,@Valid @RequestBody CrtMarkRequest r){return service.mark(sessionId,r.studentId(),r.status());}
 @GetMapping("/students/{studentId}/summary") public Map<String,Object> summary(@PathVariable Long studentId){return service.studentSummary(studentId);}
}
