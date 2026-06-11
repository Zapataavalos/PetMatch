package com.petmatch.ciudad_service.Config;

import com.petmatch.ciudad_service.Event.CiudadEventPublisher;
import com.petmatch.ciudad_service.Repository.CiudadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
public class DefaultCityInitializer {

    private static final Logger log = LoggerFactory.getLogger(DefaultCityInitializer.class);

    private static final List<DefaultRegionReference> DEFAULT_REGION_REFERENCES = List.of(
            new DefaultRegionReference(1, "METROPOLITANA", 1),
            new DefaultRegionReference(2, "VALPARAISO", 1),
            new DefaultRegionReference(3, "BIOBIO", 1)
    );

    private static final List<DefaultCity> DEFAULT_CITIES = List.of(
            new DefaultCity(1, "SANTIAGO", 1),
            new DefaultCity(2, "PROVIDENCIA", 1),
            new DefaultCity(3, "LAS CONDES", 1),
            new DefaultCity(4, "VALPARAISO", 2),
            new DefaultCity(5, "CONCEPCION", 3)
    );

    @Bean
    public CommandLineRunner seedDefaultCities(
            JdbcTemplate jdbcTemplate,
            CiudadRepository ciudadRepository,
            CiudadEventPublisher ciudadEventPublisher
    ) {
        return args -> {
            for (DefaultRegionReference region : DEFAULT_REGION_REFERENCES) {
                jdbcTemplate.update(
                        """
                                insert into region_referencia (id_region, nombre_region, id_pais, activo)
                                values (?, ?, ?, ?)
                                on duplicate key update
                                    nombre_region = values(nombre_region),
                                    id_pais = values(id_pais),
                                    activo = values(activo)
                                """,
                        region.idRegion(),
                        region.nombreRegion(),
                        region.idPais(),
                        true
                );
            }

            for (DefaultCity city : DEFAULT_CITIES) {
                jdbcTemplate.update(
                        """
                                insert into ciudad (id_ciudad, nombre_ciudad, id_region)
                                values (?, ?, ?)
                                on duplicate key update
                                    nombre_ciudad = values(nombre_ciudad),
                                    id_region = values(id_region)
                                """,
                        city.idCiudad(),
                        city.nombreCiudad(),
                        city.idRegion()
                );

                ciudadRepository.findById(city.idCiudad()).ifPresent(savedCity -> {
                    try {
                        ciudadEventPublisher.publicarCiudadCreada(savedCity);
                    } catch (RuntimeException exception) {
                        log.warn("No fue posible publicar la ciudad base {}.", savedCity.getNombreCiudad(), exception);
                    }
                });
            }
        };
    }

    private record DefaultRegionReference(Integer idRegion, String nombreRegion, Integer idPais) {
    }

    private record DefaultCity(Integer idCiudad, String nombreCiudad, Integer idRegion) {
    }
}
