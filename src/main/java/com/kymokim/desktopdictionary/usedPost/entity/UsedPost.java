package com.kymokim.desktopdictionary.usedPost.entity;

import com.kymokim.desktopdictionary.comment.entity.Comment;
import com.kymokim.desktopdictionary.usedComment.entity.UsedComment;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "usedPost")
@Entity
@Getter
@NoArgsConstructor
public class UsedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_price")
    private int productPrice;

    @Column(name = "isSold")
    private Boolean isSold = false;

    @Column(name = "writer_email")
    private String writerEmail;

    @Column(name = "writer_name")
    private String writerName;

    @Column(name = "category")
    private String category;

    @Column(name = "isReported")
    private Boolean isReported = false;

    @OneToMany(mappedBy = "usedPost", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UsedComment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "usedPost", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UsedPostImage> imgUrlList = new ArrayList<>();

    @Builder
    public UsedPost(String title, String content, String productName, int productPrice,
                    String writerEmail, String writerName, String category){
        this.title = title;
        this.content = content;
        this.productName = productName;
        this.productPrice = productPrice;
        this.writerEmail = writerEmail;
        this.writerName = writerName;
        this.category = category;
    }

    public void update(String title, String content, String productName, int productPrice, String category){
        this.title = title;
        this.content = content;
        this.productName = productName;
        this.productPrice = productPrice;
        this.category = category;
    }

    public void addUsedComment(UsedComment usedComment){
        this.commentList.add(usedComment);
    }

    public void addImgUrlList(UsedPostImage usedPostImage){
        this.imgUrlList.add(usedPostImage);
    }

    public void report(){
        this.isReported = true;
    }

    public void unReport(){
        this.isReported = false;
    }

    public void sold(){
        this.isSold = true;
    }

    public void unSold(){
        this.isSold = false;
    }

}
