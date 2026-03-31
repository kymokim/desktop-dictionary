package com.kymokim.desktopdictionary.answer.dto;

import com.kymokim.desktopdictionary.answer.entity.Answer;
import com.kymokim.desktopdictionary.question.entity.Question;
import lombok.Builder;
import lombok.Data;


public class RequestAnswer {

    @Data
    @Builder
    public static class WriteAnswerDto {
        private String answerContent;
        private Long questionId;

        public static Answer toEntity(WriteAnswerDto writeAnswerDto, Question question, String writerEmail, String writerName) {
            return Answer.builder()
                    .writerEmail(writerEmail)
                    .writerName(writerName)
                    .answerContent(writeAnswerDto.getAnswerContent())
                    .question(question)
                    .build();
        }
    }




    @Data
    @Builder
    public static class UpdateAnswerDto {
        private Long id;
        private String answerContent;

        public static Answer toEntity(Answer answer, UpdateAnswerDto updateAnswerDto) {
            answer.update(updateAnswerDto.getAnswerContent());
            return answer;
        }
    }
}
