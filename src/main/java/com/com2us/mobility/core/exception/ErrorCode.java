package com.com2us.mobility.core.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Kafka
    KAFKA_PUBLISH_FAILED(5001, "Kafka 메시지 전송 실패"),

    // Dispatch
    DISPATCH_NOT_FOUND(4001, "배차 정보를 찾을 수 없음"),
    NO_AVAILABLE_VEHICLE(4002, "배차 가능한 차량 없음"),
    ALREADY_DISPATCHED(4003, "이미 배차된 차량"),

    // Vehicle
    VEHICLE_NOT_FOUND(4004, "차량 정보를 찾을 수 없음"),

    // Passenger
    PASSENGER_NOT_FOUND(4005, "승객 정보를 찾을 수 없음");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}