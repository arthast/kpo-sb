package hse.kpo.domains;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность для отслеживания обработанных платежей.
 * Обеспечивает семантику exactly-once при списании денег.
 */
@Entity
@Table(name = "processed_payments", indexes = {
        @Index(name = "idx_processed_payment_order_id", columnList = "order_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор заказа (уникальный - для idempotency).
     */
    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    /**
     * Идентификатор пользователя.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Сумма платежа.
     */
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Статус платежа: SUCCESS или FAILED.
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    /**
     * Причина неудачи (если применимо).
     */
    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        processedAt = LocalDateTime.now();
    }

    public enum PaymentStatus {
        SUCCESS,
        FAILED
    }
}
