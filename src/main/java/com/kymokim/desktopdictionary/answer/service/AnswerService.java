package com.kymokim.desktopdictionary.answer.service;

import com.kymokim.desktopdictionary.answer.dto.RequestAnswer;
import com.kymokim.desktopdictionary.answer.dto.ResponseAnswer;
import com.kymokim.desktopdictionary.answer.entity.Answer;
import com.kymokim.desktopdictionary.answer.repository.AnswerRepository;
import com.kymokim.desktopdictionary.auth.security.role.Role;
import com.kymokim.desktopdictionary.question.entity.Question;
import com.kymokim.desktopdictionary.question.repository.QuestionRepository;
import com.kymokim.desktopdictionary.auth.entity.Auth;
import com.kymokim.desktopdictionary.auth.repository.AuthRepository;
import com.kymokim.desktopdictionary.auth.security.JwtAuthToken;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;

    public void writeAnswer(RequestAnswer.WriteAnswerDto writeAnswerDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Auth writer = authRepository.findByEmail(email);

        Question question = questionRepository.findById(writeAnswerDto.getQuestionId()).get();
        if(question == null) {
            throw new EntityNotFoundException();
        }

        Answer answer = RequestAnswer.WriteAnswerDto.toEntity(writeAnswerDto, question, writer.getEmail(), writer.getName());
        answerRepository.save(answer);

        question.addAnswer(answer);
        question.increaseAnswerCount();
        questionRepository.save(question);
    }

    public ResponseAnswer.GetAnswerDto getAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId).get();
        return ResponseAnswer.GetAnswerDto.toDto(answer);
    }

    public List<ResponseAnswer.GetAnswerDto> getAnswerByQuestionId(Long questionId) {
        Question question = questionRepository.findById(questionId).get();
        List<Answer> entityList = answerRepository.findAllByQuestion(question);
        List<ResponseAnswer.GetAnswerDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(answer -> dtoList.add(ResponseAnswer.GetAnswerDto.toDto(answer)));
        return dtoList;
    }

    public void updateAnswer(RequestAnswer.UpdateAnswerDto updateAnswerDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Answer originalAnswer = answerRepository.findById(updateAnswerDto.getId()).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || originalAnswer.getWriterEmail().equals(email)) {
            Answer updatedAnswer = RequestAnswer.UpdateAnswerDto.toEntity(originalAnswer, updateAnswerDto);
            answerRepository.save(updatedAnswer);
        } else throw new RuntimeException("User is not a writer of this answer.");
    }

    public List<ResponseAnswer.GetAnswerDto> getReportedAnswers(Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        List<Answer> answerList = answerRepository.findAllByIsReportedTrue();
        return answerList.stream()
                .map(ResponseAnswer.GetAnswerDto::toDto)
                .collect(Collectors.toList());
    }

    public void reportAnswer(Long answerId){
        Answer answer = answerRepository.findById(answerId).get();
        answer.report();
        answerRepository.save(answer);
    }

    public void unReportAnswer(Long answerId, Optional<String> token){
        String tokenEmail = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            tokenEmail = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByEmail(tokenEmail);
        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("No permission.");

        Answer answer = answerRepository.findById(answerId).get();
        answer.unReport();
        answerRepository.save(answer);
    }

    public void deleteAnswer(Long answerId, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Answer answer = answerRepository.findById(answerId).get();
        Auth user = authRepository.findByEmail(email);
        if(user.getRole() == Role.ADMIN || answer.getWriterEmail().equals(email)) {
            answerRepository.delete(answer);
            Question question = answer.getQuestion();
            question.decreaseAnswerCount();
            questionRepository.save(question);
        } else throw new RuntimeException("User is not a writer of this answer.");

    }
}
