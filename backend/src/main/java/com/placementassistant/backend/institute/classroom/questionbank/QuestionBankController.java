package com.placementassistant.backend.institute.classroom.questionbank;
import com.placementassistant.backend.institute.classroom.dto.CreateQuestionRequest; import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import org.springframework.http.HttpStatus; import java.util.*;
@RestController @RequestMapping("/api/v1/question-bank") public class QuestionBankController {private final QuestionBankService service;public QuestionBankController(QuestionBankService s){service=s;}
 @GetMapping public List<Question> browse(@RequestParam(required=false) String company,@RequestParam(required=false) QuestionRoundType round,@RequestParam(defaultValue="false") boolean includeUnapproved){return service.browse(company,round,includeUnapproved);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Question add(@Valid @RequestBody CreateQuestionRequest r){return service.add(r.companyName(),r.roundType(),r.topic(),r.difficulty(),r.questionText(),r.answerHint(),r.createdByAccountId());}
 @PatchMapping("/{id}/approve") public Question approve(@PathVariable Long id,@RequestParam Long approverAccountId){return service.approve(id,approverAccountId);}
}
