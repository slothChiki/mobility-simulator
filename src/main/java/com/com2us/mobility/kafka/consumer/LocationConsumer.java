package com.com2us.mobility.kafka.consumer;

import com.com2us.mobility.config.KafkaConfig;
import com.com2us.mobility.kafka.event.LocationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationConsumer {

    private static final String GEO_KEY = "vehicles:locations";

    private final RedisTemplate<String, String> redisTemplate;

    @KafkaListener(
            topics = KafkaConfig.TOPIC_VEHICLE_LOCATION,
            groupId = "mobility-group"
    )
    public void consume(LocationEvent event) {
        String member = "vehicle:" + event.getVehicleId();

        // AVAILABLE 차량만 GEO에 저장 (longitude 먼저, latitude 두번째 — GEO 표준)
        redisTemplate.opsForGeo().add(
                GEO_KEY,
                new Point(event.getLongitude(), event.getLatitude()),
                member
        );
    }
}