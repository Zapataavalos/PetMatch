package com.petmatch.usuario_service.Controller;

import com.petmatch.usuario_service.DTO.UsuarioResponseDTO;
import com.petmatch.usuario_service.Exception.GlobalExceptionHandler;
import com.petmatch.usuario_service.Exception.ResourceNotFoundException;
import com.petmatch.usuario_service.Security.JwtAuthenticationFilter;
import com.petmatch.usuario_service.Security.JwtService;
import com.petmatch.usuario_service.Service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("GET /api/v1/usuarios debe listar usuarios")
    void listarUsuarios_debeRetornarStatus200() throws Exception {
        List<UsuarioResponseDTO> usuarios = List.of(
                new UsuarioResponseDTO(1, "BENJAMIN MENDEZ", "benjamin@test.cl", LocalDateTime.now(), 1),
                new UsuarioResponseDTO(2, "JUAN PEREZ", "juan@test.cl", LocalDateTime.now(), 1)
        );

        when(usuarioService.listarUsuarios()).thenReturn(usuarios);

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].nombre").value("BENJAMIN MENDEZ"))
                .andExpect(jsonPath("$[0].email").value("benjamin@test.cl"))
                .andExpect(jsonPath("$[0].idRol").value(1));

        verify(usuarioService, times(1)).listarUsuarios();
    }

    @Test
    @DisplayName("GET /api/v1/usuarios/{idUsuario} debe retornar usuario")
    void buscarUsuarioPorId_cuandoExiste_debeRetornarStatus200() throws Exception {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                LocalDateTime.now(),
                1
        );

        when(usuarioService.buscarUsuarioPorId(1)).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombre").value("BENJAMIN MENDEZ"))
                .andExpect(jsonPath("$.email").value("benjamin@test.cl"))
                .andExpect(jsonPath("$.idRol").value(1));

        verify(usuarioService, times(1)).buscarUsuarioPorId(1);
    }

    @Test
    @DisplayName("GET /api/v1/usuarios/{idUsuario} debe retornar 404 si no existe")
    void buscarUsuarioPorId_cuandoNoExiste_debeRetornarStatus404() throws Exception {
        when(usuarioService.buscarUsuarioPorId(99))
                .thenThrow(new ResourceNotFoundException("No se encontró el usuario con ID: 99"));

        mockMvc.perform(get("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontró el usuario con ID: 99"));

        verify(usuarioService, times(1)).buscarUsuarioPorId(99);
    }

    @Test
    @DisplayName("GET /api/v1/usuarios/rol/{idRol} debe listar usuarios por rol")
    void listarUsuariosPorRol_debeRetornarStatus200() throws Exception {
        List<UsuarioResponseDTO> usuarios = List.of(
                new UsuarioResponseDTO(1, "BENJAMIN MENDEZ", "benjamin@test.cl", LocalDateTime.now(), 1)
        );

        when(usuarioService.listarUsuariosPorRol(1)).thenReturn(usuarios);

        mockMvc.perform(get("/api/v1/usuarios/rol/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].idRol").value(1));

        verify(usuarioService, times(1)).listarUsuariosPorRol(1);
    }

    @Test
    @DisplayName("POST /api/v1/usuarios debe crear usuario")
    void crearUsuario_conDatosValidos_debeRetornarStatus201() throws Exception {
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                LocalDateTime.now(),
                1
        );

        when(usuarioService.crearUsuario(any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombre": "Benjamin Mendez",
                    "email": "benjamin@test.cl",
                    "contrasena": "12345678",
                    "idRol": 1
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombre").value("BENJAMIN MENDEZ"))
                .andExpect(jsonPath("$.email").value("benjamin@test.cl"))
                .andExpect(jsonPath("$.idRol").value(1));

        verify(usuarioService, times(1)).crearUsuario(any());
    }

    @Test
    @DisplayName("POST /api/v1/usuarios debe retornar 400 si email es inválido")
    void crearUsuario_conEmailInvalido_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombre": "Benjamin Mendez",
                    "email": "email-invalido",
                    "contrasena": "12345678",
                    "idRol": 1
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("POST /api/v1/usuarios debe retornar 400 si contraseña es menor a 8 caracteres")
    void crearUsuario_conPasswordCorta_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombre": "Benjamin Mendez",
                    "email": "benjamin@test.cl",
                    "contrasena": "123",
                    "idRol": 1
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("PUT /api/v1/usuarios/{idUsuario} debe actualizar usuario")
    void actualizarUsuario_conDatosValidos_debeRetornarStatus200() throws Exception {
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                1,
                "BENJAMIN ACTUALIZADO",
                "nuevo@test.cl",
                LocalDateTime.now(),
                1
        );

        when(usuarioService.actualizarUsuario(eq(1), any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombre": "Benjamin Actualizado",
                    "email": "nuevo@test.cl",
                    "contrasena": "12345678",
                    "idRol": 1
                }
                """;

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombre").value("BENJAMIN ACTUALIZADO"))
                .andExpect(jsonPath("$.email").value("nuevo@test.cl"))
                .andExpect(jsonPath("$.idRol").value(1));

        verify(usuarioService, times(1)).actualizarUsuario(eq(1), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/usuarios/{idUsuario} debe eliminar usuario")
    void eliminarUsuario_debeRetornarStatus204() throws Exception {
        doNothing().when(usuarioService).eliminarUsuario(1);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).eliminarUsuario(1);
    }
}