package com.petmatch.pais_service.Config;

import com.petmatch.pais_service.Event.PaisEventPublisher;
import com.petmatch.pais_service.Repository.PaisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DefaultCountryInitializer {

    private static final Logger log = LoggerFactory.getLogger(DefaultCountryInitializer.class);

    @Bean
    public CommandLineRunner seedDefaultCountry(
            JdbcTemplate jdbcTemplate,
            PaisRepository paisRepository,
            PaisEventPublisher paisEventPublisher
    ) {
        return args -> {
            jdbcTemplate.update(
                    """
                            insert into pais (id_pais, nombre_pais)
                            values (?, ?)
                            on duplicate key update nombre_pais = values(nombre_pais)
                            """,
                    1,
                    "CHILE"
            );

            paisRepository.findById(1).ifPresent(savedCountry -> {
                try {
                    paisEventPublisher.publicarPaisCreado(savedCountry);
                } catch (RuntimeException exception) {
                    log.warn("No fue posible publicar el pais base {}.", savedCountry.getNombrePais(), exception);
                }
            });
        };
    }
}
