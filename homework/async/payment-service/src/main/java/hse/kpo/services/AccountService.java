package hse.kpo.services;

import hse.kpo.domains.Account;
import hse.kpo.dto.requests.CreateAccountRequest;
import hse.kpo.dto.requests.DepositRequest;
import hse.kpo.dto.responses.AccountResponse;
import hse.kpo.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

/**
 * Сервис для работы со счетами пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Создание нового счета для пользователя.
     * У каждого пользователя может быть только один счет.
     */
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account for user: {}", request.getUserId());

        if (accountRepository.existsByUserId(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Account already exists for user: " + request.getUserId());
        }

        Account account = Account.builder()
                .userId(request.getUserId())
                .balance(BigDecimal.ZERO)
                .build();

        account = accountRepository.save(account);
        log.info("Account created with id: {} for user: {}", account.getId(), request.getUserId());

        return mapToResponse(account);
    }

    /**
     * Пополнение счета пользователя.
     */
    @Transactional
    public AccountResponse deposit(DepositRequest request) {
        log.info("Depositing {} to account of user: {}", request.getAmount(), request.getUserId());

        // Используем блокировку для предотвращения race conditions
        Account account = accountRepository.findByUserIdForUpdate(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found for user: " + request.getUserId()));

        account.setBalance(account.getBalance().add(request.getAmount()));
        account = accountRepository.save(account);

        log.info("New balance for user {}: {}", request.getUserId(), account.getBalance());

        return mapToResponse(account);
    }

    /**
     * Получение баланса счета пользователя.
     */
    @Transactional(readOnly = true)
    public AccountResponse getBalance(Long userId) {
        log.info("Getting balance for user: {}", userId);

        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account not found for user: " + userId));

        return mapToResponse(account);
    }

    /**
     * Списание средств со счета (внутренний метод).
     * Используется при обработке платежей.
     *
     * @return true если списание успешно, false если недостаточно средств
     */
    @Transactional
    public boolean debit(Long userId, BigDecimal amount) {
        log.info("Debiting {} from account of user: {}", amount, userId);

        Account account = accountRepository.findByUserIdForUpdate(userId)
                .orElse(null);

        if (account == null) {
            log.warn("Account not found for user: {}", userId);
            return false;
        }

        if (account.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient funds for user: {}. Balance: {}, Required: {}",
                    userId, account.getBalance(), amount);
            return false;
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        log.info("Successfully debited {} from user {}. New balance: {}",
                amount, userId, account.getBalance());

        return true;
    }

    /**
     * Проверка существования счета.
     */
    public boolean accountExists(Long userId) {
        return accountRepository.existsByUserId(userId);
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
