package com.campusconnect.backend.institute.classroom.questionbank;


import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service public class QuestionBankService {private final QuestionRepository repo; public QuestionBankService(QuestionRepository repo){this.repo=repo;}
 public List<Question> browse(String company,QuestionRoundType round,boolean includeUnapproved){if(includeUnapproved)return repo.findAll();if(company!=null&&!company.isBlank()&&round!=null)return repo.findAllByCompanyNameIgnoreCaseAndRoundTypeAndApprovedTrueAndDeletedFalse(company,round);if(company!=null&&!company.isBlank())return repo.findAllByCompanyNameIgnoreCaseAndApprovedTrueAndDeletedFalse(company);if(round!=null)return repo.findAllByRoundTypeAndApprovedTrueAndDeletedFalse(round);return repo.findAllByApprovedTrueAndDeletedFalse();}
 @Transactional public Question add(String company,QuestionRoundType round,String topic,QuestionDifficulty difficulty,String text,String hint,Long creator){if(company==null||company.isBlank()||round==null||topic==null||topic.isBlank()||difficulty==null||text==null||text.isBlank()||creator==null)throw new IllegalArgumentException("Required question fields are missing");return repo.save(new Question(company.trim(),round,topic.trim(),difficulty,text.trim(),hint,creator));}
 @Transactional public Question approve(Long id,Long approver){Question q=repo.findById(id).orElseThrow(()->new NoSuchElementException("Question not found"));q.approve(approver);return q;}
}
