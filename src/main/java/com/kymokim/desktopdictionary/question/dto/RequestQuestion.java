package com.kymokim.desktopdictionary.question.dto;


import com.kymokim.desktopdictionary.question.entity.Question;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

public class RequestQuestion {
    @Data
    @Builder
    public static class WriteQuestionDto {
        @NotEmpty(message = "No title entered.")
        private String title;
        @NotEmpty(message = "No content entered.")
        private String content;
        @NotEmpty(message = "No category entered.")
        private String category;

        public static Question toEntity(WriteQuestionDto writeQuestionDto, String writerEmail, String writerName){
            return Question.builder()
                    .title(writeQuestionDto.getTitle())
                    .content(writeQuestionDto.getContent())
                    .writerEmail(writerEmail)
                    .writerName(writerName)
                    .category(writeQuestionDto.getCategory())
                    .build();
        }
    }


    @Data
    @Builder
    public static class UpdateQuestionDto {
        @NotEmpty(message = "No Question id entered.")
        private Long id;
        @NotEmpty(message = "No title entered.")
        private String title;
        @NotEmpty(message = "No content entered.")
        private String content;
        @NotEmpty(message = "No category entered.")
        private String category;

        public static Question toEntity(Question question, UpdateQuestionDto updateQuestionDto){
            question.update(updateQuestionDto.getTitle(), updateQuestionDto.getContent(), updateQuestionDto.getCategory());
            return question;
        }
    }

}
