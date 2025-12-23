package hse.kpo.outbox;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность для Transactional Outbox паттерна.
 * Сохраняет события для надежной отправки в Kafka.
 */
@Entity
@Table(name = "outbox_events", indexes = {
        @Index(name = "idx_outbox_sent", columnList = "sent")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальный идентификатор события.
     */
    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    /**
     * Тип агрегата (например, "PAYMENT").
     */
    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    /**
     * Тип события (например, "PAYMENT_RESULT").
     */
    @Column(name = "event_type", nullable = false)
    private String eventType;

    /**
     * JSON payload события.
     */
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    /**
     * Флаг отправки события.
     */
    @Column(name = "sent", nullable = false)
    private Boolean sent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (sent == null) {
            sent = false;
        }
    }
}
