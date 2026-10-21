package com.placementassistant.backend.institute.classroom.teacher;
import com.placementassistant.backend.institute.classroom.dto.CreateTeacherRequest; import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import org.springframework.http.HttpStatus; import java.util.*;
@RestController @RequestMapping("/api/v1/teachers") public class TeacherController {private final TeacherProfileService service;public TeacherController(TeacherProfileService s){service=s;}
 @GetMapping public List<TeacherProfile> list(@RequestParam(required=false) String department){return service.list(department);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public TeacherProfile create(@Valid @RequestBody CreateTeacherRequest r){return service.create(r.accountId(),r.displayName(),r.department(),r.designation(),r.hod());}
 @GetMapping("/{id}") public TeacherProfile get(@PathVariable Long id){return service.get(id);}
 @PatchMapping("/{id}") public TeacherProfile update(@PathVariable Long id,@Valid @RequestBody CreateTeacherRequest r){return service.update(id,r.displayName(),r.department(),r.designation(),r.hod());}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.softDelete(id);}
}