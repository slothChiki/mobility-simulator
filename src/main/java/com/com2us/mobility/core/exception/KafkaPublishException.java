package com.com2us.mobility.core.exception;

import lombok.Getter;

@Getter
public class KafkaPublishException extends RuntimeException {

    private final ErrorCode errorCode;

    public KafkaPublishException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public KafkaPublishException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}