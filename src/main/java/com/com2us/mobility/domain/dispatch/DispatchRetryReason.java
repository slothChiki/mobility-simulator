package com.com2us.mobility.domain.dispatch;

public enum DispatchRetryReason {
    NO_AVAILABLE_VEHICLE,  // 주변 차량 없음
    LOCK_FAILED,           // 동시 배차 경합 실패
    VEHICLE_NOT_AVAILABLE  // 차량 상태 변경됨 (DISPATCHED)
}