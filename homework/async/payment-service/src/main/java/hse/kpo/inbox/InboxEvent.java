package hse.kpo.inbox;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность для Transactional Inbox паттерна.
 * Сохраняет входящие события для идемпотентной обработки.
 */
@Entity
@Table(name = "inbox_events", indexes = {
        @Index(name = "idx_inbox_event_id", columnList = "event_id", unique = true),
        @Index(name = "idx_inbox_processed", columnList = "processed")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальный идентификатор события (для идемпотентности).
     */
    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    /**
     * Тип агрегата (например, "ORDER").
     */
    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    /**
     * Тип события (например, "PAYMENT_REQUEST").
     */
    @Column(name = "event_type", nullable = false)
    private String eventType;

    /**
     * JSON payload события.
     */
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    /**
     * Флаг обработки события.
     */
    @Column(name = "processed", nullable = false)
    private Boolean processed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (processed == null) {
            processed = false;
        }
    }
}
