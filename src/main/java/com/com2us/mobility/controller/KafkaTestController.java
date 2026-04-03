package com.com2us.mobility.controller;

import com.com2us.mobility.core.log.LogMarker;
import com.com2us.mobility.domain.dispatch.DispatchRecord;
import com.com2us.mobility.domain.passenger.Passenger;
import com.com2us.mobility.domain.vehicle.VehicleGrade;
import com.com2us.mobility.kafka.event.LocationEvent;
import com.com2us.mobility.kafka.producer.LocationProducer;
import com.com2us.mobility.repository.DispatchRecordRepository;
import com.com2us.mobility.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KafkaTestController {

    private final LocationProducer locationProducer;
    private final DispatchRecordRepository dispatchRecordRepository;
    private final PassengerRepository passengerRepository;

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

    @PostMapping("test-log")
    public void testLog() {
       LogMarker.debug("debug 왜 안나오는 걸까?");
    }

    @PostMapping("make-request")
    public void makeRequest() {


    }
}