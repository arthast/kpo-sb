package hse.kpo.repositories;

import hse.kpo.domains.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы со счетами пользователей.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Найти счет по идентификатору пользователя.
     */
    Optional<Account> findByUserId(Long userId);

    /**
     * Проверить существование счета у пользователя.
     */
    boolean existsByUserId(Long userId);

    /**
     * Найти счет с пессимистичной блокировкой для обновления.
     * Используется при списании/пополнении для предотвращения race conditions.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.userId = :userId")
    Optional<Account> findByUserIdForUpdate(@Param("userId") Long userId);
}
