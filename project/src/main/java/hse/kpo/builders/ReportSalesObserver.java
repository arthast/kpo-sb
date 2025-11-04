package hse.kpo.builders;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import hse.kpo.interfaces.SalesObserver;
import hse.kpo.domains.Customer;
import hse.kpo.enums.ProductionTypes;

/**
 * Наблюдатель, который записывает продажу в отчет через ReportBuilder.
 * Подкорректируйте вызов reportBuilder согласно фактическому API ReportBuilder.
 */
@Component
@RequiredArgsConstructor
public class ReportSalesObserver implements SalesObserver {
    private final ReportBuilder reportBuilder;

    // Поддерживаем старую сигнатуру (если интерфейс в другой модуле её ещё содержит)
    public void onSale(Customer customer, ProductionTypes type) {
        // делегируем с vin = -1
        onSale(customer, type, -1);
    }

    // Современная сигнатура с VIN
    public void onSale(Customer customer, ProductionTypes type, int vin) {
        // Добавляем запись в отчет
        reportBuilder.addSaleEntry(customer, type, vin);
    }
}