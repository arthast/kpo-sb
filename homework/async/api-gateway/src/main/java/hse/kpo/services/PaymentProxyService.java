package hse.kpo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Сервис для проксирования запросов к Payment Service.
 */
@Service
@Slf4j
public class PaymentProxyService {

    private final WebClient paymentServiceWebClient;

    public PaymentProxyService(@Qualifier("paymentServiceWebClient") WebClient paymentServiceWebClient) {
        this.paymentServiceWebClient = paymentServiceWebClient;
    }

    /**
     * Создание счета.
     */
    public Map<String, Object> createAccount(Map<String, Object> request) {
        log.info("Proxying create account request");

        return paymentServiceWebClient.post()
                .uri("/api/accounts")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new ResponseStatusException(response.statusCode(), body))))
                .bodyToMono(Map.class)
                .map(m -> (Map<String, Object>) m)
                .block();
    }

    /**
     * Пополнение счета.
     */
    public Map<String, Object> deposit(Map<String, Object> request) {
        log.info("Proxying deposit request");

        return paymentServiceWebClient.post()
                .uri("/api/accounts/deposit")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new ResponseStatusException(response.statusCode(), body))))
                .bodyToMono(Map.class)
                .map(m -> (Map<String, Object>) m)
                .block();
    }

    /**
     * Получение баланса.
     */
    public Map<String, Object> getBalance(Long userId) {
        log.info("Proxying get balance request for user: {}", userId);

        return paymentServiceWebClient.get()
                .uri("/api/accounts/{userId}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new ResponseStatusException(response.statusCode(), body))))
                .bodyToMono(Map.class)
                .map(m -> (Map<String, Object>) m)
                .block();
    }
}
