package com.com2us.mobility.domain.vehicle;

public enum VehicleStatus {
    AVAILABLE,   // 배차 가능
    DISPATCHED,  // 배차 완료 (이동 중)
    OFFLINE      // 미운행
}