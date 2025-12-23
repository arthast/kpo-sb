package hse.kpo.controllers;

import hse.kpo.dto.requests.CreateOrderRequest;
import hse.kpo.services.OrderProxyService;
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
import java.util.List;
import java.util.Map;

/**
 * API Gateway контроллер для заказов.
 * Маршрутизирует запросы к Order Service.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "API для управления заказами")
public class OrderGatewayController {

    private final OrderProxyService orderProxyService;

    /**
     * Создание нового заказа.
     */
    @PostMapping
    @Operation(summary = "Создать заказ",
            description = "Создает новый заказ и асинхронно запускает процесс оплаты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    public ResponseEntity<Map<String, Object>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("userId", request.getUserId());
        requestMap.put("amount", request.getAmount());
        requestMap.put("description", request.getDescription());

        Map<String, Object> response = orderProxyService.createOrder(requestMap);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Получение списка заказов пользователя.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить список заказов",
            description = "Возвращает список всех заказов пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список заказов")
    })
    public ResponseEntity<List<Map<String, Object>>> getOrdersByUserId(
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {
        List<Map<String, Object>> orders = orderProxyService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Получение статуса заказа.
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Получить статус заказа",
            description = "Возвращает информацию о заказе и его текущий статус")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о заказе"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public ResponseEntity<Map<String, Object>> getOrderById(
            @Parameter(description = "ID заказа") @PathVariable Long orderId) {
        Map<String, Object> order = orderProxyService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
}
