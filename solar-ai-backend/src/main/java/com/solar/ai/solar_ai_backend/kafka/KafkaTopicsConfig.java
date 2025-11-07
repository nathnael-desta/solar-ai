package com.solar.ai.solar_ai_backend.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic appHealthcheckTopic() {
        return TopicBuilder.name("app_healthcheck")
                .partitions(1)
                .replicas(1)
                .config("retention.ms", String.valueOf(24 * 60 * 60 * 1000)) // 1 day
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic summaryRequestsTopic() {
        return TopicBuilder.name("summary_requests")
                .partitions(1)
                .replicas(1)
                .config("retention.ms", String.valueOf(7L * 24 * 60 * 60 * 1000)) // 7 days
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic summaryResultsTopic() { // optional future topic
        return TopicBuilder.name("summary_results")
                .partitions(1)
                .replicas(1)
                .config("retention.ms", String.valueOf(7L * 24 * 60 * 60 * 1000))
                .config("cleanup.policy", "delete")
                .build();
    }
}
