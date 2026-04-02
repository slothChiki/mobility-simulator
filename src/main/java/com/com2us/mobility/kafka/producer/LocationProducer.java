package com.com2us.mobility.kafka.producer;

import com.com2us.mobility.config.KafkaConfig;
import com.com2us.mobility.kafka.event.LocationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationProducer {

    private final KafkaTemplate<String, LocationEvent> kafkaTemplate;

    public void send(LocationEvent event) {
        kafkaTemplate.send(
                KafkaConfig.TOPIC_VEHICLE_LOCATION,
                String.valueOf(event.getVehicleId()),
                event
        );
    }
}