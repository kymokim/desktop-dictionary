package com.kymokim.desktopdictionary.answer.entity;


import com.kymokim.desktopdictionary.question.entity.Question;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table
@Entity
@Getter
@NoArgsConstructor
@Data
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "creation_date")
    private LocalDateTime creationDate =LocalDateTime.now();

    @Column(name = "writer_email")
    private String writerEmail;

    @Column(name = "writer_name")
    private String writerName;

    @Column(name = "answer_content")
    private String answerContent;

    @Column(name = "isReported")
    private Boolean isReported = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Builder
    public Answer(String writerEmail, String writerName, String answerContent, Question question) {
        this.writerName = writerName;
        this.writerEmail = writerEmail;
        this.answerContent = answerContent;
        this.question = question;
    }

    public void update(String answerContent) {
        this.answerContent = answerContent;
    }

    public void report(){
        this.isReported = true;
    }

    public void unReport(){
        this.isReported = false;
    }
}
