package com.com2us.mobility.core.advice;

import com.com2us.mobility.core.exception.KafkaPublishException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(KafkaPublishException.class)
    public ResponseEntity<?> handleKafkaPublishException(KafkaPublishException e) {
        log.error("[KafkaPublishException] code={} message={} cause={}",
                e.getErrorCode().getCode(), e.getMessage(),
                e.getCause() != null ? e.getCause().getMessage() : "none");
        return ResponseEntity
                .internalServerError()
                .body(Map.of(
                        "code", e.getErrorCode().getCode(),
                        "message", e.getErrorCode().getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("[Exception] message={}", e.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(Map.of(
                        "code", 5000,
                        "message", "서버 내부 오류"
                ));
    }
}