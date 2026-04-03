package com.com2us.mobility.core.exception;
import lombok.Getter;

@Getter
public class CommonException extends BaseException {

    public CommonException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommonException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}