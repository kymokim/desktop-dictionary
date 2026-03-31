package com.kymokim.desktopdictionary.comment.dto;

import com.kymokim.desktopdictionary.comment.entity.Comment;
import com.kymokim.desktopdictionary.article.entity.Article;
import lombok.Builder;
import lombok.Data;


public class RequestComment {

    @Data
    @Builder
    public static class WriteCommentDto {
        private String commentContent;
        private Long articleId;

        public static Comment toEntity(WriteCommentDto writeCommentDto, Article article, String writerEmail, String writerName) {
            return Comment.builder()
                    .writerEmail(writerEmail)
                    .writerName(writerName)
                    .commentContent(writeCommentDto.getCommentContent())
                    .article(article)
                    .build();
        }
    }




    @Data
    @Builder
    public static class UpdateCommentDto {
        private Long id;
        private String commentContent;

        public static Comment toEntity(Comment comment, UpdateCommentDto updateCommentDto) {
            comment.update(updateCommentDto.getCommentContent());
            return comment;
        }
    }
}
