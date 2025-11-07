package com.solar.ai.solar_ai_backend.summary.api;

import com.solar.ai.solar_ai_backend.summary.dto.SummaryRequest;
import com.solar.ai.solar_ai_backend.kafka.SummaryRequestProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/administrations")
public class SummaryController {

    private final SummaryRequestProducer producer;

    /**
     * Submit a generate-summary request for this administration.
     * NOTE: This endpoint is async: it enqueues the job and returns broker details (partition/offset).
     */
    @PostMapping("/{id}/summary-requests")
    public CompletableFuture<ResponseEntity<String>> requestSummary(
            @PathVariable String id,
            @Valid @RequestBody SummaryRequest body
    ) {
        // Ensure path and body agree
        body.setAdministrationId(id);

        return producer.send(body).thenApply(meta -> {
            String msg = "enqueued topic=%s p=%d off=%d".formatted(meta.topic(), meta.partition(), meta.offset());
            return ResponseEntity.accepted().body(msg);
        });
    }
}
