package com.com2us.mobility.service;

import com.com2us.mobility.domain.dispatch.DispatchRecord;
import com.com2us.mobility.domain.dispatch.DispatchRetry;
import com.com2us.mobility.domain.dispatch.DispatchRetryReason;
import com.com2us.mobility.repository.DispatchRetryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchRetryService {

    private final DispatchRetryRepository dispatchRetryRepository;

    // ─────────────────────────────────────────
    // 재시도 횟수 조회
    // ─────────────────────────────────────────
    public int getAttemptCount(Long dispatchRecordId) {
        return dispatchRetryRepository.findByDispatchRecordId(dispatchRecordId)
                .map(DispatchRetry::getAttemptCount)
                .orElse(0);
    }

    // ─────────────────────────────────────────
    // 재시도 횟수 증가 (처음이면 생성)
    // ─────────────────────────────────────────
    public void increment(DispatchRecord record) {
        Optional<DispatchRetry> retryOpt = dispatchRetryRepository.findByDispatchRecordId(record.getId());

        if (retryOpt.isPresent()) {
            retryOpt.get().increment();
        } else {
            dispatchRetryRepository.save(DispatchRetry.create(record));
        }
    }

    // ─────────────────────────────────────────
    // 최종 실패 시 reason 업데이트
    // ─────────────────────────────────────────
    public void fail(DispatchRecord record, DispatchRetryReason reason) {
        dispatchRetryRepository.findByDispatchRecordId(record.getId())
                .ifPresent(retry -> {
                    retry.fail(reason);
                    log.debug("[DispatchRetryService] 최종 실패 dispatchId={} reason={}", record.getId(), reason);
                });
    }
}