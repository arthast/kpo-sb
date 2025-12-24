package hse.kpo.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hse.kpo.domains.Car;
import hse.kpo.domains.Customer;
import hse.kpo.params.PedalEngineParams;
import org.junit.jupiter.api.Test;

class PedalCarFactoryTest {

    private final PedalCarFactory factory = new PedalCarFactory();

    @Test
    void buildsCarWithIncrementedVin() {
        Car car = factory.createCar(new PedalEngineParams(5), 7);

        assertEquals(7, car.getVIN());
    }

    @Test
    void createsCarWithPedalEngine() {
        Car car = factory.createCar(new PedalEngineParams(9), 1);

        assertTrue(car.isCompatible(new Customer("PedalFan", 9, 0)));
        assertTrue(car.toString().contains("size=9"));
    }
}
