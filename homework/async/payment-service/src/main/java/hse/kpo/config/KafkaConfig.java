package hse.kpo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Конфигурация Kafka для Payment Service.
 */
@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.payment-results:payment-results}")
    private String paymentResultsTopic;

    @Bean
    public NewTopic paymentResultsTopic() {
        return TopicBuilder.name(paymentResultsTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
