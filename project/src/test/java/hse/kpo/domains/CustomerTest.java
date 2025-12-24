package hse.kpo.domains;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void storesNameAndPowerValues() {
        Customer customer = new Customer("Ivan", 6, 4);

        assertEquals("Ivan", customer.getName());
        assertEquals(6, customer.getLegPower());
        assertEquals(4, customer.getHandPower());
    }

    @Test
    void canAssignCarLater() {
        Customer customer = new Customer("Nikita", 5, 5);

        assertNull(customer.getCar());

        Car car = new Car(1, new HandEngine());
        customer.setCar(car);

        assertSame(car, customer.getCar());
    }
}
