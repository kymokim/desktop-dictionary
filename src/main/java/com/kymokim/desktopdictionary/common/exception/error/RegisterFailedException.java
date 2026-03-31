package com.kymokim.desktopdictionary.common.exception.error;

import com.kymokim.desktopdictionary.common.exception.ErrorCode;

public class RegisterFailedException extends RuntimeException{
    public RegisterFailedException(){
        super(ErrorCode.AUTHENTICATION_CONFLICT.getMessage());
    }
}