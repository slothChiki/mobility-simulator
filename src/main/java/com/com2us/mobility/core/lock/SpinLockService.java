package com.com2us.mobility.core.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpinLockService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 락 획득 시도
     * SETNX — 키가 없을 때만 저장 (Set if Not eXists)
     * TTL   — 락이 만료되지 않고 남아있는 경우 방지
     *
     * @return true  → 락 획득 성공
     *         false → 이미 다른 인스턴스가 락 보유 중
     */
    public boolean lock(String key, Duration ttl, String value) {
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, value, ttl);  // SETNX + EXPIRE 원자적 실행

        if (Boolean.TRUE.equals(acquired)) {
            log.debug("[SpinLock] 락 획득 key={} value={}", key, value);
            return true;
        }

        log.debug("[SpinLock] 락 획득 실패 key={}", key);
        return false;
    }

    /**
     * 락 해제
     * 반드시 finally에서 호출
     */
    public void unlock(String key) {
        redisTemplate.delete(key);
        log.debug("[SpinLock] 락 해제 key={}", key);
    }
}