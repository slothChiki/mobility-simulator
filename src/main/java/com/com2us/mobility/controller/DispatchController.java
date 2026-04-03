package com.com2us.mobility.controller;

import com.com2us.mobility.core.exception.CommonException;
import com.com2us.mobility.core.exception.ErrorCode;
import com.com2us.mobility.domain.dispatch.DispatchRecord;
import com.com2us.mobility.domain.dispatch.DispatchStatus;
import com.com2us.mobility.domain.passenger.Passenger;
import com.com2us.mobility.domain.vehicle.VehicleGrade;
import com.com2us.mobility.repository.DispatchRecordRepository;
import com.com2us.mobility.repository.PassengerRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/request-dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final PassengerRepository passengerRepository;
    private final DispatchRecordRepository dispatchRecordRepository;

    @PostMapping
    public ResponseDispatch dispatch(@RequestBody RequestDispatch request) {

        // 1. 승객 조회 — 없으면 예외
        Passenger passenger = passengerRepository.findById(request.getPassengerId())
                .orElseThrow(() -> new CommonException(ErrorCode.PASSENGER_NOT_FOUND));

        // 2. 배차 기록 생성 (REQUESTED) — 워커가 처리
        DispatchRecord record = DispatchRecord.create(
                passenger,
                null,
                request.getRequestedGrade(),
                request.getPickupLatitude(),
                request.getPickupLongitude()
        );
        dispatchRecordRepository.save(record);

        return ResponseDispatch.of(record);
    }

    // ─────────────────────────────────────────
    // Request
    // ─────────────────────────────────────────
    @Getter
    @NoArgsConstructor
    public static class RequestDispatch {
        private Long passengerId;
        private Double pickupLatitude;
        private Double pickupLongitude;
        private VehicleGrade requestedGrade;
    }

    // ─────────────────────────────────────────
    // Response
    // ─────────────────────────────────────────
    @Getter
    public static class ResponseDispatch {
        private final Long dispatchId;
        private final DispatchStatus status;
        private final String message;

        private ResponseDispatch(Long dispatchId, DispatchStatus status, String message) {
            this.dispatchId = dispatchId;
            this.status = status;
            this.message = message;
        }

        public static ResponseDispatch of(DispatchRecord record) {
            return new ResponseDispatch(
                    record.getId(),
                    record.getStatus(),
                    "배차 요청이 완료됐어요. 잠시만 기다려주세요."
            );
        }
    }
}