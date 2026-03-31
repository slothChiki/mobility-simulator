package com.com2us.web3.mass_data_processor.core.log;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.ServletContextRequestLoggingFilter;

@Slf4j
@Component
public class LoggingFilter extends ServletContextRequestLoggingFilter {

    private static final String START_TIME = "startTime";

    public LoggingFilter() {
        setIncludePayload(true);       // body 포함
        setMaxPayloadLength(10000);    // body 최대 길이
        setIncludeQueryString(true);   // query string 포함
        setIncludeHeaders(false);      // 헤더 포함 여부
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {

        Long startMs = (Long) request.getAttribute(START_TIME);
        long endMs = System.currentTimeMillis();
        long duration = (startMs != null) ? (endMs - startMs) : 0;

        String logMessage = String.format(
                "\u001B[33m[REQ:time=%dms] message=%s\u001B[33m",
                duration,
                message
        );

        log.info(logMessage);
    }
}