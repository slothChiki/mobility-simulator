package com.com2us.mobility.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // Topic 이름 상수
    public static final String TOPIC_VEHICLE_LOCATION = "vehicle-location";
    public static final String TOPIC_DISPATCH_RESULT  = "dispatch-result";

    // ─────────────────────────────────────────
    // Topic 생성
    // 앱 시작 시 토픽이 없으면 자동 생성
    // ─────────────────────────────────────────

    @Bean
    public NewTopic vehicleLocationTopic() {
        return TopicBuilder.name(TOPIC_VEHICLE_LOCATION)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic dispatchResultTopic() {
        return TopicBuilder.name(TOPIC_DISPATCH_RESULT)
                .partitions(3)
                .replicas(1)
                .build();
    }
}