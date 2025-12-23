package hse.kpo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import hse.kpo.inbox.InboxEvent;
import hse.kpo.inbox.InboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Kafka Consumer для получения запросов на оплату.
 * Сохраняет события в Inbox для надежной обработки.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestConsumer {

    private final InboxEventRepository inboxEventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Получает запрос на оплату и сохраняет в inbox.
     * Обеспечивает at-least-once доставку с идемпотентной обработкой.
     */
    @KafkaListener(topics = "${kafka.topics.payment-requests:payment-requests}",
            groupId = "${spring.kafka.consumer.group-id:payment-service-group}")
    @Transactional
    public void consumePaymentRequest(PaymentRequestEvent event) {
        log.info("Received payment request for order: {}, user: {}, amount: {}",
                event.getOrderId(), event.getUserId(), event.getAmount());

        // Проверяем, не обрабатывали ли мы уже это событие (идемпотентность)
        if (inboxEventRepository.existsByEventId(event.getEventId())) {
            log.info("Payment request {} already exists in inbox, skipping", event.getEventId());
            return;
        }

        try {
            // Сохраняем событие в inbox для последующей обработки
            String payload = objectMapper.writeValueAsString(event);

            InboxEvent inboxEvent = InboxEvent.builder()
                    .eventId(event.getEventId())
                    .aggregateType("ORDER")
                    .eventType("PAYMENT_REQUEST")
                    .payload(payload)
                    .processed(false)
                    .build();

            inboxEventRepository.save(inboxEvent);
            log.info("Saved payment request to inbox: {}", event.getEventId());

        } catch (Exception e) {
            log.error("Failed to save payment request to inbox", e);
            throw new RuntimeException("Failed to process payment request", e);
        }
    }
}
