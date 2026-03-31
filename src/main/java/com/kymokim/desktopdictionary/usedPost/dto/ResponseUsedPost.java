package com.kymokim.desktopdictionary.usedPost.dto;

import com.kymokim.desktopdictionary.comment.entity.Comment;
import com.kymokim.desktopdictionary.usedComment.entity.UsedComment;
import com.kymokim.desktopdictionary.usedPost.entity.UsedPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResponseUsedPost {

    @Getter
    @Builder
    public static class GetUsedPostDto{
        private Long id;
        private LocalDateTime creationDate;
        private String title;
        private String content;
        private String productName;
        private int productPrice;
        private Boolean isSold;
        private String writerName;
        private String category;
        private Boolean isWriter;
        private List<CommentListDto> commentList;
        private List<String> imgUrlList;

        public static GetUsedPostDto toDto(UsedPost usedPost, Boolean isWriter, String email){

            List<CommentListDto> commentList = new ArrayList<>();
            if(!usedPost.getCommentList().isEmpty())
                usedPost.getCommentList().stream().forEach(usedComment -> commentList.add(CommentListDto.toDto(usedComment, email)));

            List<String> imgUrlList = new ArrayList<>();
            if(!usedPost.getImgUrlList().isEmpty())
                usedPost.getImgUrlList().stream().forEach(usedPostImage -> imgUrlList.add(usedPostImage.getUrl()));

            return GetUsedPostDto.builder()
                    .id(usedPost.getId())
                    .creationDate(usedPost.getCreationDate())
                    .title(usedPost.getTitle())
                    .content(usedPost.getContent())
                    .productName(usedPost.getProductName())
                    .productPrice(usedPost.getProductPrice())
                    .isSold(usedPost.getIsSold())
                    .writerName(usedPost.getWriterName())
                    .category(usedPost.getCategory())
                    .isWriter(isWriter)
                    .commentList(commentList)
                    .imgUrlList(imgUrlList)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CommentListDto{
        private Long id;
        private String writerName;
        private String commentContent;
        private LocalDateTime creationDate;
        private Boolean isWriter;

        public static CommentListDto toDto(UsedComment usedComment, String email){

            Boolean isWriter = false;
            if (usedComment.getWriterEmail().equals(email))
                isWriter = true;

            return CommentListDto.builder()
                    .id(usedComment.getId())
                    .writerName(usedComment.getWriterName())
                    .commentContent(usedComment.getCommentContent())
                    .creationDate(usedComment.getCreationDate())
                    .isWriter(isWriter)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetAllUsedPostDto{
        private Long id;
        private String title;
        private String writerName;
        private String productName;
        private int productPrice;
        private Boolean isSold;
        private String category;
        private String firstImgUrl;


        public static GetAllUsedPostDto toDto(UsedPost usedPost){

            String firstImgUrl = null;

            if(!usedPost.getImgUrlList().isEmpty())
                firstImgUrl = usedPost.getImgUrlList().getFirst().getUrl();

            return GetAllUsedPostDto.builder()
                    .id(usedPost.getId())
                    .title(usedPost.getTitle())
                    .writerName(usedPost.getWriterName())
                    .productName(usedPost.getProductName())
                    .productPrice(usedPost.getProductPrice())
                    .isSold(usedPost.getIsSold())
                    .category(usedPost.getCategory())
                    .firstImgUrl(firstImgUrl)
                    .build();
        }
    }
}
