package com.com2us.mobility.controller;

import com.com2us.mobility.kafka.event.LocationEvent;
import com.com2us.mobility.kafka.producer.LocationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class KafkaTestController {

    private final LocationProducer locationProducer;

    @PostMapping("test-location")
    public ResponseEntity<?> sendLocation(@RequestBody LocationEvent event) {
        locationProducer.send(LocationEvent.builder()
                .vehicleId(event.getVehicleId())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .occurredAt(LocalDateTime.now())
                .build());

        return ResponseEntity.ok("전송 완료 vehicleId=" + event.getVehicleId());
    }
}