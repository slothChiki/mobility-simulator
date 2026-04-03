package com.com2us.mobility.service;

import com.com2us.mobility.domain.dispatch.DispatchStatus;
import com.com2us.mobility.domain.vehicle.VehicleGrade;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchSseService {

    private final SseEmitterService sseEmitterService;
    private static final String EVENT_NAME = "dispatch-status";

    // ─────────────────────────────────────────
    // 탐색 중 (주변 차량 포함)
    // ─────────────────────────────────────────
    public void sendSearching(Long dispatchId, String passengerName,
                              int radiusKm, List<NearbyVehicle> nearbyVehicles) {
        SsePayload payload = SsePayload.builder()
                .dispatchId(dispatchId)
                .passengerName(passengerName)
                .status(DispatchStatus.PROCESSING)
                .message(radiusKm + "km 반경 내 차량 탐색 중...")
                .nearbyVehicles(nearbyVehicles)
                .build();

        sseEmitterService.send(dispatchId, EVENT_NAME, payload);
    }

    // ─────────────────────────────────────────
    // 반경 확장 중
    // ─────────────────────────────────────────
    public void sendExpanding(Long dispatchId, String passengerName,
                              int radiusKm, List<NearbyVehicle> nearbyVehicles) {
        SsePayload payload = SsePayload.builder()
                .dispatchId(dispatchId)
                .passengerName(passengerName)
                .status(DispatchStatus.PROCESSING)
                .message(radiusKm + "km 반경으로 확대 중...")
                .nearbyVehicles(nearbyVehicles)
                .build();

        sseEmitterService.send(dispatchId, EVENT_NAME, payload);
    }

    // ─────────────────────────────────────────
    // 배차 완료
    // ─────────────────────────────────────────
    public void sendDispatched(Long dispatchId, String passengerName,
                               String vehicleNumber, Long vehicleId) {
        SsePayload payload = SsePayload.builder()
                .dispatchId(dispatchId)
                .passengerName(passengerName)
                .status(DispatchStatus.DISPATCHED)
                .vehicleId(vehicleId)
                .vehicleNumber(vehicleNumber)
                .message("배차가 완료됐어요!")
                .build();

        sseEmitterService.send(dispatchId, EVENT_NAME, payload);
        sseEmitterService.complete(dispatchId);
    }

    // ─────────────────────────────────────────
    // 배차 실패
    // ─────────────────────────────────────────
    public void sendFailed(Long dispatchId, String passengerName) {
        SsePayload payload = SsePayload.builder()
                .dispatchId(dispatchId)
                .passengerName(passengerName)
                .status(DispatchStatus.FAILED)
                .message("주변에 배차 가능한 차량이 없어요.")
                .build();

        sseEmitterService.send(dispatchId, EVENT_NAME, payload);
        sseEmitterService.complete(dispatchId);
    }

    // ─────────────────────────────────────────
    // SSE 페이로드
    // ─────────────────────────────────────────
    @Getter
    @Builder
    public static class SsePayload {
        private final Long dispatchId;
        private final String passengerName;
        private final DispatchStatus status;
        private final Long vehicleId;
        private final String vehicleNumber;
        private final String message;
        private final List<NearbyVehicle> nearbyVehicles;
    }

    @Getter
    @Builder
    public static class NearbyVehicle {
        private final Long vehicleId;
        private final String vehicleNumber;
        private final VehicleGrade grade;
        private final double distanceKm;
    }
}