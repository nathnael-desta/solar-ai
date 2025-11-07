package com.solar.ai.solar_ai_backend.kafka;

import com.solar.ai.solar_ai_backend.kafka.dto.TestMessage;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class TestProducerController {

    private final SummaryRequestProducer producer;

    @PostMapping("/test")
    public CompletableFuture<ResponseEntity<String>> sendTest(@RequestBody TestMessage body) {
        String key = body.getKey();
        String msg = body.getMessage();
        return producer.send(key, msg)
                .thenApply(this::okResponse);
    }

    private ResponseEntity<String> okResponse(RecordMetadata meta) {
        String info = "Sent to " + meta.topic() + " p=" + meta.partition() + " off=" + meta.offset();
        return ResponseEntity.ok(info);
    }
}
