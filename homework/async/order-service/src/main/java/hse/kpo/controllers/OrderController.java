package hse.kpo.controllers;

import hse.kpo.dto.requests.CreateOrderRequest;
import hse.kpo.dto.responses.OrderResponse;
import hse.kpo.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления заказами.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "API для управления заказами")
public class OrderController {

    private final OrderService orderService;

    /**
     * Создание нового заказа.
     * Асинхронно запускает процесс оплаты.
     */
    @PostMapping
    @Operation(summary = "Создать заказ",
            description = "Создает новый заказ и асинхронно запускает процесс оплаты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно создан",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Получение списка заказов пользователя.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить список заказов",
            description = "Возвращает список всех заказов пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список заказов",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class))))
    })
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Получение статуса конкретного заказа.
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Получить статус заказа",
            description = "Возвращает информацию о заказе и его текущий статус")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о заказе",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID заказа") @PathVariable Long orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }
}
