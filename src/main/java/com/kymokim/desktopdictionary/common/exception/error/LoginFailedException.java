package com.kymokim.desktopdictionary.common.exception.error;

import com.kymokim.desktopdictionary.common.exception.ErrorCode;

public class LoginFailedException extends RuntimeException{
    public LoginFailedException(){
        super(ErrorCode.LOGIN_FAILED.getMessage());
    }
}