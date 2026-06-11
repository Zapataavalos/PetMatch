package com.petmatch.usuario_service.Config;

import com.petmatch.usuario_service.Event.UsuarioEventPublisher;
import com.petmatch.usuario_service.Repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DemoUserInitializer {

    private static final Logger log = LoggerFactory.getLogger(DemoUserInitializer.class);
    private static final String DEMO_PASSWORD = "Petmatch2026!";

    private static final List<DemoUser> DEMO_USERS = List.of(
            new DemoUser(1, "Admin PetMatch", "admin@petmatch.cl", 1),
            new DemoUser(2, "Usuario Demo", "demo@petmatch.cl", 3)
    );

    @Bean
    public CommandLineRunner seedDemoUsers(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            UsuarioRepository usuarioRepository,
            UsuarioEventPublisher usuarioEventPublisher
    ) {
        return args -> {
            for (DemoUser user : DEMO_USERS) {
                String encodedPassword = passwordEncoder.encode(DEMO_PASSWORD);

                jdbcTemplate.update(
                        """
                                insert into usuario (id_usuario, nombre, email, contrasena, fecha_registro, id_rol)
                                values (?, ?, ?, ?, ?, ?)
                                on duplicate key update
                                    nombre = values(nombre),
                                    email = values(email),
                                    contrasena = values(contrasena),
                                    id_rol = values(id_rol)
                                """,
                        user.idUsuario(),
                        user.nombre(),
                        user.email(),
                        encodedPassword,
                        LocalDateTime.of(2026, 1, 1, 9, 0),
                        user.idRol()
                );

                usuarioRepository.findByEmailIgnoreCase(user.email()).ifPresent(savedUser -> {
                    try {
                        usuarioEventPublisher.publicarUsuarioCreado(savedUser);
                    } catch (RuntimeException exception) {
                        log.warn("No fue posible publicar el usuario demo {}.", savedUser.getEmail(), exception);
                    }
                });
            }
        };
    }

    private record DemoUser(Integer idUsuario, String nombre, String email, Integer idRol) {
    }
}
