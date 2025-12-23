package hse.kpo.repositories;

import hse.kpo.domains.ProcessedPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с обработанными платежами.
 * Используется для обеспечения exactly-once семантики.
 */
@Repository
public interface ProcessedPaymentRepository extends JpaRepository<ProcessedPayment, Long> {

    /**
     * Найти обработанный платеж по идентификатору заказа.
     */
    Optional<ProcessedPayment> findByOrderId(Long orderId);

    /**
     * Проверить, был ли уже обработан платеж для заказа.
     */
    boolean existsByOrderId(Long orderId);
}
