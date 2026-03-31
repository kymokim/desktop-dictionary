package com.kymokim.desktopdictionary.usedPost.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="usedPost_image")
@Entity
@Getter
@NoArgsConstructor
@Data
public class UsedPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "url")
    private String url;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usedPost_id", nullable = false)
    private UsedPost usedPost;

    @Builder
    public UsedPostImage(String url, UsedPost usedPost) {
        this.url = url;
        this.usedPost = usedPost;
    }

}
