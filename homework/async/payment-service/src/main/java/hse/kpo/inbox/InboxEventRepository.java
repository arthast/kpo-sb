package hse.kpo.inbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с Inbox событиями.
 */
@Repository
public interface InboxEventRepository extends JpaRepository<InboxEvent, Long> {

    /**
     * Проверить существование события по идентификатору.
     */
    boolean existsByEventId(String eventId);

    /**
     * Найти событие по идентификатору.
     */
    Optional<InboxEvent> findByEventId(String eventId);

    /**
     * Найти все необработанные события, отсортированные по времени создания.
     */
    List<InboxEvent> findAllByProcessedFalseOrderByCreatedAtAsc();
}
