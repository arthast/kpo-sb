package hse.kpo.domains;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hse.kpo.interfaces.IEngine;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CarTest {

    @Test
    void returnsVinPassedInConstructor() {
        IEngine engine = Mockito.mock(IEngine.class);
        Car car = new Car(42, engine);

        assertEquals(42, car.getVIN());
    }

    @Test
    void delegatesCompatibilityCheckToEngine() {
        IEngine engine = Mockito.mock(IEngine.class);
        Customer customer = new Customer("Tester", 3, 3);
        when(engine.isCompatible(customer)).thenReturn(true);

        Car car = new Car(1, engine);

        assertTrue(car.isCompatible(customer));
        verify(engine).isCompatible(customer);

        when(engine.isCompatible(customer)).thenReturn(false);
        assertFalse(car.isCompatible(customer));
    }
}
