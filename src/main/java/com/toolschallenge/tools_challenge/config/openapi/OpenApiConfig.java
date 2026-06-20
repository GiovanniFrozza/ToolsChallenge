package com.toolschallenge.tools_challenge.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tools Challenge - API de Pagamentos")
                        .description("API REST para pagamento, consulta e estorno de transações de cartão.")
                        .version("1.0.0"));
    }
}
