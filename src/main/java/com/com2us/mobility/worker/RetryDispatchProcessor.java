package com.com2us.mobility.worker;

import com.com2us.mobility.core.log.LogMarker;
import com.com2us.mobility.domain.dispatch.DispatchRecord;
import com.com2us.mobility.domain.dispatch.DispatchStatus;
import com.com2us.mobility.repository.DispatchRecordRepository;
import com.com2us.mobility.service.DispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryDispatchProcessor {

    private final DispatchRecordRepository dispatchRecordRepository;
    private final DispatchService dispatchService;

    @Transactional
    public void retry() {
        List<DispatchStatus> statusList = new ArrayList<>(List.of(DispatchStatus.REQUESTED, DispatchStatus.WORKER_FAILED));
        List<DispatchRecord> records = dispatchRecordRepository.findTop10ByStatusInOrderByIdDesc(statusList);

        if (records.isEmpty()) return;
        LogMarker.debug("[RetryDispatchProcessor] 배차 대상 {}건", records.size());

        for (DispatchRecord record : records) {
            record.processing();
            try {
                dispatchService.tryDispatch(record);
            } catch (Exception e) {
                log.error("[RetryDispatchProcessor] 처리 실패 dispatchId={} error={}", record.getId(), e.getMessage());
                record.workerFailed();
            }
        }
    }
}