package com.kymokim.desktopdictionary.common.exception.error;

import com.kymokim.desktopdictionary.common.exception.ErrorCode;

public class NotFoundUserException extends RuntimeException{
    public NotFoundUserException(){
        super(ErrorCode.NOT_FOUND_USER.getMessage());
    }
}