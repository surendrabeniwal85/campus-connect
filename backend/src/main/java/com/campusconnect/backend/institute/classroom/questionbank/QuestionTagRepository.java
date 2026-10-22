package com.campusconnect.backend.institute.classroom.questionbank;

import org.springframework.data.jpa.repository.JpaRepository; import java.util.*; public interface QuestionTagRepository extends JpaRepository<QuestionTag,Long>{List<QuestionTag> findAllByQuestionId(Long questionId);}