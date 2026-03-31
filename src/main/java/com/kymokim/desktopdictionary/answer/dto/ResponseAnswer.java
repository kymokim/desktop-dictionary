package com.kymokim.desktopdictionary.answer.dto;

import com.kymokim.desktopdictionary.answer.entity.Answer;
import lombok.Builder;
import lombok.Getter;

public class ResponseAnswer {

    @Builder
    @Getter
    public static class GetAnswerDto {
        private Long id;
        private String writerName;
        private String answerContent;

        public static GetAnswerDto toDto(Answer answer) {
            return GetAnswerDto.builder()
                    .id(answer.getId())
                    .writerName(answer.getWriterName())
                    .answerContent(answer.getAnswerContent())
                    .build();
        }
    }
}
