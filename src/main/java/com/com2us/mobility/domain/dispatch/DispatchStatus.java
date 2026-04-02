package com.com2us.mobility.domain.dispatch;

public enum DispatchStatus {
    REQUESTED ,  // 콜 요청됨
    DISPATCHED,  // 차량 배차됨
    BOARDED   ,  // 승객 승차
    COMPLETED ,  // 운행 완료
    REFUSED   ,  // 거절
    CANCELLED ,  // 취소
}
