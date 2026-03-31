package com.kymokim.desktopdictionary.article.dto;

import com.kymokim.desktopdictionary.article.entity.Article;
import com.kymokim.desktopdictionary.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResponseArticle {

    @Getter
    @Builder
    public static class GetArticleDto{
        private Long id;
        private LocalDateTime creationDate;
        private String title;
        private String content;
        private String writerName;
        private String category;
        private Boolean isWriter;
        private List<CommentListDto> commentList;
        private List<String> imgUrlList;

        public static GetArticleDto toDto(Article article, Boolean isWriter, String email){

            List<CommentListDto> commentList = new ArrayList<>();
            if(!article.getCommentList().isEmpty())
                article.getCommentList().stream().forEach(comment -> commentList.add(CommentListDto.toDto(comment, email)));

            List<String> imgUrlList = new ArrayList<>();
            if(!article.getImgUrlList().isEmpty())
                article.getImgUrlList().stream().forEach(articleImage -> imgUrlList.add(articleImage.getUrl()));

            return GetArticleDto.builder()
                    .id(article.getId())
                    .creationDate(article.getCreationDate())
                    .title(article.getTitle())
                    .content(article.getContent())
                    .writerName(article.getWriterName())
                    .category(article.getCategory())
                    .isWriter(isWriter)
                    .commentList(commentList)
                    .imgUrlList(imgUrlList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CommentListDto{
        private Long id;
        private String writerName;
        private String commentContent;
        private LocalDateTime creationDate;
        private Boolean isWriter;

        public static CommentListDto toDto(Comment comment, String email){

            Boolean isWriter = false;
            if (comment.getWriterEmail().equals(email))
                isWriter = true;

            return CommentListDto.builder()
                    .id(comment.getId())
                    .writerName(comment.getWriterName())
                    .commentContent(comment.getCommentContent())
                    .creationDate(comment.getCreationDate())
                    .isWriter(isWriter)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetAllArticleDto{
        private Long id;
        private String title;
        private String writerName;
        private String category;
        private String firstImgUrl;


        public static GetAllArticleDto toDto(Article article){

            String firstImgUrl = null;

            if(!article.getImgUrlList().isEmpty())
                firstImgUrl = article.getImgUrlList().getFirst().getUrl();

            return GetAllArticleDto.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .writerName(article.getWriterName())
                    .category(article.getCategory())
                    .firstImgUrl(firstImgUrl)
                    .build();
        }
    }
}
