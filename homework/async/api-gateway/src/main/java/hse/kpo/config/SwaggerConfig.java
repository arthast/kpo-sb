package hse.kpo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI/Swagger для API Gateway.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Гоzон API Gateway")
                        .version("1.0")
                        .description("API Gateway интернет-магазина Гоzон. " +
                                "Единая точка входа для всех микросервисов: управление заказами и платежами.")
                        .contact(new Contact()
                                .name("HSE KPO")
                                .email("kpo@hse.ru")));
    }
}
