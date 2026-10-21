package com.campusconnect.backend.institute.classroom.questionbank;


import com.campusconnect.backend.institute.classroom.entity.AuditableEntity; import jakarta.persistence.*;
@Entity @Table(name="question",indexes={@Index(name="idx_question_company_round",columnList="company_name,round_type")}) public class Question extends AuditableEntity {
 @Column(name="company_name",nullable=false,length=120) private String companyName; @Enumerated(EnumType.STRING) @Column(name="round_type",nullable=false,length=20) private QuestionRoundType roundType;
 @Column(nullable=false,length=100) private String topic; @Enumerated(EnumType.STRING) @Column(nullable=false,length=12) private QuestionDifficulty difficulty; @Column(nullable=false,columnDefinition="TEXT") private String questionText;
 @Column(name="answer_hint",columnDefinition="TEXT") private String answerHint; @Column(nullable=false) private boolean approved=false; @Column(name="created_by_account_id",nullable=false) private Long createdByAccountId; @Column(name="approved_by_account_id") private Long approvedByAccountId;
 protected Question(){} public Question(String company,QuestionRoundType round,String topic,QuestionDifficulty difficulty,String text,String hint,Long creator){this.companyName=company;this.roundType=round;this.topic=topic;this.difficulty=difficulty;this.questionText=text;this.answerHint=hint;this.createdByAccountId=creator;}
 public String getCompanyName(){return companyName;} public QuestionRoundType getRoundType(){return roundType;} public String getTopic(){return topic;} public QuestionDifficulty getDifficulty(){return difficulty;} public String getQuestionText(){return questionText;} public String getAnswerHint(){return answerHint;} public boolean isApproved(){return approved;} public Long getCreatedByAccountId(){return createdByAccountId;} public Long getApprovedByAccountId(){return approvedByAccountId;} public void approve(Long approver){approved=true;approvedByAccountId=approver;}
}
