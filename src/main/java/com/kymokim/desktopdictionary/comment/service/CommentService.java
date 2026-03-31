package com.kymokim.desktopdictionary.comment.service;

import com.kymokim.desktopdictionary.auth.entity.Auth;
import com.kymokim.desktopdictionary.auth.repository.AuthRepository;
import com.kymokim.desktopdictionary.auth.security.JwtAuthToken;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.auth.security.role.Role;
import com.kymokim.desktopdictionary.comment.dto.RequestComment;
import com.kymokim.desktopdictionary.comment.dto.ResponseComment;
import com.kymokim.desktopdictionary.comment.entity.Comment;
import com.kymokim.desktopdictionary.comment.repository.CommentRepository;
import com.kymokim.desktopdictionary.article.entity.Article;
import com.kymokim.desktopdictionary.article.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;

    public void writeComment(RequestComment.WriteCommentDto writeCommentDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth writer = authRepository.findByEmail(email);

        Article article = articleRepository.findById(writeCommentDto.getArticleId()).get();
        if(article == null) {
            throw new EntityNotFoundException();
        }

        Comment comment = RequestComment.WriteCommentDto.toEntity(writeCommentDto, article, writer.getEmail(), writer.getName());
        commentRepository.save(comment);

        article.addComment(comment);
        articleRepository.save(article);
    }

    public ResponseComment.GetCommentDto getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).get();
        return ResponseComment.GetCommentDto.toDto(comment);
    }

    public List<ResponseComment.GetCommentDto> getCommentByArticleId(Long articleId) {
        Article article = articleRepository.findById(articleId).get();
        List<Comment> entityList = commentRepository.findAllByArticle(article);
        List<ResponseComment.GetCommentDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(comment -> dtoList.add(ResponseComment.GetCommentDto.toDto(comment)));
        return dtoList;
    }

    public void updateComment(RequestComment.UpdateCommentDto updateCommentDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Comment originalComment = commentRepository.findById(updateCommentDto.getId()).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || originalComment.getWriterEmail().equals(email)) {
            Comment updatedComment = RequestComment.UpdateCommentDto.toEntity(originalComment, updateCommentDto);
            commentRepository.save(updatedComment);
        } else throw new RuntimeException("User is not a writer of this comment.");
    }

    public List<ResponseComment.GetCommentDto> getReportedComments(Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        List<Comment> commentList = commentRepository.findAllByIsReportedTrue();
        return commentList.stream()
                .map(ResponseComment.GetCommentDto::toDto)
                .collect(Collectors.toList());
    }

    public void reportComment(Long commentId){
        Comment comment = commentRepository.findById(commentId).get();
        comment.report();
        commentRepository.save(comment);
    }

    public void unReportComment(Long commentId, Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        Comment comment = commentRepository.findById(commentId).get();
        comment.unReport();
        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Comment comment = commentRepository.findById(commentId).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || comment.getWriterEmail().equals(email)) {
            commentRepository.delete(comment);
            Article article = comment.getArticle();
            articleRepository.save(article);
        } else throw new RuntimeException("User is not a writer of this comment.");

    }
}
