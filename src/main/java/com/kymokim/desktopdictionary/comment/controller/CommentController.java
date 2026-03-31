package com.kymokim.desktopdictionary.comment.controller;

import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.common.dto.ResponseDto;
import com.kymokim.desktopdictionary.comment.dto.RequestComment;
import com.kymokim.desktopdictionary.comment.dto.ResponseComment;
import com.kymokim.desktopdictionary.comment.service.CommentService;
import com.kymokim.desktopdictionary.article.entity.Article;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @PostMapping("/write")
    public ResponseEntity<ResponseDto> writeComment(@RequestBody RequestComment.WriteCommentDto writeCommentDto, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        commentService.writeComment(writeCommentDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment written successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/get/{commentId}")
    public ResponseEntity<ResponseDto> getComment(@PathVariable("commentId") Long commentId) {
        ResponseComment.GetCommentDto response = commentService.getComment(commentId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Deprecated
    @GetMapping("/getByArticle/{articleId}")
    public ResponseEntity<ResponseDto> getCommentByArticleId(@PathVariable("articleId") Long articleId) {
        List<ResponseComment.GetCommentDto> response = commentService.getCommentByArticleId(articleId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateComment(@RequestBody RequestComment.UpdateCommentDto updateCommentDto, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        commentService.updateComment(updateCommentDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/getReportedComments")
    public ResponseEntity<ResponseDto> getReportedComments(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        List<ResponseComment.GetCommentDto> response = commentService.getReportedComments(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/report/{commentId}")
    public ResponseEntity<ResponseDto> reportComment(@PathVariable("commentId") Long commentId){
        commentService.reportComment(commentId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment reported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/unReport/{commentId}")
    public ResponseEntity<ResponseDto> unReportComment(@PathVariable("commentId") Long commentId, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        commentService.unReportComment(commentId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment unreported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<ResponseDto> deleteComment(@PathVariable("commentId") Long commentId, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        commentService.deleteComment(commentId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
