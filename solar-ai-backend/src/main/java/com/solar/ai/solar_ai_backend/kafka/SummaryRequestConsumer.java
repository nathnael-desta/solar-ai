package com.solar.ai.solar_ai_backend.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SummaryRequestConsumer {

    @KafkaListener(
            topics = "summary_requests",
            groupId = "${spring.kafka.consumer.group-id}", // keep in sync with your YAML
            concurrency = "${app.kafka.consumer.concurrency:1}" // can raise later
    )
    public void onMessage(
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("📥 Consumed from topic=summary_requests p={} off={} key={} payload={}",
                partition, offset, key, payload);

        // 👉 later, you’ll parse payload (JSON/DTO), call LLM, update Mongo cache, produce result, etc.
    }
}
