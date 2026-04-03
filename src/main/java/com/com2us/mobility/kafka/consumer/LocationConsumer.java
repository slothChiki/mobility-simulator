package com.com2us.mobility.kafka.consumer;

import com.com2us.mobility.config.KafkaConfig;
import com.com2us.mobility.core.redis.GeoRedisRepository;
import com.com2us.mobility.kafka.event.LocationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationConsumer {

    private static final String GEO_KEY = "vehicles:locations";

    private final GeoRedisRepository geoRedisRepository;

    @KafkaListener(
            topics = KafkaConfig.TOPIC_VEHICLE_LOCATION,
            groupId = "mobility-group"
    )
    public void consume(LocationEvent event) {
        geoRedisRepository.add(
                GEO_KEY,
                event.getLatitude(),
                event.getLongitude(),
                "vehicle:" + event.getVehicleId()
        );
    }
}