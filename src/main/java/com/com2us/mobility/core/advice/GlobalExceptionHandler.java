package com.com2us.mobility.core.advice;

import com.com2us.mobility.core.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ApiResponse<?>> handleCoreException(CoreException e) {
        log.error("[CoreException] code={} message={} cause={}",
                e.getErrorCode().getCode(), e.getMessage(),
                e.getCause() != null ? e.getCause().getMessage() : "none");
        return ResponseEntity.internalServerError()
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ApiResponse<?>> handleCommonException(CommonException e) {
        log.warn("[CommonException] code={} message={}", e.getErrorCode().getCode(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(ApiException e) {
        log.warn("[ApiException] code={} message={}", e.getErrorCode().getCode(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(e.hasData()
                        ? ApiResponse.fail(e.getErrorCode(), e.getData())
                        : ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException e) {
        log.error("[BaseException] code={} message={}", e.getErrorCode().getCode(), e.getMessage());
        return ResponseEntity.internalServerError()
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("[Exception] message={}", e.getMessage());
        return ResponseEntity.internalServerError()
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}