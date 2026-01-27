package org.aquariux.tradingsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tradingSystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Trading System API")
                        .description("Cryptocurrency Trading System API Documentation")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nhatpl")
                                .email("longnhat.pham112@gmail.com")));
    }
}
