package hse.kpo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import hse.kpo.services.HseCarService;
import hse.kpo.services.HseCatamaranService;
import hse.kpo.builders.ReportSalesObserver;

/**
 * Точка входа в приложение.
 */
@SpringBootApplication
@RequiredArgsConstructor
public class KpoApplication {
	private final HseCarService carService;
	private final HseCatamaranService catService;
	private final ReportSalesObserver reportSalesObserver;

	public static void main(String[] args) {
		SpringApplication.run(KpoApplication.class, args);
	}

	@PostConstruct
	public void registerObservers() {
		carService.addObserver(reportSalesObserver);
		catService.addObserver(reportSalesObserver);
	}
}
