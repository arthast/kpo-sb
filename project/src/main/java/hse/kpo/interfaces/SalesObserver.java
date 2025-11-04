package hse.kpo.interfaces;

import hse.kpo.domains.Customer;
import hse.kpo.enums.ProductionTypes;

public interface SalesObserver {
    /**
     * Метод для уведомления о продаже объекта покупателю.
     *
     * @param customer - покупатель, которому был продан объект
     * @param type - тип проданного объекта
     * @param vin - уникальный идентификатор проданного объекта (VIN)
     */
    void onSale(Customer customer, ProductionTypes type, int vin);
}
