package hse.kpo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Базовый тест для Order Service.
 */
@SpringBootTest
@ActiveProfiles("test")
class OrderServiceApplicationTests {

    @Test
    void contextLoads() {
        // Проверка загрузки контекста
    }
}
