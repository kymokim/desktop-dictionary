package com.kymokim.desktopdictionary.answer.controller;

import com.kymokim.desktopdictionary.answer.dto.RequestAnswer;
import com.kymokim.desktopdictionary.answer.dto.ResponseAnswer;
import com.kymokim.desktopdictionary.answer.service.AnswerService;
import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.common.dto.ResponseDto;
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
@RequestMapping("/api/answer")
public class AnswerController {

    private final AnswerService answerService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @PostMapping("/write")
    public ResponseEntity<ResponseDto> writeAnswer(@RequestBody RequestAnswer.WriteAnswerDto writeAnswerDto, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        answerService.writeAnswer(writeAnswerDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Answer written successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/get/{answerId}")
    public ResponseEntity<ResponseDto> getAnswer(@PathVariable("answerId") Long answerId) {
        ResponseAnswer.GetAnswerDto response = answerService.getAnswer(answerId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Answer list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Deprecated
    @GetMapping("/getByQuestion/{questionId}")
    public ResponseEntity<ResponseDto> getAnswerByQuestionId(@PathVariable("questionId") Long questionId) {
        List<ResponseAnswer.GetAnswerDto> response = answerService.getAnswerByQuestionId(questionId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Answer list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAnswer(@RequestBody RequestAnswer.UpdateAnswerDto updateAnswerDto, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        answerService.updateAnswer(updateAnswerDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Answer updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/getReportedAnswers")
    public ResponseEntity<ResponseDto> getReportedAnswers(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        List<ResponseAnswer.GetAnswerDto> response = answerService.getReportedAnswers(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Answer retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/report/{answerId}")
    public ResponseEntity<ResponseDto> reportAnswer(@PathVariable("answerId") Long answerId){
        answerService.reportAnswer(answerId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Answer reported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/unReport/{answerId}")
    public ResponseEntity<ResponseDto> unReportAnswer(@PathVariable("answerId") Long answerId, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        answerService.unReportAnswer(answerId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Answer unreported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{answerId}")
    public ResponseEntity<ResponseDto> deleteAnswer(@PathVariable("answerId") Long answerId, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        answerService.deleteAnswer(answerId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Answer deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
