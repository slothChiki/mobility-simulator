package com.com2us.mobility.core.exception;

import lombok.Getter;

@Getter
public class ApiException extends BaseException {

    private final Object data;

    public ApiException(ErrorCode errorCode) {
        super(errorCode);
        this.data = null;
    }

    public ApiException(ErrorCode errorCode, Object data) {
        super(errorCode);
        this.data = data;
    }

    public ApiException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
        this.data = null;
    }

    public ApiException(ErrorCode errorCode, Object data, Throwable cause) {
        super(errorCode, cause);
        this.data = data;
    }

    public boolean hasData() {
        return this.data != null;
    }
}