package com.petmatch.color_service.Config;

import com.petmatch.color_service.Event.ColorEventPublisher;
import com.petmatch.color_service.Repository.ColorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
public class DefaultColorInitializer {

    private static final Logger log = LoggerFactory.getLogger(DefaultColorInitializer.class);

    private static final List<DefaultColor> DEFAULT_COLORS = List.of(
            new DefaultColor(1, "AMARILLO PETMATCH", "#F5C400"),
            new DefaultColor(2, "VERDE RESCATE", "#10B981"),
            new DefaultColor(3, "AZUL COMUNIDAD", "#60A5FA")
    );

    @Bean
    public CommandLineRunner seedDefaultColors(
            JdbcTemplate jdbcTemplate,
            ColorRepository colorRepository,
            ColorEventPublisher colorEventPublisher
    ) {
        return args -> {
            for (DefaultColor color : DEFAULT_COLORS) {
                jdbcTemplate.update(
                        """
                        insert into color (id_color, nombre_color, codigo_hexadecimal)
                        values (?, ?, ?)
                        on duplicate key update nombre_color = values(nombre_color),
                                                codigo_hexadecimal = values(codigo_hexadecimal)
                        """,
                        color.idColor(),
                        color.nombreColor(),
                        color.codigoHexadecimal()
                );

                colorRepository.findById(color.idColor()).ifPresent(savedColor -> {
                    try {
                        colorEventPublisher.publicarColorCreado(savedColor);
                    } catch (RuntimeException exception) {
                        log.warn(
                                "No fue posible publicar el color base {}. configuracion-usuario-service inicializa sus referencias locales.",
                                savedColor.getNombreColor(),
                                exception
                        );
                    }
                });
            }
        };
    }

    private record DefaultColor(Integer idColor, String nombreColor, String codigoHexadecimal) {
    }
}
