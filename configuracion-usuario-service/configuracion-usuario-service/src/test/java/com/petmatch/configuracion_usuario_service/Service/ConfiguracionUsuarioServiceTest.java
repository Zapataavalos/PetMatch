package com.petmatch.configuracion_usuario_service.Service;

import com.petmatch.configuracion_usuario_service.DTO.ConfiguracionUsuarioRequestDTO;
import com.petmatch.configuracion_usuario_service.DTO.ConfiguracionUsuarioResponseDTO;
import com.petmatch.configuracion_usuario_service.Exception.BadRequestException;
import com.petmatch.configuracion_usuario_service.Exception.ResourceNotFoundException;
import com.petmatch.configuracion_usuario_service.Model.ConfiguracionUsuario;
import com.petmatch.configuracion_usuario_service.Repository.ColorReferenciaRepository;
import com.petmatch.configuracion_usuario_service.Repository.ConfiguracionUsuarioRepository;
import com.petmatch.configuracion_usuario_service.Repository.UsuarioReferenciaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfiguracionUsuarioServiceTest {

    @Mock
    private ConfiguracionUsuarioRepository configuracionUsuarioRepository;

    @Mock
    private UsuarioReferenciaRepository usuarioReferenciaRepository;

    @Mock
    private ColorReferenciaRepository colorReferenciaRepository;

    @InjectMocks
    private ConfiguracionUsuarioService configuracionUsuarioService;

    @Test
    @DisplayName("Debe listar todas las configuraciones correctamente")
    void listarConfiguraciones_debeRetornarLista() {
        ConfiguracionUsuario config1 = new ConfiguracionUsuario(1, 1, 1, true, false, "ES");
        ConfiguracionUsuario config2 = new ConfiguracionUsuario(2, 2, 1, false, true, "EN");

        when(configuracionUsuarioRepository.findAll()).thenReturn(List.of(config1, config2));

        List<ConfiguracionUsuarioResponseDTO> resultado = configuracionUsuarioService.listarConfiguraciones();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).idUsuario()).isEqualTo(1);
        assertThat(resultado.get(0).idColor()).isEqualTo(1);
        assertThat(resultado.get(0).idioma()).isEqualTo("ES");

        verify(configuracionUsuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar configuración por ID correctamente")
    void buscarConfiguracionPorId_cuandoExiste_debeRetornarConfiguracion() {
        ConfiguracionUsuario config = new ConfiguracionUsuario(1, 1, 1, true, false, "ES");

        when(configuracionUsuarioRepository.findById(1)).thenReturn(Optional.of(config));

        ConfiguracionUsuarioResponseDTO resultado = configuracionUsuarioService.buscarConfiguracionPorId(1);

        assertThat(resultado.idConfiguracionUsuario()).isEqualTo(1);
        assertThat(resultado.idUsuario()).isEqualTo(1);
        assertThat(resultado.idColor()).isEqualTo(1);
        assertThat(resultado.notificacionesActivas()).isTrue();
        assertThat(resultado.modoOscuro()).isFalse();
        assertThat(resultado.idioma()).isEqualTo("ES");

        verify(configuracionUsuarioRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la configuración no existe")
    void buscarConfiguracionPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(configuracionUsuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> configuracionUsuarioService.buscarConfiguracionPorId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró la configuración con ID: 99");

        verify(configuracionUsuarioRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe buscar configuración por usuario correctamente")
    void buscarConfiguracionPorUsuario_cuandoExiste_debeRetornarConfiguracion() {
        ConfiguracionUsuario config = new ConfiguracionUsuario(1, 10, 2, true, true, "ES");

        when(configuracionUsuarioRepository.findByIdUsuario(10)).thenReturn(Optional.of(config));

        ConfiguracionUsuarioResponseDTO resultado = configuracionUsuarioService.buscarConfiguracionPorUsuario(10);

        assertThat(resultado.idConfiguracionUsuario()).isEqualTo(1);
        assertThat(resultado.idUsuario()).isEqualTo(10);
        assertThat(resultado.idColor()).isEqualTo(2);
        assertThat(resultado.modoOscuro()).isTrue();

        verify(configuracionUsuarioRepository, times(1)).findByIdUsuario(10);
    }

    @Test
    @DisplayName("Debe lanzar excepción si no existe configuración para el usuario")
    void buscarConfiguracionPorUsuario_cuandoNoExiste_debeLanzarExcepcion() {
        when(configuracionUsuarioRepository.findByIdUsuario(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> configuracionUsuarioService.buscarConfiguracionPorUsuario(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró configuración para el usuario con ID: 99");

        verify(configuracionUsuarioRepository, times(1)).findByIdUsuario(99);
    }

    @Test
    @DisplayName("Debe crear configuración correctamente")
    void crearConfiguracion_cuandoDatosValidos_debeCrearConfiguracion() {
        ConfiguracionUsuarioRequestDTO requestDTO = new ConfiguracionUsuarioRequestDTO(
                1,
                1,
                true,
                false,
                "es"
        );

        when(usuarioReferenciaRepository.existsByIdUsuarioAndActivoTrue(1)).thenReturn(true);
        when(colorReferenciaRepository.existsByIdColorAndActivoTrue(1)).thenReturn(true);
        when(configuracionUsuarioRepository.existsByIdUsuario(1)).thenReturn(false);

        when(configuracionUsuarioRepository.save(any(ConfiguracionUsuario.class))).thenAnswer(invocation -> {
            ConfiguracionUsuario configuracion = invocation.getArgument(0);
            configuracion.setIdConfiguracionUsuario(1);
            return configuracion;
        });

        ConfiguracionUsuarioResponseDTO resultado = configuracionUsuarioService.crearConfiguracion(requestDTO);

        assertThat(resultado.idConfiguracionUsuario()).isEqualTo(1);
        assertThat(resultado.idUsuario()).isEqualTo(1);
        assertThat(resultado.idColor()).isEqualTo(1);
        assertThat(resultado.notificacionesActivas()).isTrue();
        assertThat(resultado.modoOscuro()).isFalse();
        assertThat(resultado.idioma()).isEqualTo("ES");

        verify(usuarioReferenciaRepository, times(1)).existsByIdUsuarioAndActivoTrue(1);
        verify(colorReferenciaRepository, times(1)).existsByIdColorAndActivoTrue(1);
        verify(configuracionUsuarioRepository, times(1)).existsByIdUsuario(1);
        verify(configuracionUsuarioRepository, times(1)).save(any(ConfiguracionUsuario.class));
    }

    @Test
    @DisplayName("No debe crear configuración si el usuario no existe o está inactivo")
    void crearConfiguracion_cuandoUsuarioNoExiste_debeLanzarResourceNotFound() {
        ConfiguracionUsuarioRequestDTO requestDTO = new ConfiguracionUsuarioRequestDTO(
                99,
                1,
                true,
                false,
                "ES"
        );

        when(usuarioReferenciaRepository.existsByIdUsuarioAndActivoTrue(99)).thenReturn(false);

        assertThatThrownBy(() -> configuracionUsuarioService.crearConfiguracion(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No existe un usuario activo registrado con ID: 99");

        verify(colorReferenciaRepository, never()).existsByIdColorAndActivoTrue(any());
        verify(configuracionUsuarioRepository, never()).save(any(ConfiguracionUsuario.class));
    }

    @Test
    @DisplayName("No debe crear configuración si el color no existe o está inactivo")
    void crearConfiguracion_cuandoColorNoExiste_debeLanzarResourceNotFound() {
        ConfiguracionUsuarioRequestDTO requestDTO = new ConfiguracionUsuarioRequestDTO(
                1,
                99,
                true,
                false,
                "ES"
        );

        when(usuarioReferenciaRepository.existsByIdUsuarioAndActivoTrue(1)).thenReturn(true);
        when(colorReferenciaRepository.existsByIdColorAndActivoTrue(99)).thenReturn(false);

        assertThatThrownBy(() -> configuracionUsuarioService.crearConfiguracion(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No existe un color activo registrado con ID: 99");

        verify(configuracionUsuarioRepository, never()).save(any(ConfiguracionUsuario.class));
    }

    @Test
    @DisplayName("No debe crear configuración duplicada para el mismo usuario")
    void crearConfiguracion_cuandoUsuarioYaTieneConfiguracion_debeLanzarBadRequest() {
        ConfiguracionUsuarioRequestDTO requestDTO = new ConfiguracionUsuarioRequestDTO(
                1,
                1,
                true,
                false,
                "ES"
        );

        when(usuarioReferenciaRepository.existsByIdUsuarioAndActivoTrue(1)).thenReturn(true);
        when(colorReferenciaRepository.existsByIdColorAndActivoTrue(1)).thenReturn(true);
        when(configuracionUsuarioRepository.existsByIdUsuario(1)).thenReturn(true);

        assertThatThrownBy(() -> configuracionUsuarioService.crearConfiguracion(requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe una configuración registrada para el usuario con ID: 1");

        verify(configuracionUsuarioRepository, never()).save(any(ConfiguracionUsuario.class));
    }

    @Test
    @DisplayName("Debe actualizar configuración correctamente")
    void actualizarConfiguracion_cuandoDatosValidos_debeActualizarConfiguracion() {
        ConfiguracionUsuario configuracionExistente = new ConfiguracionUsuario(1, 1, 1, true, false, "ES");

        ConfiguracionUsuarioRequestDTO requestDTO = new ConfiguracionUsuarioRequestDTO(
                1,
                2,
                false,
                true,
                "en"
        );

        when(configuracionUsuarioRepository.findById(1)).thenReturn(Optional.of(configuracionExistente));
        when(usuarioReferenciaRepository.existsByIdUsuarioAndActivoTrue(1)).thenReturn(true);
        when(colorReferenciaRepository.existsByIdColorAndActivoTrue(2)).thenReturn(true);
        when(configuracionUsuarioRepository.existsByIdUsuarioAndIdConfiguracionUsuarioNot(1, 1)).thenReturn(false);
        when(configuracionUsuarioRepository.save(any(ConfiguracionUsuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConfiguracionUsuarioResponseDTO resultado = configuracionUsuarioService.actualizarConfiguracion(1, requestDTO);

        assertThat(resultado.idConfiguracionUsuario()).isEqualTo(1);
        assertThat(resultado.idUsuario()).isEqualTo(1);
        assertThat(resultado.idColor()).isEqualTo(2);
        assertThat(resultado.notificacionesActivas()).isFalse();
        assertThat(resultado.modoOscuro()).isTrue();
        assertThat(resultado.idioma()).isEqualTo("EN");

        verify(configuracionUsuarioRepository, times(1)).findById(1);
        verify(configuracionUsuarioRepository, times(1)).save(any(ConfiguracionUsuario.class));
    }

    @Test
    @DisplayName("No debe actualizar si el usuario ya tiene otra configuración")
    void actualizarConfiguracion_cuandoUsuarioDuplicado_debeLanzarBadRequest() {
        ConfiguracionUsuario configuracionExistente = new ConfiguracionUsuario(1, 1, 1, true, false, "ES");

        ConfiguracionUsuarioRequestDTO requestDTO = new ConfiguracionUsuarioRequestDTO(
                2,
                1,
                true,
                false,
                "ES"
        );

        when(configuracionUsuarioRepository.findById(1)).thenReturn(Optional.of(configuracionExistente));
        when(usuarioReferenciaRepository.existsByIdUsuarioAndActivoTrue(2)).thenReturn(true);
        when(colorReferenciaRepository.existsByIdColorAndActivoTrue(1)).thenReturn(true);
        when(configuracionUsuarioRepository.existsByIdUsuarioAndIdConfiguracionUsuarioNot(2, 1)).thenReturn(true);

        assertThatThrownBy(() -> configuracionUsuarioService.actualizarConfiguracion(1, requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe otra configuración registrada para el usuario con ID: 2");

        verify(configuracionUsuarioRepository, never()).save(any(ConfiguracionUsuario.class));
    }

    @Test
    @DisplayName("Debe eliminar configuración correctamente")
    void eliminarConfiguracion_cuandoExiste_debeEliminarConfiguracion() {
        ConfiguracionUsuario configuracion = new ConfiguracionUsuario(1, 1, 1, true, false, "ES");

        when(configuracionUsuarioRepository.findById(1)).thenReturn(Optional.of(configuracion));

        configuracionUsuarioService.eliminarConfiguracion(1);

        verify(configuracionUsuarioRepository, times(1)).findById(1);
        verify(configuracionUsuarioRepository, times(1)).delete(configuracion);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar configuración inexistente")
    void eliminarConfiguracion_cuandoNoExiste_debeLanzarExcepcion() {
        when(configuracionUsuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> configuracionUsuarioService.eliminarConfiguracion(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró la configuración con ID: 99");

        verify(configuracionUsuarioRepository, never()).delete(any(ConfiguracionUsuario.class));
    }
}