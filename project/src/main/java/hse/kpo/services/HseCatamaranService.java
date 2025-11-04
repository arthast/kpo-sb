package hse.kpo.services;

import hse.kpo.interfaces.CustomerProvider;
import hse.kpo.interfaces.SalesObserver;
import hse.kpo.interfaces.catamarans.CatamaranProvider;
import hse.kpo.domains.Catamaran;
import hse.kpo.domains.Customer;
import hse.kpo.enums.ProductionTypes;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Сервис продажи катамаранов.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HseCatamaranService {

    private final CatamaranProvider catamaranProvider;

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
     * Метод продажи катамаранов.
     */
    public void sellCatamarans() {
        // получаем список покупателей
        var customers = customerProvider.getCustomers();
        // пробегаемся по полученному списку
        customers.stream().filter(customer -> Objects.isNull(customer.getCatamaran()))
                .forEach(customer -> {
                    var catamaran = catamaranProvider.takeCatamaran(customer);
                    if (Objects.nonNull(catamaran)) {
                        customer.setCatamaran(catamaran);
                        // уведомляем наблюдателей о продаже
                        try {
                            notifyObserversOnSale(customer, ProductionTypes.CATAMARAN, catamaran.getVin());
                        } catch (Exception ex) {
                            log.error("Failed to notify observers after catamaran sale", ex);
                        }
                    } else {
                        log.warn("No catamaran in CatamaranService");
                    }
                });
    }
}