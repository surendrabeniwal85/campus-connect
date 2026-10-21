package com.campusconnect.backend.institute.classroom.attendance;
import com.campusconnect.backend.institute.classroom.dto.*; import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import org.springframework.http.HttpStatus; import java.time.LocalDate; import java.util.*;
@RestController @RequestMapping("/api/v1/class-sessions") public class ClassSessionController {private final AttendanceService service;public ClassSessionController(AttendanceService s){service=s;}
 @GetMapping public List<ClassSession> list(@RequestParam LocalDate date,@RequestParam(required=false) Long teacherProfileId){return service.sessions(date,teacherProfileId);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public ClassSession create(@Valid @RequestBody CreateSessionRequest r){return service.createSession(r.timetableSlotId(),r.sessionDate(),r.startsAt(),r.endsAt());}
 @GetMapping("/{id}/attendance") public List<AttendanceRecord> records(@PathVariable Long id){return service.recordsForSession(id);}
 @PostMapping("/{id}/attendance") @ResponseStatus(HttpStatus.CREATED) public List<AttendanceRecord> mark(@PathVariable Long id,@Valid @RequestBody AttendanceMarkRequest r){return service.mark(id,r.teacherProfileId(),r.marks());}
}
