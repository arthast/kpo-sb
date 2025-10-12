package org.example.domain;

import org.example.domain.animal.Monkey;
import org.example.domain.animal.Rabbit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HerboTest {
    @Test
    void kindnessBounds_enforced() {
        assertThrows(IllegalArgumentException.class, () -> new Rabbit("R", 1, 1, true, -1));
        assertThrows(IllegalArgumentException.class, () -> new Monkey("M", 2, 1, true, 11));
        // valid boundaries
        assertDoesNotThrow(() -> new Rabbit("R", 3, 1, true, 0));
        assertDoesNotThrow(() -> new Monkey("M", 4, 1, true, 10));
    }

    @Test
    void pettingAllowed_onlyAboveFive() {
        assertFalse(new Rabbit("R1", 1, 1, true, 5).isPettingAllowed());
        assertTrue(new Monkey("M1", 2, 1, true, 6).isPettingAllowed());
    }
}

