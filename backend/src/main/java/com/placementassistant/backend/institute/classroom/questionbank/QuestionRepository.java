package com.placementassistant.backend.institute.classroom.questionbank;


import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface QuestionRepository extends JpaRepository<Question,Long>{List<Question> findAllByApprovedTrueAndDeletedFalse(); List<Question> findAllByCompanyNameIgnoreCaseAndApprovedTrueAndDeletedFalse(String company); List<Question> findAllByRoundTypeAndApprovedTrueAndDeletedFalse(QuestionRoundType round); List<Question> findAllByCompanyNameIgnoreCaseAndRoundTypeAndApprovedTrueAndDeletedFalse(String company,QuestionRoundType round);}
