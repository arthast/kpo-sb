package hse.kpo.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hse.kpo.domains.Car;
import hse.kpo.domains.Customer;
import hse.kpo.params.EmptyEngineParams;
import org.junit.jupiter.api.Test;

class HandCarFactoryTest {

    private final HandCarFactory factory = new HandCarFactory();

    @Test
    void buildsCarWithExpectedVin() {
        Car car = factory.createCar(EmptyEngineParams.DEFAULT, 3);

        assertEquals(3, car.getVIN());
    }

    @Test
    void createsCarWithHandEngine() {
        Car car = factory.createCar(EmptyEngineParams.DEFAULT, 5);

        assertTrue(car.isCompatible(new Customer("StrongHands", 0, 7)));
        assertFalse(car.isCompatible(new Customer("WeakHands", 10, 5)));
    }
}
