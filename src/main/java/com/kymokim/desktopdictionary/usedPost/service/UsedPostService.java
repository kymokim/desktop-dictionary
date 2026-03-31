package com.kymokim.desktopdictionary.usedPost.service;

import com.kymokim.desktopdictionary.auth.entity.Auth;
import com.kymokim.desktopdictionary.auth.repository.AuthRepository;
import com.kymokim.desktopdictionary.auth.security.JwtAuthToken;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.auth.security.role.Role;
import com.kymokim.desktopdictionary.common.service.ImageService;
import com.kymokim.desktopdictionary.usedPost.dto.RequestUsedPost;
import com.kymokim.desktopdictionary.usedPost.dto.ResponseUsedPost;
import com.kymokim.desktopdictionary.usedPost.entity.UsedPost;
import com.kymokim.desktopdictionary.usedPost.entity.UsedPostImage;
import com.kymokim.desktopdictionary.usedPost.repository.UsedPostImageRepository;
import com.kymokim.desktopdictionary.usedPost.repository.UsedPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsedPostService {

    private final UsedPostRepository usedPostRepository;
    private final UsedPostImageRepository usedPostImageRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;
    private final ImageService imageService;

    public void writeUsedPost(MultipartFile[] files, RequestUsedPost.WriteUsedPostDto writeUsedPostDto, Optional<String> token) throws IOException {

        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        String writerName = authRepository.findByEmail(email).getName();
        UsedPost usedPost = RequestUsedPost.WriteUsedPostDto.toEntity(writeUsedPostDto, email, writerName);
        usedPostRepository.save(usedPost);

        if (files != null) {
            List<String> imageUrls = imageService.uploadImage(files, String.valueOf(usedPost.getId()));
            for (String url : imageUrls){
                UsedPostImage usedPostImage = UsedPostImage.builder().url(url).usedPost(usedPost).build();
                usedPostImageRepository.save(usedPostImage);
                usedPost.addImgUrlList(usedPostImage);
            }
            usedPostRepository.save(usedPost);
        }
    }

    public Page<ResponseUsedPost.GetAllUsedPostDto> getAllUsedPost(Pageable pageable) {
        Page<UsedPost> entityPage = usedPostRepository.findAll(pageable);
        return entityPage.map(ResponseUsedPost.GetAllUsedPostDto::toDto);
    }

    public ResponseUsedPost.GetUsedPostDto getUsedPostNonLogIn(Long id) {
        UsedPost usedPost = usedPostRepository.findById(id).get();
        return ResponseUsedPost.GetUsedPostDto.toDto(usedPost, false, null);
    }

    public ResponseUsedPost.GetUsedPostDto getUsedPost(Long id, Optional<String> token) {
        String email = null;
        Boolean isWriter = false;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        UsedPost usedPost = usedPostRepository.findById(id).get();
        if (usedPost.getWriterEmail().equals(email)) {
            isWriter = true;
        }

        return ResponseUsedPost.GetUsedPostDto.toDto(usedPost, isWriter, email);
    }

    public Page<ResponseUsedPost.GetAllUsedPostDto> searchUsedPost(String keyword, Pageable pageable) {
        Page<UsedPost> entityPage = usedPostRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        return entityPage.map(ResponseUsedPost.GetAllUsedPostDto::toDto);
    }

    public Page<ResponseUsedPost.GetAllUsedPostDto> getUsedPostByCategory(String category, Pageable pageable) {
        Page<UsedPost> entityPage = usedPostRepository.findAllByCategory(category, pageable);
        return entityPage.map(ResponseUsedPost.GetAllUsedPostDto::toDto);
    }

    public void updateUsedPost(RequestUsedPost.UpdateUsedPostDto updateUsedPostDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        UsedPost originalUsedPost = usedPostRepository.findById(updateUsedPostDto.getId()).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || originalUsedPost.getWriterEmail().equals(email)) {
            UsedPost updatedUsedPost = RequestUsedPost.UpdateUsedPostDto.toEntity(originalUsedPost, updateUsedPostDto);
            usedPostRepository.save(updatedUsedPost);
        } else throw new RuntimeException("User is not a writer of this usedPost.");
    }

    public List<ResponseUsedPost.GetAllUsedPostDto> getReportedUsedPosts(Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        List<UsedPost> usedPostList = usedPostRepository.findAllByIsReportedTrue();
        return usedPostList.stream()
                .map(ResponseUsedPost.GetAllUsedPostDto::toDto)
                .collect(Collectors.toList());
    }

    public void reportUsedPost(Long usedPostId){
        UsedPost usedPost = usedPostRepository.findById(usedPostId).get();
        usedPost.report();
        usedPostRepository.save(usedPost);
    }

    public void unReportUsedPost(Long usedPostId, Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        UsedPost usedPost = usedPostRepository.findById(usedPostId).get();
        usedPost.unReport();
        usedPostRepository.save(usedPost);
    }

    public void soldUsedPost(Long usedPostId, Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }

        UsedPost originalUsedPost = usedPostRepository.findById(usedPostId).get();
        Auth user = authRepository.findByEmail(tokenEmail);
        if (user.getRole() == Role.ADMIN || originalUsedPost.getWriterEmail().equals(tokenEmail)){
            UsedPost usedPost = usedPostRepository.findById(usedPostId).get();
            usedPost.sold();
            usedPostRepository.save(usedPost);
        } else throw new RuntimeException("No permission.");
    }

    public void unSoldUsedPost(Long usedPostId, Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        UsedPost originalUsedPost = usedPostRepository.findById(usedPostId).get();
        Auth user = authRepository.findByEmail(tokenEmail);
        if (user.getRole() == Role.ADMIN || originalUsedPost.getWriterEmail().equals(tokenEmail)){
            UsedPost usedPost = usedPostRepository.findById(usedPostId).get();
            usedPost.unSold();
            usedPostRepository.save(usedPost);
        } else throw new RuntimeException("No permission.");
    }
    public void deleteUsedPost(Long usedPostId, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        UsedPost usedPost = usedPostRepository.findById(usedPostId).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || usedPost.getWriterEmail().equals(email)) {
            usedPostRepository.delete(usedPost);
        } else throw new RuntimeException("User is not a writer of this usedPost.");
    }
}
