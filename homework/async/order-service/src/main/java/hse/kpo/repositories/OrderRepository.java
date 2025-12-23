package hse.kpo.repositories;

import hse.kpo.domains.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с заказами.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Найти все заказы пользователя.
     */
    List<Order> findByUserId(Long userId);

    /**
     * Найти все заказы пользователя, отсортированные по дате создания (новые первые).
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
