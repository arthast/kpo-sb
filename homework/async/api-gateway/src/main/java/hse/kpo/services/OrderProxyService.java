package hse.kpo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Сервис для проксирования запросов к Order Service.
 */
@Service
@Slf4j
public class OrderProxyService {

    private final WebClient orderServiceWebClient;

    public OrderProxyService(@Qualifier("orderServiceWebClient") WebClient orderServiceWebClient) {
        this.orderServiceWebClient = orderServiceWebClient;
    }

    /**
     * Создание заказа.
     */
    public Map<String, Object> createOrder(Map<String, Object> request) {
        log.info("Proxying create order request");

        return orderServiceWebClient.post()
                .uri("/api/orders")
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
     * Получение списка заказов пользователя.
     */
    public List<Map<String, Object>> getOrdersByUserId(Long userId) {
        log.info("Proxying get orders request for user: {}", userId);

        return orderServiceWebClient.get()
                .uri("/api/orders/user/{userId}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new ResponseStatusException(response.statusCode(), body))))
                .bodyToFlux(Map.class)
                .map(m -> (Map<String, Object>) m)
                .collectList()
                .block();
    }

    /**
     * Получение статуса заказа.
     */
    public Map<String, Object> getOrderById(Long orderId) {
        log.info("Proxying get order request for id: {}", orderId);

        return orderServiceWebClient.get()
                .uri("/api/orders/{orderId}", orderId)
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
