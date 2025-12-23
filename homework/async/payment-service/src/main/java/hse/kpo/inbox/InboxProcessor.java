package hse.kpo.inbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import hse.kpo.kafka.PaymentRequestEvent;
import hse.kpo.services.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Процессор Transactional Inbox.
 * Периодически обрабатывает необработанные входящие события.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InboxProcessor {

    private final InboxEventRepository inboxEventRepository;
    private final PaymentProcessingService paymentProcessingService;
    private final ObjectMapper objectMapper;

    /**
     * Обрабатывает необработанные события из inbox.
     * Запускается каждые 5 секунд.
     */
    @Scheduled(fixedDelayString = "${inbox.processor.fixed-delay:5000}")
    @Transactional
    public void processInboxEvents() {
        List<InboxEvent> unprocessedEvents = inboxEventRepository
                .findAllByProcessedFalseOrderByCreatedAtAsc();

        if (unprocessedEvents.isEmpty()) {
            return;
        }

        log.info("Processing {} inbox events", unprocessedEvents.size());

        for (InboxEvent event : unprocessedEvents) {
            try {
                processEvent(event);
                event.setProcessed(true);
                event.setProcessedAt(LocalDateTime.now());
                inboxEventRepository.save(event);
                log.info("Successfully processed inbox event: {}", event.getEventId());
            } catch (Exception e) {
                log.error("Failed to process inbox event: {}", event.getEventId(), e);
                // Не помечаем как обработанное, попробуем снова
            }
        }
    }

    private void processEvent(InboxEvent event) {
        if ("PAYMENT_REQUEST".equals(event.getEventType())) {
            try {
                PaymentRequestEvent paymentRequest = objectMapper.readValue(
                        event.getPayload(), PaymentRequestEvent.class);
                paymentProcessingService.processPaymentRequest(paymentRequest);
            } catch (Exception e) {
                log.error("Failed to deserialize payment request event", e);
                throw new RuntimeException("Failed to process payment request", e);
            }
        } else {
            log.warn("Unknown event type: {}", event.getEventType());
        }
    }
}
