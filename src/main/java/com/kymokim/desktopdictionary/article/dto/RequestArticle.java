package com.kymokim.desktopdictionary.article.dto;


import com.kymokim.desktopdictionary.article.entity.Article;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class RequestArticle {
    @Data
    @Builder
    public static class WriteArticleDto {
        @NotEmpty(message = "No title entered.")
        private String title;
        @NotEmpty(message = "No content entered.")
        private String content;
        @NotEmpty(message = "No category entered.")
        private String category;

        public static Article toEntity(WriteArticleDto writeArticleDto, String writerEmail, String writerName){
            return Article.builder()
                    .title(writeArticleDto.getTitle())
                    .content(writeArticleDto.getContent())
                    .writerEmail(writerEmail)
                    .writerName(writerName)
                    .category(writeArticleDto.getCategory())
                    .build();
        }
    }


    @Data
    @Builder
    public static class UpdateArticleDto {
        @NotEmpty(message = "No Article id entered.")
        private Long id;
        @NotEmpty(message = "No title entered.")
        private String title;
        @NotEmpty(message = "No content entered.")
        private String content;
        @NotEmpty(message = "No category entered.")
        private String category;

        public static Article toEntity(Article article, UpdateArticleDto updateArticleDto){
            article.update(updateArticleDto.getTitle(), updateArticleDto.getContent(), updateArticleDto.getCategory());
            return article;
        }
    }

}
