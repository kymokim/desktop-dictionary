package com.kymokim.desktopdictionary.comment.dto;

import com.kymokim.desktopdictionary.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

public class ResponseComment {

    @Builder
    @Getter
    public static class GetCommentDto {
        private Long id;
        private String writerName;
        private String commentContent;

        public static GetCommentDto toDto(Comment comment) {
            return GetCommentDto.builder()
                    .id(comment.getId())
                    .writerName(comment.getWriterName())
                    .commentContent(comment.getCommentContent())
                    .build();
        }
    }
}
