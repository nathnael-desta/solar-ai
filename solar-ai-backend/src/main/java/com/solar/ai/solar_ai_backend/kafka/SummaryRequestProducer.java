package com.solar.ai.solar_ai_backend.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.ai.solar_ai_backend.summary.dto.SummaryRequest;
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
public class SummaryRequestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // Allow override via property: app.kafka.topics.summary-requests=summary_requests
    @Value("${app.kafka.topics.summary-requests:summary_requests}")
    private String summaryRequestsTopic;

    /**
     * Send a message to the summary_requests topic.
     * @return a future with the RecordMetadata (partition/offset) when the broker acks.
     */
    public CompletableFuture<RecordMetadata> send(String key, String payload) {
        var future = kafkaTemplate.send(summaryRequestsTopic, key, payload);
        // Spring Kafka 3 returns CompletableFuture<SendResult<...>>
        return future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka send FAILED topic={}, key={}, err={}",
                        summaryRequestsTopic, key, ex.toString());
            } else if (result != null) {
                var meta = result.getRecordMetadata();
                log.info("Kafka send OK topic={} partition={} offset={} key={}",
                        meta.topic(), meta.partition(), meta.offset(), key);
            }
        }).thenApply(SendResult::getRecordMetadata);
    }

    public CompletableFuture<RecordMetadata> send(SummaryRequest req) {
        try {
            String json = objectMapper.writeValueAsString(req);
            String key = req.getAdministrationId(); // key = adminId (keeps per-id ordering)
            return send(key, json);
        } catch (Exception e) {
            CompletableFuture<RecordMetadata> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }
}
