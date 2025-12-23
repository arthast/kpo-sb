package hse.kpo.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hse.kpo.domains.Order;
import hse.kpo.dto.requests.CreateOrderRequest;
import hse.kpo.dto.responses.OrderResponse;
import hse.kpo.kafka.PaymentRequestEvent;
import hse.kpo.outbox.OutboxEvent;
import hse.kpo.outbox.OutboxEventRepository;
import hse.kpo.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с заказами.
 * Реализует создание заказов с Transactional Outbox паттерном.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Создание нового заказа.
     * В одной транзакции создается заказ и событие на оплату в outbox.
     * (Transactional Outbox - часть 1)
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}, amount: {}", request.getUserId(), request.getAmount());

        // Создаем заказ со статусом NEW
        Order order = Order.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .description(request.getDescription())
                .status(Order.OrderStatus.NEW)
                .build();

        order = orderRepository.save(order);
        log.info("Order created with id: {}", order.getId());

        // Создаем событие запроса на оплату в outbox (в той же транзакции)
        createPaymentRequestOutboxEvent(order);

        return mapToResponse(order);
    }

    /**
     * Получение списка заказов пользователя.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        log.info("Getting orders for user: {}", userId);

        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Получение статуса конкретного заказа.
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        log.info("Getting order by id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order not found: " + orderId));

        return mapToResponse(order);
    }

    /**
     * Обновление статуса заказа на основе результата оплаты.
     * Изменения идемпотентны, поэтому transactional inbox не обязателен.
     */
    @Transactional
    public void updateOrderStatus(Long orderId, boolean paymentSuccess, String failureReason) {
        log.info("Updating order status. Order: {}, Success: {}", orderId, paymentSuccess);

        Order order = orderRepository.findById(orderId)
                .orElse(null);

        if (order == null) {
            log.warn("Order not found for status update: {}", orderId);
            return;
        }

        // Идемпотентная операция - проверяем текущий статус
        if (order.getStatus() != Order.OrderStatus.NEW) {
            log.info("Order {} already has final status: {}", orderId, order.getStatus());
            return;
        }

        if (paymentSuccess) {
            order.setStatus(Order.OrderStatus.FINISHED);
            log.info("Order {} status updated to FINISHED", orderId);
        } else {
            order.setStatus(Order.OrderStatus.CANCELLED);
            log.info("Order {} status updated to CANCELLED. Reason: {}", orderId, failureReason);
        }

        orderRepository.save(order);
    }

    /**
     * Создает событие запроса на оплату в outbox таблице.
     */
    private void createPaymentRequestOutboxEvent(Order order) {
        PaymentRequestEvent paymentRequest = PaymentRequestEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(order.getId())
                .userId(order.getUserId())
                .amount(order.getAmount())
                .description(order.getDescription())
                .build();

        try {
            String payload = objectMapper.writeValueAsString(paymentRequest);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventId(paymentRequest.getEventId())
                    .aggregateType("ORDER")
                    .aggregateId(order.getId().toString())
                    .eventType("PAYMENT_REQUEST")
                    .payload(payload)
                    .sent(false)
                    .build();

            outboxEventRepository.save(outboxEvent);
            log.info("Created outbox event for payment request. Order: {}", order.getId());

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payment request event", e);
            throw new RuntimeException("Failed to create outbox event", e);
        }
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .amount(order.getAmount())
                .description(order.getDescription())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
