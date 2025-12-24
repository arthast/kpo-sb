package hse.kpo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class KpoApplicationTests {

    @Test
    void applicationClassInstantiates() {
        assertDoesNotThrow(KpoApplication::new);
    }
}
