package hse.kpo.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO для пополнения счета.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequest {

    /**
     * Идентификатор пользователя.
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * Сумма пополнения.
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;
}
