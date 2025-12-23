package hse.kpo.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hse.kpo.domains.ProcessedPayment;
import hse.kpo.kafka.PaymentRequestEvent;
import hse.kpo.kafka.PaymentResultEvent;
import hse.kpo.outbox.OutboxEvent;
import hse.kpo.outbox.OutboxEventRepository;
import hse.kpo.repositories.ProcessedPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Сервис обработки платежей.
 * Реализует exactly-once семантику при списании денег.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingService {

    private final AccountService accountService;
    private final ProcessedPaymentRepository processedPaymentRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Обработка запроса на оплату заказа.
     * Обеспечивает exactly-once семантику через проверку ProcessedPayment.
     */
    @Transactional
    public void processPaymentRequest(PaymentRequestEvent request) {
        log.info("Processing payment request for order: {}, user: {}, amount: {}",
                request.getOrderId(), request.getUserId(), request.getAmount());

        // Проверяем, был ли уже обработан платеж для этого заказа (exactly-once)
        Optional<ProcessedPayment> existingPayment = processedPaymentRepository
                .findByOrderId(request.getOrderId());

        if (existingPayment.isPresent()) {
            log.info("Payment for order {} was already processed with status: {}",
                    request.getOrderId(), existingPayment.get().getStatus());
            // Повторно отправляем результат, так как событие могло не дойти
            createOutboxEventForExistingPayment(existingPayment.get(), request);
            return;
        }

        // Проверяем существование счета
        if (!accountService.accountExists(request.getUserId())) {
            log.warn("Account not found for user: {}", request.getUserId());
            saveFailedPayment(request, "Account not found for user");
            return;
        }

        // Пытаемся списать деньги
        boolean debited = accountService.debit(request.getUserId(), request.getAmount());

        if (debited) {
            log.info("Payment successful for order: {}", request.getOrderId());
            saveSuccessfulPayment(request);
        } else {
            log.warn("Payment failed for order: {} - insufficient funds", request.getOrderId());
            saveFailedPayment(request, "Insufficient funds");
        }
    }

    private void saveSuccessfulPayment(PaymentRequestEvent request) {
        // Сохраняем запись об успешном платеже
        ProcessedPayment payment = ProcessedPayment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status(ProcessedPayment.PaymentStatus.SUCCESS)
                .build();
        processedPaymentRepository.save(payment);

        // Создаем событие результата в outbox
        createPaymentResultOutboxEvent(request, true, null);
    }

    private void saveFailedPayment(PaymentRequestEvent request, String reason) {
        // Сохраняем запись о неудачном платеже
        ProcessedPayment payment = ProcessedPayment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status(ProcessedPayment.PaymentStatus.FAILED)
                .failureReason(reason)
                .build();
        processedPaymentRepository.save(payment);

        // Создаем событие результата в outbox
        createPaymentResultOutboxEvent(request, false, reason);
    }

    private void createOutboxEventForExistingPayment(ProcessedPayment payment, PaymentRequestEvent request) {
        boolean success = payment.getStatus() == ProcessedPayment.PaymentStatus.SUCCESS;
        createPaymentResultOutboxEvent(request, success, payment.getFailureReason());
    }

    private void createPaymentResultOutboxEvent(PaymentRequestEvent request, boolean success, String failureReason) {
        PaymentResultEvent resultEvent = PaymentResultEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .success(success)
                .failureReason(failureReason)
                .build();

        try {
            String payload = objectMapper.writeValueAsString(resultEvent);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .aggregateType("PAYMENT")
                    .eventType("PAYMENT_RESULT")
                    .payload(payload)
                    .sent(false)
                    .build();

            outboxEventRepository.save(outboxEvent);
            log.info("Created outbox event for payment result. Order: {}, Success: {}",
                    request.getOrderId(), success);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payment result event", e);
            throw new RuntimeException("Failed to create outbox event", e);
        }
    }
}
