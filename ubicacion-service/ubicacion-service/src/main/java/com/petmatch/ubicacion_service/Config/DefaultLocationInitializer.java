package com.petmatch.ubicacion_service.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
public class DefaultLocationInitializer {

    private static final List<DefaultCityReference> DEFAULT_CITY_REFERENCES = List.of(
            new DefaultCityReference(1, "SANTIAGO", 1),
            new DefaultCityReference(2, "PROVIDENCIA", 1),
            new DefaultCityReference(3, "LAS CONDES", 1),
            new DefaultCityReference(4, "VALPARAISO", 2),
            new DefaultCityReference(5, "CONCEPCION", 3)
    );

    @Bean
    public CommandLineRunner seedDefaultLocationReferences(JdbcTemplate jdbcTemplate) {
        return args -> {
            for (DefaultCityReference city : DEFAULT_CITY_REFERENCES) {
                jdbcTemplate.update(
                        """
                                insert into ciudad_referencia (id_ciudad, nombre_ciudad, id_region, activo)
                                values (?, ?, ?, ?)
                                on duplicate key update
                                    nombre_ciudad = values(nombre_ciudad),
                                    id_region = values(id_region),
                                    activo = values(activo)
                                """,
                        city.idCiudad(),
                        city.nombreCiudad(),
                        city.idRegion(),
                        true
                );
            }

            jdbcTemplate.update(
                    """
                            insert into ubicacion (id_ubicacion, direccion, numero, referencia, codigo_postal, latitud, longitud, id_ciudad)
                            values (?, ?, ?, ?, ?, ?, ?, ?)
                            on duplicate key update
                                direccion = values(direccion),
                                numero = values(numero),
                                referencia = values(referencia),
                                codigo_postal = values(codigo_postal),
                                latitud = values(latitud),
                                longitud = values(longitud),
                                id_ciudad = values(id_ciudad)
                            """,
                    1,
                    "Plaza de Armas",
                    "S/N",
                    "Centro de Santiago",
                    "8320000",
                    -33.4372,
                    -70.6506,
                    1
            );
        };
    }

    private record DefaultCityReference(Integer idCiudad, String nombreCiudad, Integer idRegion) {
    }
}
