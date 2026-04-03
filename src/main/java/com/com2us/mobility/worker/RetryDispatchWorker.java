package com.com2us.mobility.worker;

import com.com2us.mobility.core.lock.SpinLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryDispatchWorker {

    private static final String LOCK_KEY = "lock:retry-dispatch-worker";
    private static final Duration LOCK_TTL = Duration.ofSeconds(3);

    private boolean working = false;

    private final RetryDispatchProcessor retryDispatchProcessor;
    private final SpinLockService spinLockService;

    @Scheduled(fixedDelay = 1000)
    public void process() {
        if (working) return;
        working = true;

        boolean locked = spinLockService.lock(LOCK_KEY, LOCK_TTL, "retry-worker");
        if (!locked) {
            working = false;
            return;
        }

        try {
            retryDispatchProcessor.retry();
        } catch (Exception e) {
            log.error("[RetryDispatchWorker] 에러 발생 error={}", e.getMessage());
        } finally {
            spinLockService.unlock(LOCK_KEY);
            working = false;
        }
    }
}