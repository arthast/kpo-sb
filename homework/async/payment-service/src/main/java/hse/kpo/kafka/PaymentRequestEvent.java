package hse.kpo.kafka;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Событие запроса на оплату заказа.
 * Отправляется из Order Service в Payment Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class PaymentRequestEvent {

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
     * Сумма к оплате.
     */
    private BigDecimal amount;

    /**
     * Описание заказа.
     */
    private String description;
}
