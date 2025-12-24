package hse.kpo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import hse.kpo.domains.Customer;
import java.util.List;
import org.junit.jupiter.api.Test;

class CustomerStorageTest {

    @Test
    void addsCustomersToInternalList() {
        CustomerStorage storage = new CustomerStorage();
        Customer customer = new Customer("Ivan", 5, 5);

        storage.addCustomer(customer);

        List<Customer> customers = storage.getCustomers();
        assertEquals(1, customers.size());
        assertSame(customer, customers.getFirst());
    }
}
