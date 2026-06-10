package com.petmatch.usuario_service.Config;

import com.petmatch.usuario_service.Model.RolReferencia;
import com.petmatch.usuario_service.Model.Usuario;
import com.petmatch.usuario_service.Repository.RolReferenciaRepository;
import com.petmatch.usuario_service.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserInitializerTest {

    private static final String ADMIN_EMAIL = "admin@test.cl";
    private static final String ADMIN_PASSWORD = "admin123.";
    private static final String ADMIN_NAME = "Administrador PetMatch";
    private static final String ENCODED_PASSWORD = "$2a$10$encoded-password";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolReferenciaRepository rolReferenciaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminUserInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new AdminUserInitializer(
                usuarioRepository,
                rolReferenciaRepository,
                passwordEncoder,
                ADMIN_EMAIL,
                ADMIN_PASSWORD,
                ADMIN_NAME
        );
    }

    @Test
    @DisplayName("Crea el administrador cuando no existe")
    void run_cuandoAdministradorNoExiste_debeCrearlo() {
        RolReferencia rolAdmin = new RolReferencia(5, "ADMIN", true);

        when(usuarioRepository.existsByEmailIgnoreCase(ADMIN_EMAIL)).thenReturn(false);
        when(rolReferenciaRepository.findAll()).thenReturn(List.of(rolAdmin));
        when(passwordEncoder.encode(ADMIN_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        initializer.run();

        Usuario usuarioGuardado = capturarUsuarioGuardado();

        assertThat(usuarioGuardado.getNombre()).isEqualTo(ADMIN_NAME);
        assertThat(usuarioGuardado.getEmail()).isEqualTo(ADMIN_EMAIL);
        assertThat(usuarioGuardado.getFechaRegistro()).isNotNull();
        assertThat(usuarioGuardado.getIdRol()).isEqualTo(5);
    }

    @Test
    @DisplayName("No duplica el administrador cuando ya existe")
    void run_cuandoAdministradorExiste_noDebeDuplicarlo() {
        when(usuarioRepository.existsByEmailIgnoreCase(ADMIN_EMAIL)).thenReturn(true);

        initializer.run();

        verify(rolReferenciaRepository, never()).findAll();
        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Guarda la contrasena codificada")
    void run_debeGuardarContrasenaCodificada() {
        RolReferencia rolAdmin = new RolReferencia(5, "ADMIN", true);

        when(usuarioRepository.existsByEmailIgnoreCase(ADMIN_EMAIL)).thenReturn(false);
        when(rolReferenciaRepository.findAll()).thenReturn(List.of(rolAdmin));
        when(passwordEncoder.encode(ADMIN_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        initializer.run();

        Usuario usuarioGuardado = capturarUsuarioGuardado();

        assertThat(usuarioGuardado.getContrasena()).isEqualTo(ENCODED_PASSWORD);
        assertThat(usuarioGuardado.getContrasena()).isNotEqualTo(ADMIN_PASSWORD);
        verify(passwordEncoder).encode(ADMIN_PASSWORD);
    }

    @Test
    @DisplayName("Asigna el rol ADMIN activo encontrado por nombre")
    void run_debeAsignarRolAdminActivoPorNombre() {
        RolReferencia rolDueno = new RolReferencia(2, "DUENO", true);
        RolReferencia rolAdmin = new RolReferencia(5, "ADMIN", true);

        when(usuarioRepository.existsByEmailIgnoreCase(ADMIN_EMAIL)).thenReturn(false);
        when(rolReferenciaRepository.findAll()).thenReturn(List.of(rolDueno, rolAdmin));
        when(passwordEncoder.encode(ADMIN_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        initializer.run();

        Usuario usuarioGuardado = capturarUsuarioGuardado();

        assertThat(usuarioGuardado.getIdRol()).isEqualTo(5);
    }

    @Test
    @DisplayName("Usa ADMINISTRADOR como rol administrador compatible del proyecto")
    void run_cuandoNoExisteAdminExacto_debeUsarAdministradorActivo() {
        RolReferencia rolAdministrador = new RolReferencia(1, "ADMINISTRADOR", true);

        when(usuarioRepository.existsByEmailIgnoreCase(ADMIN_EMAIL)).thenReturn(false);
        when(rolReferenciaRepository.findAll()).thenReturn(List.of(rolAdministrador));
        when(passwordEncoder.encode(ADMIN_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        initializer.run();

        Usuario usuarioGuardado = capturarUsuarioGuardado();

        assertThat(usuarioGuardado.getIdRol()).isEqualTo(1);
    }

    @Test
    @DisplayName("Maneja correctamente la ausencia del rol ADMIN")
    void run_cuandoNoExisteRolAdministrador_noDebeCrearUsuario() {
        RolReferencia rolDueno = new RolReferencia(2, "DUENO", true);
        RolReferencia rolAdminInactivo = new RolReferencia(5, "ADMIN", false);

        when(usuarioRepository.existsByEmailIgnoreCase(ADMIN_EMAIL)).thenReturn(false);
        when(rolReferenciaRepository.findAll()).thenReturn(List.of(rolDueno, rolAdminInactivo));

        assertThatThrownBy(() -> initializer.run())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No existe un rol administrador activo");

        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any());
    }

    private Usuario capturarUsuarioGuardado() {
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        return usuarioCaptor.getValue();
    }
}
