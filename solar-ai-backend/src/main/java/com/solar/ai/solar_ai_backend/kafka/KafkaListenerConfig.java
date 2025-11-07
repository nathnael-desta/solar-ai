package com.solar.ai.solar_ai_backend.kafka;

import org.apache.kafka.common.KafkaException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaListenerConfig {

    @Bean
    public DefaultErrorHandler defaultErrorHandler() {
        // retry 3 times with 1s pause; then the record is skipped and the offset advances
        var backoff = new FixedBackOff(1000L, 3L);
        var handler = new DefaultErrorHandler(backoff);

        // (optional) add non-retryable exceptions
        handler.addNotRetryableExceptions(IllegalArgumentException.class, KafkaException.class);
        return handler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            DefaultErrorHandler errorHandler
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory);
        // concurrency can also be set here; we’ll use the annotation’s property for demo
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
