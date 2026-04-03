package com.com2us.mobility.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
public class SseEmitterService {

    // dispatchId → SseEmitter
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // ─────────────────────────────────────────
    // 단건 연결 (기존 - 그대로 유지)
    // ─────────────────────────────────────────
    public SseEmitter connect(Long dispatchId) {
        SseEmitter emitter = new SseEmitter(300_000L);

        emitter.onCompletion(() -> {
            emitters.remove(dispatchId);
            log.debug("[SSE] 연결 종료 dispatchId={}", dispatchId);
        });
        emitter.onTimeout(() -> {
            emitters.remove(dispatchId);
            log.debug("[SSE] 연결 타임아웃 dispatchId={}", dispatchId);
        });
        emitter.onError(e -> {
            emitters.remove(dispatchId);
            log.debug("[SSE] 연결 에러 dispatchId={} error={}", dispatchId, e.getMessage());
        });

        emitters.put(dispatchId, emitter);
        send(dispatchId, "connected", Map.of("dispatchId", dispatchId));
        return emitter;
    }

    // ─────────────────────────────────────────
    // 배치 연결 (신규)
    // ─────────────────────────────────────────
    public SseEmitter connectBatch(List<Long> dispatchIds) {
        SseEmitter emitter = new SseEmitter(300_000L);

        dispatchIds.forEach(id -> emitters.put(id, emitter));

        emitter.onCompletion(() -> {
            dispatchIds.forEach(emitters::remove);
            log.debug("[SSE] 배치 연결 종료 dispatchIds={}", dispatchIds);
        });
        emitter.onTimeout(() -> {
            dispatchIds.forEach(emitters::remove);
            log.debug("[SSE] 배치 연결 타임아웃 dispatchIds={}", dispatchIds);
        });
        emitter.onError(e -> {
            dispatchIds.forEach(emitters::remove);
            log.debug("[SSE] 배치 연결 에러 dispatchIds={} error={}", dispatchIds, e.getMessage());
        });

        // 연결 확인용 초기 이벤트
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data(Map.of("dispatchIds", dispatchIds)));
        } catch (IOException e) {
            log.debug("[SSE] 배치 초기 이벤트 전송 실패");
        }

        return emitter;
    }

    // ─────────────────────────────────────────
    // 이벤트 푸시 (공통)
    // ─────────────────────────────────────────
    public void send(Long dispatchId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(dispatchId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            emitters.remove(dispatchId);
            log.debug("[SSE] 전송 실패 dispatchId={}", dispatchId);
        }
    }

    // ─────────────────────────────────────────
    // 연결 종료
    // ─────────────────────────────────────────
    public void complete(Long dispatchId) {
        SseEmitter emitter = emitters.get(dispatchId);
        if (emitter == null) return;

        emitters.remove(dispatchId);

        // 같은 emitter를 공유하는 id가 없을 때만 complete
        boolean sharedEmitterExists = emitters.values().stream()
                .anyMatch(e -> e == emitter);
        if (!sharedEmitterExists) {
            emitter.complete();
        }
    }
}