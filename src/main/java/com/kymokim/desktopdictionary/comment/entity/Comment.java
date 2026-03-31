package com.kymokim.desktopdictionary.comment.entity;


import com.kymokim.desktopdictionary.article.entity.Article;
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
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "creation_date")
    private LocalDateTime creationDate =LocalDateTime.now();

    @Column(name = "writer_email")
    private String writerEmail;

    @Column(name = "writer_name")
    private String writerName;

    @Column(name = "comment_content")
    private String commentContent;

    @Column(name = "isReported")
    private Boolean isReported = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Builder
    public Comment(String writerEmail, String writerName, String commentContent, Article article) {
        this.writerName = writerName;
        this.writerEmail = writerEmail;
        this.commentContent = commentContent;
        this.article = article;
    }

    public void update(String commentContent) {
        this.commentContent = commentContent;
    }

    public void report(){
        this.isReported = true;
    }

    public void unReport(){
        this.isReported = false;
    }
}
