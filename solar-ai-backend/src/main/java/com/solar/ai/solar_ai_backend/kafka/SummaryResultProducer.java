package com.solar.ai.solar_ai_backend.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.ai.solar_ai_backend.summary.dto.SummaryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryResultProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.summary-results:summary_results}")
    private String summaryResultsTopic;

    public CompletableFuture<RecordMetadata> send(SummaryResult result) {
        try {
            String key = result.getAdministrationId();
            String json = objectMapper.writeValueAsString(result);
            var fut = kafkaTemplate.send(summaryResultsTopic, key, json);

            return fut.whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("Result send FAILED topic={}, key={}, err={}", summaryResultsTopic, key, ex.toString());
                } else if (res != null) {
                    var m = res.getRecordMetadata();
                    log.info("Result send OK topic={} p={} off={} key={}", m.topic(), m.partition(), m.offset(), key);
                }
            }).thenApply(SendResult::getRecordMetadata);

        } catch (Exception e) {
            var failed = new CompletableFuture<RecordMetadata>();
            failed.completeExceptionally(e);
            return failed;
        }
    }
}
