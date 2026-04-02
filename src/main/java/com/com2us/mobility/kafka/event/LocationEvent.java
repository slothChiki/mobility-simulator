package com.com2us.mobility.kafka.event;

import com.com2us.mobility.domain.vehicle.VehicleStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LocationEvent {

    private Long vehicleId;
    private Double latitude;
    private Double longitude;
    private VehicleStatus status;  // AVAILABLE / OFFLINE (차량이 보냄) / DISPATCHED (배차 시스템이 관리)
    private LocalDateTime occurredAt;
}