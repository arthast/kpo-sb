package hse.kpo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Конфигурация WebClient для вызовов к микросервисам.
 */
@Configuration
public class WebClientConfig {

    @Value("${services.order-service.url}")
    private String orderServiceUrl;

    @Value("${services.payment-service.url}")
    private String paymentServiceUrl;

    @Bean
    public WebClient orderServiceWebClient() {
        return WebClient.builder()
                .baseUrl(orderServiceUrl)
                .build();
    }

    @Bean
    public WebClient paymentServiceWebClient() {
        return WebClient.builder()
                .baseUrl(paymentServiceUrl)
                .build();
    }
}
