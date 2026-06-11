package com.petmatch.region_service.Config;

import com.petmatch.region_service.Event.RegionEventPublisher;
import com.petmatch.region_service.Repository.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
public class DefaultRegionInitializer {

    private static final Logger log = LoggerFactory.getLogger(DefaultRegionInitializer.class);

    private static final List<DefaultRegion> DEFAULT_REGIONS = List.of(
            new DefaultRegion(1, "METROPOLITANA", 1),
            new DefaultRegion(2, "VALPARAISO", 1),
            new DefaultRegion(3, "BIOBIO", 1)
    );

    @Bean
    public CommandLineRunner seedDefaultRegions(
            JdbcTemplate jdbcTemplate,
            RegionRepository regionRepository,
            RegionEventPublisher regionEventPublisher
    ) {
        return args -> {
            jdbcTemplate.update(
                    """
                            insert into pais_referencia (id_pais, nombre_pais, activo)
                            values (?, ?, ?)
                            on duplicate key update
                                nombre_pais = values(nombre_pais),
                                activo = values(activo)
                            """,
                    1,
                    "CHILE",
                    true
            );

            for (DefaultRegion region : DEFAULT_REGIONS) {
                jdbcTemplate.update(
                        """
                                insert into region (id_region, nombre_region, id_pais)
                                values (?, ?, ?)
                                on duplicate key update
                                    nombre_region = values(nombre_region),
                                    id_pais = values(id_pais)
                                """,
                        region.idRegion(),
                        region.nombreRegion(),
                        region.idPais()
                );

                regionRepository.findById(region.idRegion()).ifPresent(savedRegion -> {
                    try {
                        regionEventPublisher.publicarRegionCreada(savedRegion);
                    } catch (RuntimeException exception) {
                        log.warn("No fue posible publicar la region base {}.", savedRegion.getNombreRegion(), exception);
                    }
                });
            }
        };
    }

    private record DefaultRegion(Integer idRegion, String nombreRegion, Integer idPais) {
    }
}
