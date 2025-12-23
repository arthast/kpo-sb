package hse.kpo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI/Swagger для Payment Service.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .version("1.0")
                        .description("API сервиса платежей интернет-магазина Гоzон. " +
                                "Позволяет создавать счета, пополнять их и просматривать баланс.")
                        .contact(new Contact()
                                .name("HSE KPO")
                                .email("kpo@hse.ru")));
    }
}
