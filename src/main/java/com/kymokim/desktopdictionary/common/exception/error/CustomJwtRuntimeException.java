package com.kymokim.desktopdictionary.common.exception.error;

import com.kymokim.desktopdictionary.common.exception.ErrorCode;

public class CustomJwtRuntimeException extends RuntimeException{
    public CustomJwtRuntimeException(){
        super(ErrorCode.AUTHENTICATION_FAILED.getMessage());
    }
}
