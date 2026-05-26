package com.petmatch.rol_service.Config;

import com.petmatch.rol_service.Event.RolEventPublisher;
import com.petmatch.rol_service.Repository.RolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
public class DefaultRoleInitializer {

    private static final Logger log = LoggerFactory.getLogger(DefaultRoleInitializer.class);

    private static final List<DefaultRole> DEFAULT_ROLES = List.of(
            new DefaultRole(1, "ADMINISTRADOR"),
            new DefaultRole(2, "DUENO"),
            new DefaultRole(3, "CIUDADANO")
    );

    @Bean
    public CommandLineRunner seedDefaultRoles(
            JdbcTemplate jdbcTemplate,
            RolRepository rolRepository,
            RolEventPublisher rolEventPublisher
    ) {
        return args -> {
            for (DefaultRole role : DEFAULT_ROLES) {
                jdbcTemplate.update(
                        "insert into rol (id_rol, nombre_rol) values (?, ?) on duplicate key update nombre_rol = ?",
                        role.idRol(),
                        role.nombreRol(),
                        role.nombreRol()
                );

                rolRepository.findById(role.idRol()).ifPresent(savedRole -> {
                    try {
                        rolEventPublisher.publicarRolCreado(savedRole);
                    } catch (RuntimeException exception) {
                        log.warn(
                                "No fue posible publicar el rol base {}. usuario-service inicializa sus referencias locales.",
                                savedRole.getNombreRol(),
                                exception
                        );
                    }
                });
            }
        };
    }

    private record DefaultRole(Integer idRol, String nombreRol) {
    }
}
