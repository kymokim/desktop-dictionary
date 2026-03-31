package com.kymokim.desktopdictionary.question.service;

import com.kymokim.desktopdictionary.article.dto.ResponseArticle;
import com.kymokim.desktopdictionary.article.entity.Article;
import com.kymokim.desktopdictionary.auth.entity.Auth;
import com.kymokim.desktopdictionary.auth.repository.AuthRepository;
import com.kymokim.desktopdictionary.auth.security.JwtAuthToken;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.auth.security.role.Role;
import com.kymokim.desktopdictionary.common.service.ImageService;
import com.kymokim.desktopdictionary.question.dto.RequestQuestion;
import com.kymokim.desktopdictionary.question.dto.ResponseQuestion;
import com.kymokim.desktopdictionary.question.entity.Question;
import com.kymokim.desktopdictionary.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;
    private final ImageService imageService;

    public void writeQuestion(RequestQuestion.WriteQuestionDto writeQuestionDto, Optional<String> token) throws IOException {

        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        String writerName = authRepository.findByEmail(email).getName();
        Question question = RequestQuestion.WriteQuestionDto.toEntity(writeQuestionDto, email, writerName);
        questionRepository.save(question);

    }

    public Page<ResponseQuestion.GetAllQuestionDto> getAllQuestion(Pageable pageable) {
        Page<Question> entityPage = questionRepository.findAll(pageable);
        return entityPage.map(ResponseQuestion.GetAllQuestionDto::toDto);
    }

    public ResponseQuestion.GetQuestionDto getQuestionNonLogIn(Long id) {
        Question question = questionRepository.findById(id).get();
        return ResponseQuestion.GetQuestionDto.toDto(question, false, null);
    }

    public Page<ResponseQuestion.GetAllQuestionDto> searchQuestion(String keyword, Pageable pageable) {
        Page<Question> entityPage = questionRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        return entityPage.map(ResponseQuestion.GetAllQuestionDto::toDto);
    }

    public ResponseQuestion.GetQuestionDto getQuestion(Long id, Optional<String> token) {
        String email = null;
        Boolean isWriter = false;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Question question = questionRepository.findById(id).get();
        if (question.getWriterEmail().equals(email)) {
            isWriter = true;
        }
        question.increaseViewCount();
        questionRepository.save(question);
        return ResponseQuestion.GetQuestionDto.toDto(question, isWriter, email);
    }

    public Page<ResponseQuestion.GetAllQuestionDto> getQuestionByCategory(String category, Pageable pageable) {
        Page<Question> entityPage = questionRepository.findAllByCategory(category, pageable);
        return entityPage.map(ResponseQuestion.GetAllQuestionDto::toDto);
    }

    public void updateQuestion(RequestQuestion.UpdateQuestionDto updateQuestionDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Question originalQuestion = questionRepository.findById(updateQuestionDto.getId()).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || originalQuestion.getWriterEmail().equals(email)) {
            Question updatedQuestion = RequestQuestion.UpdateQuestionDto.toEntity(originalQuestion, updateQuestionDto);
            questionRepository.save(updatedQuestion);
        } else throw new RuntimeException("User is not a writer of this question.");
    }

    public List<ResponseQuestion.GetAllQuestionDto> getReportedQuestions(Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        List<Question> questionList = questionRepository.findAllByIsReportedTrue();
        return questionList.stream()
                .map(ResponseQuestion.GetAllQuestionDto::toDto)
                .collect(Collectors.toList());
    }

    public void reportQuestion(Long questionId){
        Question question = questionRepository.findById(questionId).get();
        question.report();
        questionRepository.save(question);
    }

    public void unReportQuestion(Long questionId, Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        Question question = questionRepository.findById(questionId).get();
        question.unReport();
        questionRepository.save(question);
    }

    public void deleteQuestion(Long questionId, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Question question = questionRepository.findById(questionId).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || question.getWriterEmail().equals(email)) {
            questionRepository.delete(question);
        } else throw new RuntimeException("User is not a writer of this question.");
    }
}
