package hse.kpo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Конфигурация Kafka для Order Service.
 */
@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.payment-requests:payment-requests}")
    private String paymentRequestsTopic;

    @Bean
    public NewTopic paymentRequestsTopic() {
        return TopicBuilder.name(paymentRequestsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
