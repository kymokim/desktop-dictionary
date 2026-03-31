package com.kymokim.desktopdictionary.usedComment.service;


import com.kymokim.desktopdictionary.usedPost.entity.UsedPost;
import com.kymokim.desktopdictionary.usedPost.repository.UsedPostRepository;
import com.kymokim.desktopdictionary.auth.entity.Auth;
import com.kymokim.desktopdictionary.auth.repository.AuthRepository;
import com.kymokim.desktopdictionary.auth.security.JwtAuthToken;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.auth.security.role.Role;
import com.kymokim.desktopdictionary.usedComment.dto.RequestUsedComment;
import com.kymokim.desktopdictionary.usedComment.dto.ResponseUsedComment;
import com.kymokim.desktopdictionary.usedComment.entity.UsedComment;
import com.kymokim.desktopdictionary.usedComment.repository.UsedCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsedCommentService {
    private final UsedPostRepository usedPostRepository;
    private final UsedCommentRepository usedCommentRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;

    public void writeUsedComment(RequestUsedComment.WriteUsedCommentDto writeUsedCommentDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth writer = authRepository.findByEmail(email);

        UsedPost usedPost = usedPostRepository.findById(writeUsedCommentDto.getUsedPostId()).get();
        if(usedPost == null) {
            throw new EntityNotFoundException();
        }

        UsedComment usedComment = RequestUsedComment.WriteUsedCommentDto.toEntity(writeUsedCommentDto, usedPost, writer.getEmail(), writer.getName());
        usedCommentRepository.save(usedComment);

        usedPost.addUsedComment(usedComment);
        usedPostRepository.save(usedPost);
    }

    public ResponseUsedComment.GetUsedCommentDto getUsedComment(Long usedCommentId) {
        UsedComment usedComment = usedCommentRepository.findById(usedCommentId).get();
        return ResponseUsedComment.GetUsedCommentDto.toDto(usedComment);
    }

    public List<ResponseUsedComment.GetUsedCommentDto> getUsedCommentByUsedPostId(Long usedPostId) {
        UsedPost usedPost = usedPostRepository.findById(usedPostId).get();
        List<UsedComment> entityList = usedCommentRepository.findAllByUsedPost(usedPost);
        List<ResponseUsedComment.GetUsedCommentDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(usedComment -> dtoList.add(ResponseUsedComment.GetUsedCommentDto.toDto(usedComment)));
        return dtoList;
    }

    public void updateUsedComment(RequestUsedComment.UpdateUsedCommentDto updateUsedCommentDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        UsedComment originalUsedComment = usedCommentRepository.findById(updateUsedCommentDto.getId()).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || originalUsedComment.getWriterEmail().equals(email)) {
            UsedComment updatedUsedComment = RequestUsedComment.UpdateUsedCommentDto.toEntity(originalUsedComment, updateUsedCommentDto);
            usedCommentRepository.save(updatedUsedComment);
        } else throw new RuntimeException("User is not a writer of this usedComment.");
    }

    public List<ResponseUsedComment.GetUsedCommentDto> getReportedUsedComments(Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        List<UsedComment> usedCommentList = usedCommentRepository.findAllByIsReportedTrue();
        return usedCommentList.stream()
                .map(ResponseUsedComment.GetUsedCommentDto::toDto)
                .collect(Collectors.toList());
    }

    public void reportUsedComment(Long usedCommentId){
        UsedComment usedComment = usedCommentRepository.findById(usedCommentId).get();
        usedComment.report();
        usedCommentRepository.save(usedComment);
    }

    public void unReportUsedComment(Long usedCommentId, Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        UsedComment usedComment = usedCommentRepository.findById(usedCommentId).get();
        usedComment.unReport();
        usedCommentRepository.save(usedComment);
    }

    public void deleteUsedComment(Long usedCommentId, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        UsedComment usedComment = usedCommentRepository.findById(usedCommentId).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || usedComment.getWriterEmail().equals(email)) {
            usedCommentRepository.delete(usedComment);
            UsedPost usedPost = usedComment.getUsedPost();
            usedPostRepository.save(usedPost);
        } else throw new RuntimeException("User is not a writer of this usedComment.");

    }
}
