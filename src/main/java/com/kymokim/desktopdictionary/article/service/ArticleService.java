package com.kymokim.desktopdictionary.article.service;

import com.kymokim.desktopdictionary.article.dto.RequestArticle;
import com.kymokim.desktopdictionary.article.dto.ResponseArticle;
import com.kymokim.desktopdictionary.article.entity.Article;
import com.kymokim.desktopdictionary.article.entity.ArticleImage;
import com.kymokim.desktopdictionary.article.repository.ArticleImageRepository;
import com.kymokim.desktopdictionary.article.repository.ArticleRepository;
import com.kymokim.desktopdictionary.auth.controller.AuthController;
import com.kymokim.desktopdictionary.auth.entity.Auth;
import com.kymokim.desktopdictionary.auth.repository.AuthRepository;
import com.kymokim.desktopdictionary.auth.security.JwtAuthToken;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.auth.security.role.Role;
import com.kymokim.desktopdictionary.common.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleImageRepository articleImageRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;
    private final ImageService imageService;

    public void writeArticle(MultipartFile[] files, RequestArticle.WriteArticleDto writeArticleDto, Optional<String> token) throws IOException {

        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        String writerName = authRepository.findByEmail(email).getName();
        Article article = RequestArticle.WriteArticleDto.toEntity(writeArticleDto, email, writerName);
        articleRepository.save(article);

        if (files != null) {
            List<String> imageUrls = imageService.uploadImage(files, String.valueOf(article.getId()));
            for (String url : imageUrls){
                ArticleImage articleImage = ArticleImage.builder().url(url).article(article).build();
                articleImageRepository.save(articleImage);
                article.addImgUrlList(articleImage);
            }
            articleRepository.save(article);
        }
    }

    public Page<ResponseArticle.GetAllArticleDto> getAllArticle(Pageable pageable) {
        Page<Article> entityPage = articleRepository.findAll(pageable);
        return entityPage.map(ResponseArticle.GetAllArticleDto::toDto);
    }

    public ResponseArticle.GetArticleDto getArticleNonLogIn(Long id) {
        Article article = articleRepository.findById(id).get();
        return ResponseArticle.GetArticleDto.toDto(article, false, null);
    }

    public ResponseArticle.GetArticleDto getArticle(Long id, Optional<String> token) {
        String email = null;
        Boolean isWriter = false;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Article article = articleRepository.findById(id).get();
        if (article.getWriterEmail().equals(email)) {
            isWriter = true;
        }
        return ResponseArticle.GetArticleDto.toDto(article, isWriter, email);
    }

    public Page<ResponseArticle.GetAllArticleDto> searchArticle(String keyword, Pageable pageable) {
        Page<Article> entityPage = articleRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        return entityPage.map(ResponseArticle.GetAllArticleDto::toDto);
    }

    public Page<ResponseArticle.GetAllArticleDto> getArticleByCategory(String category, Pageable pageable) {
        Page<Article> entityPage = articleRepository.findAllByCategory(category, pageable);
        return entityPage.map(ResponseArticle.GetAllArticleDto::toDto);
    }

    public void updateArticle(RequestArticle.UpdateArticleDto updateArticleDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Article originalArticle = articleRepository.findById(updateArticleDto.getId()).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || originalArticle.getWriterEmail().equals(email)) {
            Article updatedArticle = RequestArticle.UpdateArticleDto.toEntity(originalArticle, updateArticleDto);
            articleRepository.save(updatedArticle);
        } else throw new RuntimeException("User is not a writer of this article.");
    }

    public List<ResponseArticle.GetAllArticleDto> getReportedArticles(Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        List<Article> articleList = articleRepository.findAllByIsReportedTrue();
        return articleList.stream()
                .map(ResponseArticle.GetAllArticleDto::toDto)
                .collect(Collectors.toList());
    }

    public void reportArticle(Long articleId){
        Article article = articleRepository.findById(articleId).get();
        article.report();
        articleRepository.save(article);
    }

    public void unReportArticle(Long articleId, Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        Article article = articleRepository.findById(articleId).get();
        article.unReport();
        articleRepository.save(article);
    }

    public void deleteArticle(Long articleId, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Article article = articleRepository.findById(articleId).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || article.getWriterEmail().equals(email)) {
            articleRepository.delete(article);
        } else throw new RuntimeException("User is not a writer of this article.");
    }

}
