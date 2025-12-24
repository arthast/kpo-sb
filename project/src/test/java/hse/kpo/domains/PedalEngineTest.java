package hse.kpo.domains;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PedalEngineTest {

    @Test
    void exposesConfiguredSize() {
        PedalEngine engine = new PedalEngine(8);

        assertEquals(8, engine.getSize());
    }

    @Test
    void compatibleWhenLegPowerAboveThreshold() {
        PedalEngine engine = new PedalEngine(6);

        assertTrue(engine.isCompatible(new Customer("StrongLegs", 6, 0)));
        assertFalse(engine.isCompatible(new Customer("WeakLegs", 5, 10)));
    }
}
