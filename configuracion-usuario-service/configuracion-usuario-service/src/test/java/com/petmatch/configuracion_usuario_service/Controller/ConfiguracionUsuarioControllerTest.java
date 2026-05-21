package com.petmatch.configuracion_usuario_service.Controller;

import com.petmatch.configuracion_usuario_service.DTO.ConfiguracionUsuarioResponseDTO;
import com.petmatch.configuracion_usuario_service.Exception.GlobalExceptionHandler;
import com.petmatch.configuracion_usuario_service.Exception.ResourceNotFoundException;
import com.petmatch.configuracion_usuario_service.Service.ConfiguracionUsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConfiguracionUsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ConfiguracionUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfiguracionUsuarioService configuracionUsuarioService;

    @Test
    @DisplayName("GET /api/v1/configuraciones-usuario debe listar configuraciones")
    void listarConfiguraciones_debeRetornarStatus200() throws Exception {
        List<ConfiguracionUsuarioResponseDTO> configuraciones = List.of(
                new ConfiguracionUsuarioResponseDTO(1, 1, 1, true, false, "ES"),
                new ConfiguracionUsuarioResponseDTO(2, 2, 1, false, true, "EN")
        );

        when(configuracionUsuarioService.listarConfiguraciones()).thenReturn(configuraciones);

        mockMvc.perform(get("/api/v1/configuraciones-usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idConfiguracionUsuario").value(1))
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].idColor").value(1))
                .andExpect(jsonPath("$[0].notificacionesActivas").value(true))
                .andExpect(jsonPath("$[0].modoOscuro").value(false))
                .andExpect(jsonPath("$[0].idioma").value("ES"));

        verify(configuracionUsuarioService, times(1)).listarConfiguraciones();
    }

    @Test
    @DisplayName("GET /api/v1/configuraciones-usuario/{id} debe retornar configuración")
    void buscarConfiguracionPorId_cuandoExiste_debeRetornarStatus200() throws Exception {
        ConfiguracionUsuarioResponseDTO responseDTO =
                new ConfiguracionUsuarioResponseDTO(1, 1, 1, true, false, "ES");

        when(configuracionUsuarioService.buscarConfiguracionPorId(1)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/configuraciones-usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idConfiguracionUsuario").value(1))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.idColor").value(1))
                .andExpect(jsonPath("$.idioma").value("ES"));

        verify(configuracionUsuarioService, times(1)).buscarConfiguracionPorId(1);
    }

    @Test
    @DisplayName("GET /api/v1/configuraciones-usuario/{id} debe retornar 404 si no existe")
    void buscarConfiguracionPorId_cuandoNoExiste_debeRetornarStatus404() throws Exception {
        when(configuracionUsuarioService.buscarConfiguracionPorId(99))
                .thenThrow(new ResourceNotFoundException("No se encontró la configuración con ID: 99"));

        mockMvc.perform(get("/api/v1/configuraciones-usuario/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontró la configuración con ID: 99"));

        verify(configuracionUsuarioService, times(1)).buscarConfiguracionPorId(99);
    }

    @Test
    @DisplayName("GET /api/v1/configuraciones-usuario/usuario/{idUsuario} debe retornar configuración")
    void buscarConfiguracionPorUsuario_cuandoExiste_debeRetornarStatus200() throws Exception {
        ConfiguracionUsuarioResponseDTO responseDTO =
                new ConfiguracionUsuarioResponseDTO(1, 10, 1, true, false, "ES");

        when(configuracionUsuarioService.buscarConfiguracionPorUsuario(10)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/configuraciones-usuario/usuario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(10))
                .andExpect(jsonPath("$.idColor").value(1));

        verify(configuracionUsuarioService, times(1)).buscarConfiguracionPorUsuario(10);
    }

    @Test
    @DisplayName("POST /api/v1/configuraciones-usuario debe crear configuración")
    void crearConfiguracion_conDatosValidos_debeRetornarStatus201() throws Exception {
        ConfiguracionUsuarioResponseDTO responseDTO =
                new ConfiguracionUsuarioResponseDTO(1, 1, 1, true, false, "ES");

        when(configuracionUsuarioService.crearConfiguracion(any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "idUsuario": 1,
                    "idColor": 1,
                    "notificacionesActivas": true,
                    "modoOscuro": false,
                    "idioma": "ES"
                }
                """;

        mockMvc.perform(post("/api/v1/configuraciones-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idConfiguracionUsuario").value(1))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.idColor").value(1))
                .andExpect(jsonPath("$.idioma").value("ES"));

        verify(configuracionUsuarioService, times(1)).crearConfiguracion(any());
    }

    @Test
    @DisplayName("POST /api/v1/configuraciones-usuario debe retornar 400 si idioma es inválido")
    void crearConfiguracion_conIdiomaInvalido_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "idUsuario": 1,
                    "idColor": 1,
                    "notificacionesActivas": true,
                    "modoOscuro": false,
                    "idioma": "FR"
                }
                """;

        mockMvc.perform(post("/api/v1/configuraciones-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(configuracionUsuarioService);
    }

    @Test
    @DisplayName("POST /api/v1/configuraciones-usuario debe retornar 400 si idUsuario es nulo")
    void crearConfiguracion_conIdUsuarioNulo_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "idUsuario": null,
                    "idColor": 1,
                    "notificacionesActivas": true,
                    "modoOscuro": false,
                    "idioma": "ES"
                }
                """;

        mockMvc.perform(post("/api/v1/configuraciones-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(configuracionUsuarioService);
    }

    @Test
    @DisplayName("POST /api/v1/configuraciones-usuario debe retornar 400 si idColor es nulo")
    void crearConfiguracion_conIdColorNulo_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "idUsuario": 1,
                    "idColor": null,
                    "notificacionesActivas": true,
                    "modoOscuro": false,
                    "idioma": "ES"
                }
                """;

        mockMvc.perform(post("/api/v1/configuraciones-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(configuracionUsuarioService);
    }

    @Test
    @DisplayName("PUT /api/v1/configuraciones-usuario/{id} debe actualizar configuración")
    void actualizarConfiguracion_conDatosValidos_debeRetornarStatus200() throws Exception {
        ConfiguracionUsuarioResponseDTO responseDTO =
                new ConfiguracionUsuarioResponseDTO(1, 1, 2, false, true, "EN");

        when(configuracionUsuarioService.actualizarConfiguracion(eq(1), any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "idUsuario": 1,
                    "idColor": 2,
                    "notificacionesActivas": false,
                    "modoOscuro": true,
                    "idioma": "EN"
                }
                """;

        mockMvc.perform(put("/api/v1/configuraciones-usuario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idConfiguracionUsuario").value(1))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.idColor").value(2))
                .andExpect(jsonPath("$.modoOscuro").value(true))
                .andExpect(jsonPath("$.idioma").value("EN"));

        verify(configuracionUsuarioService, times(1)).actualizarConfiguracion(eq(1), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/configuraciones-usuario/{id} debe eliminar configuración")
    void eliminarConfiguracion_debeRetornarStatus204() throws Exception {
        doNothing().when(configuracionUsuarioService).eliminarConfiguracion(1);

        mockMvc.perform(delete("/api/v1/configuraciones-usuario/1"))
                .andExpect(status().isNoContent());

        verify(configuracionUsuarioService, times(1)).eliminarConfiguracion(1);
    }
}