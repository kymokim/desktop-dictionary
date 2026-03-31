package com.kymokim.desktopdictionary.usedComment.controller;

import com.kymokim.desktopdictionary.auth.security.JwtAuthTokenProvider;
import com.kymokim.desktopdictionary.common.dto.ResponseDto;
import com.kymokim.desktopdictionary.usedComment.dto.RequestUsedComment;
import com.kymokim.desktopdictionary.usedComment.dto.ResponseUsedComment;
import com.kymokim.desktopdictionary.usedComment.service.UsedCommentService;
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
@RequestMapping("/api/usedComment")
public class UsedCommentController {

    private final UsedCommentService usedCommentService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @PostMapping("/write")
    public ResponseEntity<ResponseDto> writeUsedComment(@RequestBody RequestUsedComment.WriteUsedCommentDto writeUsedCommentDto, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        usedCommentService.writeUsedComment(writeUsedCommentDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedComment written successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/get/{usedCommentId}")
    public ResponseEntity<ResponseDto> getUsedComment(@PathVariable("usedCommentId") Long usedCommentId) {
        ResponseUsedComment.GetUsedCommentDto response = usedCommentService.getUsedComment(usedCommentId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedComment list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Deprecated
    @GetMapping("/getByUsedPost/{usedPostId}")
    public ResponseEntity<ResponseDto> getUsedCommentByUsedPostId(@PathVariable("usedPostId") Long usedPostId) {
        List<ResponseUsedComment.GetUsedCommentDto> response = usedCommentService.getUsedCommentByUsedPostId(usedPostId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedComment list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateUsedComment(@RequestBody RequestUsedComment.UpdateUsedCommentDto updateUsedCommentDto, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        usedCommentService.updateUsedComment(updateUsedCommentDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedComment updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/getReportedUsedComments")
    public ResponseEntity<ResponseDto> getReportedUsedComments(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        List<ResponseUsedComment.GetUsedCommentDto> response = usedCommentService.getReportedUsedComments(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedComment retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/report/{usedCommentId}")
    public ResponseEntity<ResponseDto> reportUsedComment(@PathVariable("usedCommentId") Long usedCommentId){
        usedCommentService.reportUsedComment(usedCommentId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedComment reported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/unReport/{usedCommentId}")
    public ResponseEntity<ResponseDto> unReportUsedComment(@PathVariable("usedCommentId") Long usedCommentId, HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        usedCommentService.unReportUsedComment(usedCommentId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedComment unreported successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{usedCommentId}")
    public ResponseEntity<ResponseDto> deleteUsedComment(@PathVariable("usedCommentId") Long usedCommentId, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        usedCommentService.deleteUsedComment(usedCommentId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("UsedComment deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
