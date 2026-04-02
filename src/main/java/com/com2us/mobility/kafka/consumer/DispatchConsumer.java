package com.com2us.mobility.kafka.consumer;

import com.com2us.mobility.config.KafkaConfig;
import com.com2us.mobility.domain.dispatch.DispatchRecord;
import com.com2us.mobility.domain.vehicle.VehicleState;
import com.com2us.mobility.kafka.event.DispatchEvent;
import com.com2us.mobility.repository.DispatchRecordRepository;
import com.com2us.mobility.repository.VehicleStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchConsumer {

    private final DispatchRecordRepository dispatchRecordRepository;
    private final VehicleStateRepository vehicleStateRepository;

    @KafkaListener(
            topics = KafkaConfig.TOPIC_DISPATCH_RESULT,
            groupId = "mobility-group"
    )
    @Transactional
    public void consume(DispatchEvent event) {
        DispatchRecord record = dispatchRecordRepository.findById(event.getDispatchId())
                .orElseThrow(() -> new IllegalArgumentException("배차 기록 없음 dispatchId=" + event.getDispatchId()));

        switch (event.getStatus()) {
            case DISPATCHED -> {
                record.dispatch(record.getVehicle());
                updateVehicleState(record.getVehicle().getId(), false);
            }
            case BOARDED    -> record.board();
            case COMPLETED  -> {
                record.complete();
                updateVehicleState(record.getVehicle().getId(), true);
            }
            case REFUSED    -> record.refuse();
            case CANCELLED  -> record.cancel();
            default -> log.warn("[DispatchConsumer] 처리할 수 없는 status={}", event.getStatus());
        }
    }

    // ─────────────────────────────────────────
    // 차량 상태 변경
    // available = true  → AVAILABLE (운행 완료)
    // available = false → DISPATCHED (배차됨)
    // ─────────────────────────────────────────
    private void updateVehicleState(Long vehicleId, boolean available) {
        vehicleStateRepository.findByVehicleId(vehicleId)
                .ifPresent(state -> {
                    if (available) {
                        state.complete();
                    } else {
                        state.dispatch();
                    }
                });
    }
}