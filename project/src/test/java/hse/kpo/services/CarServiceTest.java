package hse.kpo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import hse.kpo.domains.Car;
import hse.kpo.domains.Customer;
import hse.kpo.factories.HandCarFactory;
import hse.kpo.factories.PedalCarFactory;
import hse.kpo.params.EmptyEngineParams;
import hse.kpo.params.PedalEngineParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarServiceTest {

    private CarService carService;
    private PedalCarFactory pedalCarFactory;
    private HandCarFactory handCarFactory;

    @BeforeEach
    void setUp() {
        carService = new CarService();
        pedalCarFactory = new PedalCarFactory();
        handCarFactory = new HandCarFactory();
    }

    @Test
    void assignsIncrementingVinToNewCars() {
        carService.addCar(pedalCarFactory, new PedalEngineParams(5));
        carService.addCar(handCarFactory, EmptyEngineParams.DEFAULT);

        Car firstCar = carService.takeCar(new Customer("Legs", 9, 0));
        Car secondCar = carService.takeCar(new Customer("Hands", 0, 9));

        assertEquals(1, firstCar.getVIN());
        assertEquals(2, secondCar.getVIN());
    }

    @Test
    void removesCarWhenCustomerTakesCompatibleOne() {
        carService.addCar(pedalCarFactory, new PedalEngineParams(5));

        Customer customer = new Customer("Legs", 9, 1);
        Car firstAttempt = carService.takeCar(customer);
        Car secondAttempt = carService.takeCar(customer);

        assertNotNull(firstAttempt);
        assertNull(secondAttempt);
    }

    @Test
    void returnsNullWhenNoCompatibleCars() {
        carService.addCar(handCarFactory, EmptyEngineParams.DEFAULT);

        Customer customer = new Customer("NoHands", 8, 2);

        assertNull(carService.takeCar(customer));
    }
}
