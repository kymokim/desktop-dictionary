package com.kymokim.desktopdictionary.usedComment.dto;

import com.kymokim.desktopdictionary.usedPost.entity.UsedPost;
import com.kymokim.desktopdictionary.usedComment.entity.UsedComment;
import lombok.Builder;
import lombok.Data;


public class RequestUsedComment {

    @Data
    @Builder
    public static class WriteUsedCommentDto {
        private String commentContent;
        private Long usedPostId;

        public static UsedComment toEntity(WriteUsedCommentDto writeUsedCommentDto, UsedPost usedPost, String writerEmail, String writerName) {
            return UsedComment.builder()
                    .writerEmail(writerEmail)
                    .writerName(writerName)
                    .commentContent(writeUsedCommentDto.getCommentContent())
                    .usedPost(usedPost)
                    .build();
        }
    }




    @Data
    @Builder
    public static class UpdateUsedCommentDto {
        private Long id;
        private String commentContent;

        public static UsedComment toEntity(UsedComment usedComment, UpdateUsedCommentDto updateUsedCommentDto) {
            usedComment.update(updateUsedCommentDto.getCommentContent());
            return usedComment;
        }
    }
}
