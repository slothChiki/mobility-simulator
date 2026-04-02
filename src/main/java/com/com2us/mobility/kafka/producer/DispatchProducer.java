package com.com2us.mobility.kafka.producer;

import com.com2us.mobility.config.KafkaConfig;
import com.com2us.mobility.core.exception.ErrorCode;
import com.com2us.mobility.core.exception.KafkaPublishException;
import com.com2us.mobility.kafka.event.DispatchEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DispatchProducer {

    private static final int MAX_RETRY = 3;

    private final KafkaTemplate<String, DispatchEvent> kafkaTemplate;

    public void send(DispatchEvent event) {
        int attempt = 0;

        while (attempt < MAX_RETRY) {
            try {
                kafkaTemplate.send(
                        KafkaConfig.TOPIC_DISPATCH_RESULT,
                        String.valueOf(event.getDispatchId()),
                        event
                ).get();
                return;

            } catch (Exception e) {
                attempt++;

                if (attempt == MAX_RETRY) {
                    throw new KafkaPublishException(ErrorCode.KAFKA_PUBLISH_FAILED, e);
                }

                try { // 천천히 재시도
                    Thread.sleep(500L * (long) Math.pow(2, attempt)); // 1000ms, 2000ms
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}