package com.kymokim.desktopdictionary.question.entity;

import com.kymokim.desktopdictionary.answer.entity.Answer;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "question")
@Entity
@Getter
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "writer_email")
    private String writerEmail;

    @Column(name = "writer_name")
    private String writerName;

    @Column(name = "category")
    private String category;

    @Column(name = "answerCount")
    private Long answerCount = 0L;

    @Column(name = "viewCount")
    private Long viewCount = 0L;

    @Column(name = "isReported")
    private Boolean isReported = false;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Answer> answerList = new ArrayList<>();

    @Builder
    public Question(String title, String content, String writerEmail, String writerName, String category){
        this.title = title;
        this.content = content;
        this.writerEmail = writerEmail;
        this.writerName = writerName;
        this.category = category;
    }

    public void update(String title, String content, String category){
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void addAnswer(Answer answer){
        this.answerList.add(answer);
    }

    public void increaseAnswerCount(){
        this.answerCount++;
    }

    public void decreaseAnswerCount(){
        this.answerCount--;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

    public void report(){
        this.isReported = true;
    }

    public void unReport(){
        this.isReported = false;
    }
}
