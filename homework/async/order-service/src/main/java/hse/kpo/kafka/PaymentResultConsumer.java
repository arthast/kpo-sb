package hse.kpo.kafka;

import hse.kpo.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka Consumer для получения результатов оплаты.
 * Обновляет статус заказа на основе результата.
 *
 * Примечание: Transactional Inbox не используется здесь,
 * так как обновление статуса заказа идемпотентно.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResultConsumer {

    private final OrderService orderService;

    /**
     * Получает результат оплаты и обновляет статус заказа.
     * Операция идемпотентна - повторное получение события не изменит результат.
     */
    @KafkaListener(topics = "${kafka.topics.payment-results:payment-results}",
            groupId = "${spring.kafka.consumer.group-id:order-service-group}")
    public void consumePaymentResult(PaymentResultEvent event) {
        log.info("Received payment result for order: {}, success: {}, reason: {}",
                event.getOrderId(), event.getSuccess(), event.getFailureReason());

        try {
            orderService.updateOrderStatus(
                    event.getOrderId(),
                    event.getSuccess(),
                    event.getFailureReason()
            );
            log.info("Successfully processed payment result for order: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("Failed to process payment result for order: {}", event.getOrderId(), e);
            // Kafka перепопробует доставку
            throw e;
        }
    }
}
