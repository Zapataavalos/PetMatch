package com.petmatch.configuracion_usuario_service.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
public class DefaultUserConfigurationInitializer {

    private static final List<DefaultUserReference> DEFAULT_USER_REFERENCES = List.of(
            new DefaultUserReference(1, "Admin PetMatch", "admin@petmatch.cl", 1),
            new DefaultUserReference(2, "Usuario Demo", "demo@petmatch.cl", 3)
    );

    private static final List<DefaultColorReference> DEFAULT_COLOR_REFERENCES = List.of(
            new DefaultColorReference(1, "AMARILLO PETMATCH", "#F5C400"),
            new DefaultColorReference(2, "VERDE RESCATE", "#10B981"),
            new DefaultColorReference(3, "AZUL COMUNIDAD", "#60A5FA")
    );

    @Bean
    public CommandLineRunner seedDefaultUserConfigurations(JdbcTemplate jdbcTemplate) {
        return args -> {
            for (DefaultUserReference user : DEFAULT_USER_REFERENCES) {
                jdbcTemplate.update(
                        """
                                insert into usuario_referencia (id_usuario, nombre, email, id_rol, activo)
                                values (?, ?, ?, ?, ?)
                                on duplicate key update
                                    nombre = values(nombre),
                                    email = values(email),
                                    id_rol = values(id_rol),
                                    activo = values(activo)
                                """,
                        user.idUsuario(),
                        user.nombre(),
                        user.email(),
                        user.idRol(),
                        true
                );
            }

            for (DefaultColorReference color : DEFAULT_COLOR_REFERENCES) {
                jdbcTemplate.update(
                        """
                                insert into color_referencia (id_color, nombre_color, codigo_hexadecimal, activo)
                                values (?, ?, ?, ?)
                                on duplicate key update
                                    nombre_color = values(nombre_color),
                                    codigo_hexadecimal = values(codigo_hexadecimal),
                                    activo = values(activo)
                                """,
                        color.idColor(),
                        color.nombreColor(),
                        color.codigoHexadecimal(),
                        true
                );
            }

            seedConfiguration(jdbcTemplate, 1, 1, 1, true, true, "ES");
            seedConfiguration(jdbcTemplate, 2, 2, 1, true, true, "ES");
        };
    }

    private void seedConfiguration(
            JdbcTemplate jdbcTemplate,
            Integer idConfiguracionUsuario,
            Integer idUsuario,
            Integer idColor,
            Boolean notificacionesActivas,
            Boolean modoOscuro,
            String idioma
    ) {
        jdbcTemplate.update(
                """
                        insert into configuracion_usuario (
                            id_configuracion_usuario,
                            id_usuario,
                            id_color,
                            notificaciones_activas,
                            modo_oscuro,
                            idioma
                        )
                        values (?, ?, ?, ?, ?, ?)
                        on duplicate key update
                            id_color = values(id_color),
                            notificaciones_activas = values(notificaciones_activas),
                            modo_oscuro = values(modo_oscuro),
                            idioma = values(idioma)
                        """,
                idConfiguracionUsuario,
                idUsuario,
                idColor,
                notificacionesActivas,
                modoOscuro,
                idioma
        );
    }

    private record DefaultUserReference(Integer idUsuario, String nombre, String email, Integer idRol) {
    }

    private record DefaultColorReference(Integer idColor, String nombreColor, String codigoHexadecimal) {
    }
}
