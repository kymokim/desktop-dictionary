package com.kymokim.desktopdictionary.usedPost.dto;


import com.kymokim.desktopdictionary.usedPost.entity.UsedPost;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

public class RequestUsedPost {
    @Data
    @Builder
    public static class WriteUsedPostDto {
        @NotEmpty(message = "No title entered.")
        private String title;
        @NotEmpty(message = "No content entered.")
        private String content;
        @NotEmpty(message = "No category entered.")
        private String category;
        @NotEmpty(message = "No productName entered.")
        private String productName;
        @NotEmpty(message = "No productPrice entered.")
        private int productPrice;

        public static UsedPost toEntity(WriteUsedPostDto writeUsedPostDto, String writerEmail, String writerName){
            return UsedPost.builder()
                    .title(writeUsedPostDto.getTitle())
                    .content(writeUsedPostDto.getContent())
                    .productName(writeUsedPostDto.getProductName())
                    .productPrice(writeUsedPostDto.getProductPrice())
                    .writerEmail(writerEmail)
                    .writerName(writerName)
                    .category(writeUsedPostDto.getCategory())
                    .build();
        }
    }


    @Data
    @Builder
    public static class UpdateUsedPostDto {
        @NotEmpty(message = "No UsedPost id entered.")
        private Long id;
        @NotEmpty(message = "No title entered.")
        private String title;
        @NotEmpty(message = "No content entered.")
        private String content;
        @NotEmpty(message = "No category entered.")
        private String category;
        @NotEmpty(message = "No productName entered.")
        private String productName;
        @NotEmpty(message = "No productPrice entered.")
        private int productPrice;

        public static UsedPost toEntity(UsedPost usedPost, UpdateUsedPostDto updateUsedPostDto){
            usedPost.update(updateUsedPostDto.getTitle(), updateUsedPostDto.getContent(), updateUsedPostDto.getProductName(), updateUsedPostDto.getProductPrice(), updateUsedPostDto.getCategory());
            return usedPost;
        }
    }

}
