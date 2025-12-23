package hse.kpo.controllers;

import hse.kpo.dto.requests.CreateAccountRequest;
import hse.kpo.dto.requests.DepositRequest;
import hse.kpo.services.PaymentProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * API Gateway контроллер для платежей.
 * Маршрутизирует запросы к Payment Service.
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "API для управления счетами пользователей")
public class PaymentGatewayController {

    private final PaymentProxyService paymentProxyService;

    /**
     * Создание нового счета.
     */
    @PostMapping
    @Operation(summary = "Создать счет",
            description = "Создает новый счет для пользователя. У каждого пользователя может быть только один счет.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Счет успешно создан"),
            @ApiResponse(responseCode = "409", description = "Счет уже существует"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    public ResponseEntity<Map<String, Object>> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("userId", request.getUserId());

        Map<String, Object> response = paymentProxyService.createAccount(requestMap);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Пополнение счета.
     */
    @PostMapping("/deposit")
    @Operation(summary = "Пополнить счет",
            description = "Пополняет счет пользователя на указанную сумму")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Счет успешно пополнен"),
            @ApiResponse(responseCode = "404", description = "Счет не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    public ResponseEntity<Map<String, Object>> deposit(
            @Valid @RequestBody DepositRequest request) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("userId", request.getUserId());
        requestMap.put("amount", request.getAmount());

        Map<String, Object> response = paymentProxyService.deposit(requestMap);
        return ResponseEntity.ok(response);
    }

    /**
     * Получение баланса.
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Получить баланс",
            description = "Возвращает информацию о счете и текущий баланс пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о счете"),
            @ApiResponse(responseCode = "404", description = "Счет не найден")
    })
    public ResponseEntity<Map<String, Object>> getBalance(
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {
        Map<String, Object> account = paymentProxyService.getBalance(userId);
        return ResponseEntity.ok(account);
    }
}
