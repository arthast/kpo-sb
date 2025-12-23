package hse.kpo.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с Outbox событиями.
 */
@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    /**
     * Найти все неотправленные события, отсортированные по времени создания.
     */
    List<OutboxEvent> findAllBySentFalseOrderByCreatedAtAsc();
}
