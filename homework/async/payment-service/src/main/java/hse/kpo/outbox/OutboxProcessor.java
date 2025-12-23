package hse.kpo.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import hse.kpo.kafka.PaymentResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Процессор Transactional Outbox.
 * Периодически отправляет неотправленные события в Kafka.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, PaymentResultEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.payment-results:payment-results}")
    private String paymentResultsTopic;

    /**
     * Обрабатывает неотправленные события из outbox.
     * Запускается каждые 5 секунд.
     */
    @Scheduled(fixedDelayString = "${outbox.processor.fixed-delay:5000}")
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> unsentEvents = outboxEventRepository
                .findAllBySentFalseOrderByCreatedAtAsc();

        if (unsentEvents.isEmpty()) {
            return;
        }

        log.info("Processing {} outbox events", unsentEvents.size());

        for (OutboxEvent event : unsentEvents) {
            try {
                sendEvent(event);
                event.setSent(true);
                event.setSentAt(LocalDateTime.now());
                outboxEventRepository.save(event);
                log.info("Successfully sent outbox event: {}", event.getEventId());
            } catch (Exception e) {
                log.error("Failed to send outbox event: {}", event.getEventId(), e);
                // Не помечаем как отправленное, попробуем снова
            }
        }
    }

    private void sendEvent(OutboxEvent event) {
        if ("PAYMENT_RESULT".equals(event.getEventType())) {
            try {
                PaymentResultEvent resultEvent = objectMapper.readValue(
                        event.getPayload(), PaymentResultEvent.class);
                kafkaTemplate.send(paymentResultsTopic, resultEvent.getOrderId().toString(), resultEvent);
                log.info("Sent payment result to Kafka. Order: {}, Success: {}",
                        resultEvent.getOrderId(), resultEvent.getSuccess());
            } catch (Exception e) {
                log.error("Failed to send payment result event", e);
                throw new RuntimeException("Failed to send event to Kafka", e);
            }
        } else {
            log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}
