package com.kymokim.desktopdictionary.common.exception.error;

import com.kymokim.desktopdictionary.common.exception.ErrorCode;

public class NotFoundTaskException extends RuntimeException{

    public NotFoundTaskException() { super(ErrorCode.NOT_FOUND_TASK.getMessage());}
}