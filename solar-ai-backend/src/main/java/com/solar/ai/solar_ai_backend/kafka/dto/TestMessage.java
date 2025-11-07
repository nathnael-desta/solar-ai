package com.solar.ai.solar_ai_backend.kafka.dto;

import lombok.Data;

@Data
public class TestMessage {
    private String key;     // optional; if null, Kafka will round-robin the partition
    private String message; // the text to send
}
