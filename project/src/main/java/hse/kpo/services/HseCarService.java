package hse.kpo.services;

import hse.kpo.interfaces.cars.CarProvider;
import hse.kpo.interfaces.CustomerProvider;
import hse.kpo.interfaces.SalesObserver;
import hse.kpo.domains.Customer;
import hse.kpo.enums.ProductionTypes;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Сервис продажи машин.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HseCarService {

    private final CarProvider carProvider;

    private final CustomerProvider customerProvider;

    // список наблюдателей
    private final List<SalesObserver> observers = new ArrayList<>();

    public void addObserver(SalesObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    private void notifyObserversOnSale(Customer customer, ProductionTypes type, int vin) {
        for (SalesObserver o : observers) {
            try {
                o.onSale(customer, type, vin);
            } catch (Exception ex) {
                log.error("Sales observer failed", ex);
            }
        }
    }

    /**
     * Метод продажи машин
     */
    public void sellCars() {
        // получаем список покупателей
        var customers = customerProvider.getCustomers();
        // пробегаемся по полученном списке
        customers.stream().filter(customer -> Objects.isNull(customer.getCar()))
                .forEach(customer -> {
                    var car = carProvider.takeCar(customer);
                    if (Objects.nonNull(car)) {
                        customer.setCar(car);
                        // уведомляем наблюдателей о продаже
                        try {
                            notifyObserversOnSale(customer, ProductionTypes.CAR, car.getVin());
                        } catch (Exception ex) {
                            log.error("Failed to notify observers after car sale", ex);
                        }
                    } else {
                        log.warn("No car in CarService");
                    }
                });
    }
}