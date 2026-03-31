package com.kymokim.desktopdictionary.article.entity;

import com.kymokim.desktopdictionary.comment.entity.Comment;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "article")
@Entity
@Getter
@NoArgsConstructor
public class Article {

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

    @Column(name = "isReported")
    private Boolean isReported = false;

    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ArticleImage> imgUrlList = new ArrayList<>();

    @Builder
    public Article(String title, String content, String writerEmail, String writerName, String category){
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

    public void addComment(Comment comment){
        this.commentList.add(comment);
    }

    public void addImgUrlList(ArticleImage articleImage){
        this.imgUrlList.add(articleImage);
    }

    public void report(){
        this.isReported = true;
    }

    public void unReport(){
        this.isReported = false;
    }

}
