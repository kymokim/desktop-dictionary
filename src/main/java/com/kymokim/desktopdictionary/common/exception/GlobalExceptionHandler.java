package com.kymokim.desktopdictionary.common.exception;

import com.kymokim.desktopdictionary.common.dto.ResponseDto;
import com.kymokim.desktopdictionary.common.exception.error.RegisterFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RegisterFailedException.class)
    protected ResponseEntity<ResponseDto> handleRegisterFailedException(RegisterFailedException e) {
        ErrorCode code = ErrorCode.AUTHENTICATION_CONFLICT;

        ResponseDto response = ResponseDto.builder()
                .message(code.getMessage())
                .data(code.getCode())
                .build();
        return new ResponseEntity<>(response, code.getHttpStatus());
    }
}
