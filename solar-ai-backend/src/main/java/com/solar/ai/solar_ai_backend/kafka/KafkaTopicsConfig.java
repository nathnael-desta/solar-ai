package com.solar.ai.solar_ai_backend.kafka;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Defining topics as beans lets Spring create them on startup via KafkaAdmin.
 * You’ll see log lines at boot saying the topics were created (or already exist).
 */
@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic appHealthcheckTopic() {
        // name, partitions, replicationFactor
        return new NewTopic("app_healthcheck", 1, (short) 1);
    }

    @Bean
    public NewTopic summaryRequestsTopic() {
        return new NewTopic("summary_requests", 1, (short) 1);
    }
}