package com.com2us.mobility.controller;

import com.com2us.mobility.domain.dispatch.DispatchRecord;
import com.com2us.mobility.domain.dispatch.DispatchStatus;
import com.com2us.mobility.domain.passenger.Passenger;
import com.com2us.mobility.domain.vehicle.VehicleGrade;
import com.com2us.mobility.repository.DispatchRecordRepository;
import com.com2us.mobility.repository.PassengerRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/api/dispatch/mass")
@RequiredArgsConstructor
public class DispatchMassController {

    private final PassengerRepository passengerRepository;
    private final DispatchRecordRepository dispatchRecordRepository;

    @PostMapping
    public List<ResponseDispatchMass> dispatch() {

        double[][] coords = {
                {37.4815, 126.8821},
                {37.4808, 126.8815},
                {37.4822, 126.8830},
                {37.4802, 126.8835},
                {37.4819, 126.8809},
                {37.4825, 126.8827},
                {37.4798, 126.8812},
                {37.4811, 126.8840},
                {37.4805, 126.8826},
                {37.4820, 126.8818}
        };

        List<Passenger> passengerList = passengerRepository.findAll();
        VehicleGrade[] vehicleGradeList = VehicleGrade.values();
        List<ResponseDispatchMass> result = new ArrayList<>();

        for (Passenger passenger : passengerList) {
            int random = new Random().nextInt(10);
            int random2 = new Random().nextInt(2);
            DispatchRecord record = DispatchRecord.create(
                    passenger,
                    null,
                    vehicleGradeList[random2],
                    coords[random][0],
                    coords[random][1]
            );
            dispatchRecordRepository.save(record);

            result.add(ResponseDispatchMass.of(record, passenger));
        }

        return result;
    }

    @Getter
    public static class ResponseDispatchMass {
        private final Long dispatchId;
        private final Long passengerId;
        private final String passengerName;
        private final DispatchStatus status;
        private final Double pickupLatitude;
        private final Double pickupLongitude;

        private ResponseDispatchMass(Long dispatchId, Long passengerId, String passengerName,
                                     DispatchStatus status, Double pickupLatitude, Double pickupLongitude) {
            this.dispatchId = dispatchId;
            this.passengerId = passengerId;
            this.passengerName = passengerName;
            this.status = status;
            this.pickupLatitude = pickupLatitude;
            this.pickupLongitude = pickupLongitude;
        }

        public static ResponseDispatchMass of(DispatchRecord record, Passenger passenger) {
            return new ResponseDispatchMass(
                    record.getId(),
                    passenger.getId(),
                    passenger.getName(),
                    record.getStatus(),
                    record.getPickupLatitude(),
                    record.getPickupLongitude()
            );
        }
    }
}