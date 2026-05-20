package com.petmatch.rol_service.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rolServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rol Service API")
                        .version("1.0.0")
                        .description("API REST para la gestión de roles del sistema de PetMatch.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("soporte@PetMatch.cl"))
                        .license(new License()
                                .name("Uso académico")
                                .url("https://www.mascotas.cl")));
    }
}