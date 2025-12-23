package hse.kpo.kafka;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Событие результата оплаты заказа.
 * Отправляется из Payment Service в Order Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class PaymentResultEvent {

    /**
     * Уникальный идентификатор события.
     */
    private String eventId;

    /**
     * Идентификатор заказа.
     */
    private Long orderId;

    /**
     * Идентификатор пользователя.
     */
    private Long userId;

    /**
     * Сумма платежа.
     */
    private BigDecimal amount;

    /**
     * Успешность платежа.
     */
    private Boolean success;

    /**
     * Причина неудачи (если применимо).
     */
    private String failureReason;
}
