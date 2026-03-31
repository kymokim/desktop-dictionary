package com.kymokim.desktopdictionary.question.controller;

import com.kymokim.desktopdictionary.article.dto.ResponseArticle;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.common.dto.ResponseDto;
import com.kymokim.desktopdictionary.question.dto.RequestQuestion;
import com.kymokim.desktopdictionary.question.dto.ResponseQuestion;
import com.kymokim.desktopdictionary.question.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @PostMapping("/write")
    public ResponseEntity<ResponseDto> writeQuestion(@RequestBody RequestQuestion.WriteQuestionDto dto, HttpServletRequest request) throws IOException {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        questionService.writeQuestion(dto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Question written successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getAllQuestion(@PageableDefault(size = 10, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResponseQuestion.GetAllQuestionDto> response = questionService.getAllQuestion(pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Question list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<ResponseDto> searchQuestion(@PathVariable("keyword") String keyword,
                                                     @PageableDefault(size = 10, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResponseQuestion.GetAllQuestionDto> response = questionService.searchQuestion(keyword, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Question search list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/{questionId}")
    public ResponseEntity<ResponseDto> getQuestion(@PathVariable("questionId") Long questionId, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        ResponseQuestion.GetQuestionDto response;
        if (token.isPresent()){
            response = questionService.getQuestion(questionId, token);
        }
        else {
            response = questionService.getQuestionNonLogIn(questionId);
        }
        ResponseDto responseDto = ResponseDto.builder()
                .message("Question retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/getByCategory/{category}")
    public ResponseEntity<ResponseDto> getByCategory(@PathVariable("category") String category,
                                                     @PageableDefault(size = 10, sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResponseQuestion.GetAllQuestionDto> response = questionService.getQuestionByCategory(category, pageable);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Question list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateQuestion(@RequestBody RequestQuestion.UpdateQuestionDto updateQuestionDto, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        questionService.updateQuestion(updateQuestionDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Question updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/getReportedQuestions")
    public ResponseEntity<ResponseDto> getReportedQuestions(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        List<ResponseQuestion.GetAllQuestionDto> response = questionService.getReportedQuestions(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Question retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/report/{questionId}")
    public ResponseEntity<ResponseDto> reportQuestion(@PathVariable("questionId") Long questionId){
        questionService.reportQuestion(questionId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Question reported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/unReport/{questionId}")
    public ResponseEntity<ResponseDto> unReportQuestion(@PathVariable("questionId") Long questionId, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        questionService.unReportQuestion(questionId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Question unreported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{questionId}")
    public ResponseEntity<ResponseDto> deleteQuestion(@PathVariable("questionId") Long questionId, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        questionService.deleteQuestion(questionId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Question deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


}
