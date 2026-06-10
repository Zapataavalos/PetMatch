package com.petmatch.usuario_service.Config;

import com.petmatch.usuario_service.Model.RolReferencia;
import com.petmatch.usuario_service.Model.Usuario;
import com.petmatch.usuario_service.Repository.RolReferenciaRepository;
import com.petmatch.usuario_service.Repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "petmatch.seed.admin", name = "enabled", havingValue = "true")
public class AdminUserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);
    private static final String ADMIN_ROLE_NAME = "ADMIN";
    private static final String LEGACY_ADMIN_ROLE_NAME = "ADMINISTRADOR";

    private final UsuarioRepository usuarioRepository;
    private final RolReferenciaRepository rolReferenciaRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminName;

    public AdminUserInitializer(
            UsuarioRepository usuarioRepository,
            RolReferenciaRepository rolReferenciaRepository,
            PasswordEncoder passwordEncoder,
            @Value("${petmatch.seed.admin.email}") String adminEmail,
            @Value("${petmatch.seed.admin.password}") String adminPassword,
            @Value("${petmatch.seed.admin.name}") String adminName
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolReferenciaRepository = rolReferenciaRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminName = adminName;
    }

    @Override
    @Transactional
    public void run(String... args) {
        String emailNormalizado = normalizarEmail(adminEmail);

        if (usuarioRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            log.info("El usuario administrador ya existe; no se realizaron cambios");
            return;
        }

        RolReferencia rolAdministrador = obtenerRolAdministradorActivo();

        Usuario usuario = new Usuario();
        usuario.setNombre(normalizarNombre(adminName));
        usuario.setEmail(emailNormalizado);
        usuario.setContrasena(passwordEncoder.encode(adminPassword));
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setIdRol(rolAdministrador.getIdRol());

        usuarioRepository.save(usuario);

        log.info("Usuario administrador de desarrollo creado correctamente");
    }

    private RolReferencia obtenerRolAdministradorActivo() {
        List<RolReferencia> roles = rolReferenciaRepository.findAll();

        return buscarRolActivoPorNombre(roles, ADMIN_ROLE_NAME)
                .or(() -> buscarRolActivoPorNombre(roles, LEGACY_ADMIN_ROLE_NAME))
                .orElseThrow(() -> new IllegalStateException(
                        "No existe un rol administrador activo en rol_referencia"
                ));
    }

    private Optional<RolReferencia> buscarRolActivoPorNombre(List<RolReferencia> roles, String nombreRol) {
        String nombreNormalizado = normalizarRol(nombreRol);

        return roles.stream()
                .filter(rol -> Boolean.TRUE.equals(rol.getActivo()))
                .filter(rol -> normalizarRol(rol.getNombreRol()).equals(nombreNormalizado))
                .findFirst();
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email del administrador de desarrollo es obligatorio");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del administrador de desarrollo es obligatorio");
        }

        return nombre.trim();
    }

    private String normalizarRol(String nombreRol) {
        if (nombreRol == null || nombreRol.isBlank()) {
            return "";
        }

        return Normalizer
                .normalize(nombreRol.trim().toUpperCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
