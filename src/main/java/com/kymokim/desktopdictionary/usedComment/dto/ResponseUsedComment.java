package com.kymokim.desktopdictionary.usedComment.dto;

import com.kymokim.desktopdictionary.usedComment.entity.UsedComment;
import lombok.Builder;
import lombok.Getter;

public class ResponseUsedComment {

    @Builder
    @Getter
    public static class GetUsedCommentDto {
        private Long id;
        private String writerName;
        private String commentContent;

        public static GetUsedCommentDto toDto(UsedComment usedComment) {
            return GetUsedCommentDto.builder()
                    .id(usedComment.getId())
                    .writerName(usedComment.getWriterName())
                    .commentContent(usedComment.getCommentContent())
                    .build();
        }
    }
}
