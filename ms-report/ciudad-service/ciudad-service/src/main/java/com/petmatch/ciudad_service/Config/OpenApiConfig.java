package com.petmatch.ciudad_service.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ciudadServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ciudad Service API")
                        .version("1.0.0")
                        .description("API REST para la gestión de ciudades del sistema PetMatch.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo PetMatch")
                                .email("soporte@petmatch.cl"))
                        .license(new License()
                                .name("Uso académico")
                                .url("https://www.petmatch.cl")));
    }
}