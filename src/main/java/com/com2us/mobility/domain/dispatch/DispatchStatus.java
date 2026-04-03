package com.com2us.mobility.domain.dispatch;

public enum DispatchStatus {
    REQUESTED,   // 콜 요청됨
    PROCESSING,  // 배차 처리 중 (워커가 잡은 상태)
    WORKER_FAILED, // 워커에서 실패 함
    DISPATCHED,  // 차량 배차됨
    BOARDED,     // 승객 승차
    COMPLETED,   // 운행 완료
    REFUSED,     // 기사 거절 (NORMAL 등급만 가능)
    CANCELLED,   // 승객 취소
    FAILED       // 배차 실패 (재시도 후에도 차량 없음)
}