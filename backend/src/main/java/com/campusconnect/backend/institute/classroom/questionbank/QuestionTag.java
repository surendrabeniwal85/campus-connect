package com.campusconnect.backend.institute.classroom.questionbank;


import com.campusconnect.backend.institute.classroom.entity.AuditableEntity; import jakarta.persistence.*;
@Entity @Table(name="question_tag",uniqueConstraints=@UniqueConstraint(name="uk_question_tag",columnNames={"question_id","tag"})) public class QuestionTag extends AuditableEntity { @Column(name="question_id",nullable=false) private Long questionId; @Column(nullable=false,length=60) private String tag; protected QuestionTag(){} public QuestionTag(Long questionId,String tag){this.questionId=questionId;this.tag=tag;} public Long getQuestionId(){return questionId;} public String getTag(){return tag;} }
