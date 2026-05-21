package com.petmatch.usuario_service.Service;

import com.petmatch.usuario_service.DTO.UsuarioRequestDTO;
import com.petmatch.usuario_service.DTO.UsuarioResponseDTO;
import com.petmatch.usuario_service.Event.UsuarioEventPublisher;
import com.petmatch.usuario_service.Exception.BadRequestException;
import com.petmatch.usuario_service.Exception.ResourceNotFoundException;
import com.petmatch.usuario_service.Model.Usuario;
import com.petmatch.usuario_service.Repository.RolReferenciaRepository;
import com.petmatch.usuario_service.Repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolReferenciaRepository rolReferenciaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioEventPublisher usuarioEventPublisher;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Debe listar todos los usuarios correctamente")
    void listarUsuarios_debeRetornarListaDeUsuarios() {
        Usuario usuario1 = new Usuario(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                "HASH",
                LocalDateTime.now(),
                1
        );

        Usuario usuario2 = new Usuario(
                2,
                "JUAN PEREZ",
                "juan@test.cl",
                "HASH",
                LocalDateTime.now(),
                1
        );

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario1, usuario2));

        List<UsuarioResponseDTO> resultado = usuarioService.listarUsuarios();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).idUsuario()).isEqualTo(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("BENJAMIN MENDEZ");
        assertThat(resultado.get(0).email()).isEqualTo("benjamin@test.cl");
        assertThat(resultado.get(0).idRol()).isEqualTo(1);

        verify(usuarioRepository, times(1)).findAll();
        verifyNoInteractions(usuarioEventPublisher);
    }

    @Test
    @DisplayName("Debe buscar usuario por ID correctamente")
    void buscarUsuarioPorId_cuandoExiste_debeRetornarUsuario() {
        Usuario usuario = new Usuario(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                "HASH",
                LocalDateTime.now(),
                1
        );

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO resultado = usuarioService.buscarUsuarioPorId(1);

        assertThat(resultado.idUsuario()).isEqualTo(1);
        assertThat(resultado.nombre()).isEqualTo("BENJAMIN MENDEZ");
        assertThat(resultado.email()).isEqualTo("benjamin@test.cl");
        assertThat(resultado.idRol()).isEqualTo(1);

        verify(usuarioRepository, times(1)).findById(1);
        verifyNoInteractions(usuarioEventPublisher);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void buscarUsuarioPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarUsuarioPorId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el usuario con ID: 99");

        verify(usuarioRepository, times(1)).findById(99);
        verifyNoInteractions(usuarioEventPublisher);
    }

    @Test
    @DisplayName("Debe listar usuarios por rol si el rol existe y está activo")
    void listarUsuariosPorRol_cuandoRolExiste_debeRetornarUsuarios() {
        Integer idRol = 1;

        Usuario usuario = new Usuario(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                "HASH",
                LocalDateTime.now(),
                idRol
        );

        when(rolReferenciaRepository.existsByIdRolAndActivoTrue(idRol)).thenReturn(true);
        when(usuarioRepository.findByIdRol(idRol)).thenReturn(List.of(usuario));

        List<UsuarioResponseDTO> resultado = usuarioService.listarUsuariosPorRol(idRol);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).idRol()).isEqualTo(idRol);

        verify(rolReferenciaRepository, times(1)).existsByIdRolAndActivoTrue(idRol);
        verify(usuarioRepository, times(1)).findByIdRol(idRol);
        verifyNoInteractions(usuarioEventPublisher);
    }

    @Test
    @DisplayName("No debe listar usuarios por rol si el rol no existe o está inactivo")
    void listarUsuariosPorRol_cuandoRolNoExiste_debeLanzarExcepcion() {
        Integer idRol = 99;

        when(rolReferenciaRepository.existsByIdRolAndActivoTrue(idRol)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.listarUsuariosPorRol(idRol))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No existe un rol activo registrado con ID: 99");

        verify(rolReferenciaRepository, times(1)).existsByIdRolAndActivoTrue(idRol);
        verify(usuarioRepository, never()).findByIdRol(any());
        verifyNoInteractions(usuarioEventPublisher);
    }

    @Test
    @DisplayName("Debe crear un usuario correctamente y publicar evento")
    void crearUsuario_cuandoDatosValidos_debeCrearUsuarioYPublicarEvento() {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "Benjamin Mendez",
                "BENJAMIN@TEST.CL",
                "12345678",
                1
        );

        when(rolReferenciaRepository.existsByIdRolAndActivoTrue(1)).thenReturn(true);
        when(usuarioRepository.existsByEmailIgnoreCase("benjamin@test.cl")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("HASH");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setIdUsuario(1);
            return usuario;
        });

        UsuarioResponseDTO resultado = usuarioService.crearUsuario(requestDTO);

        assertThat(resultado.idUsuario()).isEqualTo(1);
        assertThat(resultado.nombre()).isEqualTo("BENJAMIN MENDEZ");
        assertThat(resultado.email()).isEqualTo("benjamin@test.cl");
        assertThat(resultado.idRol()).isEqualTo(1);

        verify(rolReferenciaRepository, times(1)).existsByIdRolAndActivoTrue(1);
        verify(usuarioRepository, times(1)).existsByEmailIgnoreCase("benjamin@test.cl");
        verify(passwordEncoder, times(1)).encode("12345678");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(usuarioEventPublisher, times(1)).publicarUsuarioCreado(any(Usuario.class));
    }

    @Test
    @DisplayName("No debe crear usuario si el rol no existe")
    void crearUsuario_cuandoRolNoExiste_debeLanzarResourceNotFound() {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "Benjamin Mendez",
                "benjamin@test.cl",
                "12345678",
                99
        );

        when(rolReferenciaRepository.existsByIdRolAndActivoTrue(99)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.crearUsuario(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No existe un rol activo registrado con ID: 99");

        verify(rolReferenciaRepository, times(1)).existsByIdRolAndActivoTrue(99);
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verifyNoInteractions(usuarioEventPublisher);
    }

    @Test
    @DisplayName("No debe crear usuario si el email ya existe")
    void crearUsuario_cuandoEmailExiste_debeLanzarBadRequest() {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "Benjamin Mendez",
                "benjamin@test.cl",
                "12345678",
                1
        );

        when(rolReferenciaRepository.existsByIdRolAndActivoTrue(1)).thenReturn(true);
        when(usuarioRepository.existsByEmailIgnoreCase("benjamin@test.cl")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuario(requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe un usuario registrado con el email: benjamin@test.cl");

        verify(rolReferenciaRepository, times(1)).existsByIdRolAndActivoTrue(1);
        verify(usuarioRepository, times(1)).existsByEmailIgnoreCase("benjamin@test.cl");
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verifyNoInteractions(usuarioEventPublisher);
    }

    @Test
    @DisplayName("Debe actualizar un usuario correctamente y publicar evento")
    void actualizarUsuario_cuandoDatosValidos_debeActualizarUsuarioYPublicarEvento() {
        Usuario usuarioExistente = new Usuario(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                "HASH",
                LocalDateTime.now(),
                1
        );

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "Benjamin Actualizado",
                "nuevo@test.cl",
                "12345678",
                1
        );

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioExistente));
        when(rolReferenciaRepository.existsByIdRolAndActivoTrue(1)).thenReturn(true);
        when(usuarioRepository.existsByEmailIgnoreCaseAndIdUsuarioNot("nuevo@test.cl", 1)).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("HASH_NUEVO");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO resultado = usuarioService.actualizarUsuario(1, requestDTO);

        assertThat(resultado.idUsuario()).isEqualTo(1);
        assertThat(resultado.nombre()).isEqualTo("BENJAMIN ACTUALIZADO");
        assertThat(resultado.email()).isEqualTo("nuevo@test.cl");
        assertThat(resultado.idRol()).isEqualTo(1);

        verify(usuarioRepository, times(1)).findById(1);
        verify(rolReferenciaRepository, times(1)).existsByIdRolAndActivoTrue(1);
        verify(usuarioRepository, times(1)).existsByEmailIgnoreCaseAndIdUsuarioNot("nuevo@test.cl", 1);
        verify(passwordEncoder, times(1)).encode("12345678");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(usuarioEventPublisher, times(1)).publicarUsuarioActualizado(any(Usuario.class));
    }

    @Test
    @DisplayName("No debe actualizar usuario si el email pertenece a otro usuario")
    void actualizarUsuario_cuandoEmailDuplicado_debeLanzarBadRequest() {
        Usuario usuarioExistente = new Usuario(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                "HASH",
                LocalDateTime.now(),
                1
        );

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "Benjamin Mendez",
                "otro@test.cl",
                "12345678",
                1
        );

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioExistente));
        when(rolReferenciaRepository.existsByIdRolAndActivoTrue(1)).thenReturn(true);
        when(usuarioRepository.existsByEmailIgnoreCaseAndIdUsuarioNot("otro@test.cl", 1)).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.actualizarUsuario(1, requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe otro usuario registrado con el email: otro@test.cl");

        verify(usuarioRepository, times(1)).findById(1);
        verify(rolReferenciaRepository, times(1)).existsByIdRolAndActivoTrue(1);
        verify(usuarioRepository, times(1)).existsByEmailIgnoreCaseAndIdUsuarioNot("otro@test.cl", 1);
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verifyNoInteractions(usuarioEventPublisher);
    }

    @Test
    @DisplayName("Debe eliminar un usuario correctamente y publicar evento")
    void eliminarUsuario_cuandoExiste_debeEliminarUsuarioYPublicarEvento() {
        Usuario usuario = new Usuario(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                "HASH",
                LocalDateTime.now(),
                1
        );

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        usuarioService.eliminarUsuario(1);

        verify(usuarioRepository, times(1)).findById(1);
        verify(usuarioRepository, times(1)).delete(usuario);
        verify(usuarioEventPublisher, times(1)).publicarUsuarioEliminado(usuario);
    }

    @Test
    @DisplayName("No debe eliminar usuario inexistente")
    void eliminarUsuario_cuandoNoExiste_debeLanzarResourceNotFound() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.eliminarUsuario(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el usuario con ID: 99");

        verify(usuarioRepository, times(1)).findById(99);
        verify(usuarioRepository, never()).delete(any(Usuario.class));
        verifyNoInteractions(usuarioEventPublisher);
    }
}