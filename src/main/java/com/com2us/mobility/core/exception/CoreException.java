package com.com2us.mobility.core.exception;

import lombok.Getter;

@Getter
public class CoreException extends BaseException {

    public CoreException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CoreException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}