package hse.kpo.controllers;

import hse.kpo.dto.requests.CreateAccountRequest;
import hse.kpo.dto.requests.DepositRequest;
import hse.kpo.dto.responses.AccountResponse;
import hse.kpo.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

/**
 * REST контроллер для управления счетами пользователей.
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "API для управления счетами пользователей")
public class AccountController {

    private final AccountService accountService;

    /**
     * Создание нового счета для пользователя.
     */
    @PostMapping
    @Operation(summary = "Создать счет",
            description = "Создает новый счет для пользователя. У каждого пользователя может быть только один счет.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Счет успешно создан",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "409", description = "Счет уже существует для данного пользователя"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Пополнение счета пользователя.
     */
    @PostMapping("/deposit")
    @Operation(summary = "Пополнить счет",
            description = "Пополняет счет пользователя на указанную сумму")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Счет успешно пополнен",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Счет не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    public ResponseEntity<AccountResponse> deposit(
            @Valid @RequestBody DepositRequest request) {
        AccountResponse response = accountService.deposit(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Получение баланса счета пользователя.
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Получить баланс",
            description = "Возвращает информацию о счете и текущий баланс пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о счете",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Счет не найден")
    })
    public ResponseEntity<AccountResponse> getBalance(
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {
        AccountResponse response = accountService.getBalance(userId);
        return ResponseEntity.ok(response);
    }
}
