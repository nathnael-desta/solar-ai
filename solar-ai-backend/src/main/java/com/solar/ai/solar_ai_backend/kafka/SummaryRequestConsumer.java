package com.solar.ai.solar_ai_backend.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.ai.solar_ai_backend.model.AdministrationSummaryCache;
import com.solar.ai.solar_ai_backend.repository.AdministrationSummaryRepository;
import com.solar.ai.solar_ai_backend.summary.SummarizationWorker;
import com.solar.ai.solar_ai_backend.summary.dto.SummaryRequest;
import com.solar.ai.solar_ai_backend.util.ContextHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryRequestConsumer {

    private final ObjectMapper objectMapper;
    private final SummarizationWorker worker;
    private final AdministrationSummaryRepository repo;

    @KafkaListener(
            topics = "${app.kafka.topics.summary-requests:summary_requests}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "${app.kafka.consumer.concurrency:1}"
    )
    public void onMessage(
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) throws Exception {
        log.info("📥 summary_requests p={} off={} key={} payload='{}'", partition, offset, key, payload);

        // 1) Parse JSON to DTO
        SummaryRequest req = objectMapper.readValue(payload, SummaryRequest.class);

        // 2) Compute hash (stable, based on full request)
        String ctxHash = ContextHasher.sha256Base64(req);

        // 3) Idempotency: if same hash already cached for this admin, skip heavy work
        boolean fresh = repo.existsByAdministrationIdAndContextHash(req.getAdministrationId(), ctxHash);
        if (fresh) {
            log.info("Cache is fresh for {} (hash={}) — skipping recompute.", req.getAdministrationId(), ctxHash);
            return;
        }

        // 4) "Generate" summary (stub for now)
        String summary = worker.generateSummary(req);

        // 5) Upsert cache document
        var cache = AdministrationSummaryCache.builder()
                .administrationId(req.getAdministrationId())
                .summaryText(summary)
                .contextHash(ctxHash)
                .lastUpdated(Instant.now())
                .build();

        repo.save(cache);
        log.info("💾 Saved summary cache id={} hash={}", req.getAdministrationId(), ctxHash);
    }
}
