package com.com2us.mobility.controller;

import com.com2us.mobility.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/dispatch/stream")
@RequiredArgsConstructor
public class DispatchSseController {

    private final SseEmitterService sseEmitterService;

    // 특정 배차 건 SSE 연결
    @GetMapping(value = "/{dispatchId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable Long dispatchId) {
        return sseEmitterService.connect(dispatchId);
    }
    // DispatchSseController.java
    @GetMapping(value = "/batch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamBatch(@RequestParam List<Long> dispatchIds) {
        return sseEmitterService.connectBatch(dispatchIds);
    }
}