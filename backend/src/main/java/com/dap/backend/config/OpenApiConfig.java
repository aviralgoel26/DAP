package com.dap.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dapOpenAPI() {

        return new OpenAPI()

                .info(

                        new Info()

                                .title("DAP API")

                                .version("v1.0")

                                .description(
                                        "Document Automation Platform APIs"
                                )

                                .contact(

                                        new Contact()

                                                .name("Aviral Goel")

                                                .email("your-email@example.com")

                                )

                );

    }

}