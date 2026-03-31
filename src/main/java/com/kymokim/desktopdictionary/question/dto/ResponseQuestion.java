package com.kymokim.desktopdictionary.question.dto;

import com.kymokim.desktopdictionary.answer.entity.Answer;
import com.kymokim.desktopdictionary.question.entity.Question;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResponseQuestion {

    @Getter
    @Builder
    public static class GetQuestionDto{
        private Long id;
        private LocalDateTime creationDate;
        private String title;
        private String content;
        private String writerName;
        private String category;
        private Boolean isWriter;
        private Long answerCount;
        private Long viewCount;
        private List<AnswerListDto> answerList;

        public static GetQuestionDto toDto(Question question, Boolean isWriter, String email){

            List<AnswerListDto> answerList = new ArrayList<>();
            if(!question.getAnswerList().isEmpty())
                question.getAnswerList().stream().forEach(answer -> answerList.add(AnswerListDto.toDto(answer, email)));

            return GetQuestionDto.builder()
                    .id(question.getId())
                    .creationDate(question.getCreationDate())
                    .title(question.getTitle())
                    .content(question.getContent())
                    .writerName(question.getWriterName())
                    .category(question.getCategory())
                    .isWriter(isWriter)
                    .answerCount(question.getAnswerCount())
                    .viewCount(question.getViewCount())
                    .answerList(answerList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class AnswerListDto{
        private Long id;
        private String writerName;
        private String answerContent;
        private LocalDateTime creationDate;
        private Boolean isWriter;

        public static AnswerListDto toDto(Answer answer, String email){

            Boolean isWriter = false;
            if (answer.getWriterEmail().equals(email))
                isWriter = true;

            return AnswerListDto.builder()
                    .id(answer.getId())
                    .writerName(answer.getWriterName())
                    .answerContent(answer.getAnswerContent())
                    .creationDate(answer.getCreationDate())
                    .isWriter(isWriter)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetAllQuestionDto{
        private Long id;
        private String title;
        private String writerName;
        private String category;
        private Long answerCount;
        private Long viewCount;

        public static GetAllQuestionDto toDto(Question question){
            return GetAllQuestionDto.builder()
                    .id(question.getId())
                    .title(question.getTitle())
                    .writerName(question.getWriterName())
                    .category(question.getCategory())
                    .answerCount(question.getAnswerCount())
                    .viewCount(question.getViewCount())
                    .build();
        }
    }
}
