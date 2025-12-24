package hse.kpo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import hse.kpo.domains.Car;
import hse.kpo.domains.Customer;
import hse.kpo.domains.HandEngine;
import hse.kpo.factories.HandCarFactory;
import hse.kpo.factories.PedalCarFactory;
import hse.kpo.params.EmptyEngineParams;
import hse.kpo.params.PedalEngineParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HseCarServiceTest {

    private CustomerStorage customerStorage;
    private CarService carService;
    private HseCarService hseCarService;

    @BeforeEach
    void setUp() {
        customerStorage = new CustomerStorage();
        carService = new CarService();
        hseCarService = new HseCarService(carService, customerStorage);
    }

    @Test
    void assignsCompatibleCarsInOrder() {
        PedalCarFactory pedalCarFactory = new PedalCarFactory();
        HandCarFactory handCarFactory = new HandCarFactory();

        carService.addCar(pedalCarFactory, new PedalEngineParams(6));
        carService.addCar(handCarFactory, EmptyEngineParams.DEFAULT);

        Customer pedalCustomer = new Customer("Legs", 7, 3);
        Customer handCustomer = new Customer("Hands", 2, 8);
        Customer noMatchCustomer = new Customer("None", 3, 2);

        customerStorage.addCustomer(pedalCustomer);
        customerStorage.addCustomer(handCustomer);
        customerStorage.addCustomer(noMatchCustomer);

        hseCarService.sellCars();

        assertEquals(1, pedalCustomer.getCar().getVIN());
        assertEquals(2, handCustomer.getCar().getVIN());
        assertNull(noMatchCustomer.getCar());
    }

    @Test
    void keepsExistingCarUntouched() {
        Customer customer = new Customer("Owner", 10, 10);
        customer.setCar(new Car(99, new HandEngine()));
        customerStorage.addCustomer(customer);

        carService.addCar(new PedalCarFactory(), new PedalEngineParams(6));

        hseCarService.sellCars();

        assertEquals(99, customer.getCar().getVIN());
    }
}
