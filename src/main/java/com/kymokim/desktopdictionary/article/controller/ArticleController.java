package com.kymokim.desktopdictionary.article.controller;

import com.kymokim.desktopdictionary.article.dto.RequestArticle;
import com.kymokim.desktopdictionary.article.dto.ResponseArticle;
import com.kymokim.desktopdictionary.article.service.ArticleService;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

//    @PostMapping("/write")
//    public ResponseEntity<ResponseDto> writeArticle(@RequestBody RequestArticle.WriteArticleDto requestDto, HttpServletRequest request) {
//        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
//        articleService.writeArticle(requestDto, token);
//        ResponseDto responseDto = ResponseDto.builder()
//                .message("Article written successfully.")
//                .build();
//        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
//    }

    @PostMapping("/write")
    public ResponseEntity<ResponseDto> writeArticle(@RequestPart(value = "files", required = false) MultipartFile[] files,
                                                    @RequestPart(value = "writeArticleDto") RequestArticle.WriteArticleDto dto,
                                                    HttpServletRequest request) throws IOException {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        articleService.writeArticle(files, dto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Article written successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<ResponseDto> searchArticle(@PathVariable("keyword") String keyword,
            @PageableDefault(size = 8, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResponseArticle.GetAllArticleDto> response = articleService.searchArticle(keyword, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Article search list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getAllArticle(@PageableDefault(size = 8, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResponseArticle.GetAllArticleDto> response = articleService.getAllArticle(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Article list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/{articleId}")
    public ResponseEntity<ResponseDto> getArticle(@PathVariable("articleId") Long articleId, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        ResponseArticle.GetArticleDto response;
        if (token.isPresent()){
            response = articleService.getArticle(articleId, token);
        }
        else {
            response = articleService.getArticleNonLogIn(articleId);
        }
        ResponseDto responseDto = ResponseDto.builder()
                .message("Article retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/getByCategory/{category}")
    public ResponseEntity<ResponseDto> getByCategory(@PathVariable("category") String category,
                                                     @PageableDefault(size = 8, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResponseArticle.GetAllArticleDto> response = articleService.getArticleByCategory(category, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Article list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateArticle(@RequestBody RequestArticle.UpdateArticleDto updateArticleDto, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        articleService.updateArticle(updateArticleDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Article updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/getReportedArticles")
    public ResponseEntity<ResponseDto> getReportedArticles(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        List<ResponseArticle.GetAllArticleDto> response = articleService.getReportedArticles(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Article retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/report/{articleId}")
    public ResponseEntity<ResponseDto> reportArticle(@PathVariable("articleId") Long articleId){
        articleService.reportArticle(articleId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Article reported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/unReport/{articleId}")
    public ResponseEntity<ResponseDto> unReportArticle(@PathVariable("articleId") Long articleId, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        articleService.unReportArticle(articleId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Article unreported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{articleId}")
    public ResponseEntity<ResponseDto> deleteArticle(@PathVariable("articleId") Long articleId, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        articleService.deleteArticle(articleId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Article deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
