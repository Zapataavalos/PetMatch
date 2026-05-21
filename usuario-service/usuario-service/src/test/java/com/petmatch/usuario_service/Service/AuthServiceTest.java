package com.petmatch.usuario_service.Service;

import com.petmatch.usuario_service.DTO.AuthResponseDTO;
import com.petmatch.usuario_service.DTO.LoginRequestDTO;
import com.petmatch.usuario_service.DTO.UsuarioRequestDTO;
import com.petmatch.usuario_service.DTO.UsuarioResponseDTO;
import com.petmatch.usuario_service.Exception.ResourceNotFoundException;
import com.petmatch.usuario_service.Exception.UnauthorizedException;
import com.petmatch.usuario_service.Model.RolReferencia;
import com.petmatch.usuario_service.Model.Usuario;
import com.petmatch.usuario_service.Repository.RolReferenciaRepository;
import com.petmatch.usuario_service.Repository.UsuarioRepository;
import com.petmatch.usuario_service.Security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolReferenciaRepository rolReferenciaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Debe registrar un usuario correctamente")
    void registrar_debeDelegarEnUsuarioService() {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "Benjamin Mendez",
                "benjamin@test.cl",
                "12345678",
                1
        );

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                LocalDateTime.now(),
                1
        );

        when(usuarioService.crearUsuario(requestDTO)).thenReturn(responseDTO);

        UsuarioResponseDTO resultado = authService.registrar(requestDTO);

        assertThat(resultado.idUsuario()).isEqualTo(1);
        assertThat(resultado.email()).isEqualTo("benjamin@test.cl");

        verify(usuarioService, times(1)).crearUsuario(requestDTO);
    }

    @Test
    @DisplayName("Debe iniciar sesión correctamente y retornar JWT")
    void login_conCredencialesValidas_debeRetornarToken() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("benjamin@test.cl", "12345678");

        Usuario usuario = new Usuario(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                "HASH",
                LocalDateTime.now(),
                1
        );

        RolReferencia rol = new RolReferencia(1, "ADMINISTRADOR", true);

        when(usuarioRepository.findByEmailIgnoreCase("benjamin@test.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("12345678", "HASH")).thenReturn(true);
        when(rolReferenciaRepository.findByIdRolAndActivoTrue(1)).thenReturn(Optional.of(rol));
        when(jwtService.generarToken(usuario, "ADMINISTRADOR")).thenReturn("TOKEN_JWT");

        AuthResponseDTO resultado = authService.login(requestDTO);

        assertThat(resultado.token()).isEqualTo("TOKEN_JWT");
        assertThat(resultado.tipo()).isEqualTo("Bearer");
        assertThat(resultado.idUsuario()).isEqualTo(1);
        assertThat(resultado.email()).isEqualTo("benjamin@test.cl");
        assertThat(resultado.idRol()).isEqualTo(1);

        verify(usuarioRepository, times(1)).findByEmailIgnoreCase("benjamin@test.cl");
        verify(passwordEncoder, times(1)).matches("12345678", "HASH");
        verify(jwtService, times(1)).generarToken(usuario, "ADMINISTRADOR");
    }

    @Test
    @DisplayName("No debe iniciar sesión si el usuario no existe")
    void login_cuandoUsuarioNoExiste_debeLanzarUnauthorized() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("noexiste@test.cl", "12345678");

        when(usuarioRepository.findByEmailIgnoreCase("noexiste@test.cl")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(requestDTO))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generarToken(any(), any());
    }

    @Test
    @DisplayName("No debe iniciar sesión si la contraseña es incorrecta")
    void login_cuandoPasswordIncorrecta_debeLanzarUnauthorized() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("benjamin@test.cl", "incorrecta");

        Usuario usuario = new Usuario(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                "HASH",
                LocalDateTime.now(),
                1
        );

        when(usuarioRepository.findByEmailIgnoreCase("benjamin@test.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("incorrecta", "HASH")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(requestDTO))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(jwtService, never()).generarToken(any(), any());
    }

    @Test
    @DisplayName("No debe iniciar sesión si el rol está inactivo")
    void login_cuandoRolInactivo_debeLanzarResourceNotFound() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("benjamin@test.cl", "12345678");

        Usuario usuario = new Usuario(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                "HASH",
                LocalDateTime.now(),
                1
        );

        when(usuarioRepository.findByEmailIgnoreCase("benjamin@test.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("12345678", "HASH")).thenReturn(true);
        when(rolReferenciaRepository.findByIdRolAndActivoTrue(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("El rol del usuario no se encuentra activo");

        verify(jwtService, never()).generarToken(any(), any());
    }
}