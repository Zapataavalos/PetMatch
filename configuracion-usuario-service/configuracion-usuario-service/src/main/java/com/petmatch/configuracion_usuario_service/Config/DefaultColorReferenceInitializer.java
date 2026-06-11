package com.petmatch.configuracion_usuario_service.Config;

import com.petmatch.configuracion_usuario_service.Model.ColorReferencia;
import com.petmatch.configuracion_usuario_service.Repository.ColorReferenciaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class DefaultColorReferenceInitializer {

    private static final List<ColorReferencia> DEFAULT_COLOR_REFERENCES = List.of(
            new ColorReferencia(1, "AMARILLO PETMATCH", "#F5C400", true),
            new ColorReferencia(2, "VERDE RESCATE", "#10B981", true),
            new ColorReferencia(3, "AZUL COMUNIDAD", "#60A5FA", true)
    );

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CommandLineRunner seedDefaultColorReferences(ColorReferenciaRepository colorReferenciaRepository) {
        return args -> colorReferenciaRepository.saveAll(DEFAULT_COLOR_REFERENCES);
    }
}
