package hse.kpo.domains;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HandEngineTest {

    @Test
    void compatibleWhenHandPowerAboveThreshold() {
        HandEngine engine = new HandEngine();

        assertTrue(engine.isCompatible(new Customer("Strong", 1, 6)));
        assertFalse(engine.isCompatible(new Customer("Weak", 7, 5)));
    }
}
