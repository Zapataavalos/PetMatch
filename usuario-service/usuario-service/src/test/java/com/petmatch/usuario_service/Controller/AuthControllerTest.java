package com.petmatch.usuario_service.Controller;

import com.petmatch.usuario_service.DTO.AuthResponseDTO;
import com.petmatch.usuario_service.DTO.UsuarioResponseDTO;
import com.petmatch.usuario_service.Exception.GlobalExceptionHandler;
import com.petmatch.usuario_service.Exception.UnauthorizedException;
import com.petmatch.usuario_service.Security.JwtAuthenticationFilter;
import com.petmatch.usuario_service.Security.JwtService;
import com.petmatch.usuario_service.Service.AuthService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("POST /api/v1/auth/register debe registrar usuario")
    void registrar_conDatosValidos_debeRetornarStatus201() throws Exception {
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                LocalDateTime.now(),
                1
        );

        when(authService.registrar(any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombre": "Benjamin Mendez",
                    "email": "benjamin@test.cl",
                    "contrasena": "12345678",
                    "idRol": 1
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombre").value("BENJAMIN MENDEZ"))
                .andExpect(jsonPath("$.email").value("benjamin@test.cl"))
                .andExpect(jsonPath("$.idRol").value(1));

        verify(authService, times(1)).registrar(any());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register debe retornar 400 si email es inválido")
    void registrar_conEmailInvalido_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombre": "Benjamin Mendez",
                    "email": "correo-invalido",
                    "contrasena": "12345678",
                    "idRol": 1
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar JWT")
    void login_conCredencialesValidas_debeRetornarStatus200() throws Exception {
        AuthResponseDTO responseDTO = new AuthResponseDTO(
                "TOKEN_JWT",
                "Bearer",
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                1
        );

        when(authService.login(any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "email": "benjamin@test.cl",
                    "contrasena": "12345678"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("TOKEN_JWT"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.email").value("benjamin@test.cl"))
                .andExpect(jsonPath("$.idRol").value(1));

        verify(authService, times(1)).login(any());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar 400 si email es inválido")
    void login_conEmailInvalido_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "email": "correo-invalido",
                    "contrasena": "12345678"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar 401 con credenciales inválidas")
    void login_conCredencialesInvalidas_debeRetornarStatus401() throws Exception {
        when(authService.login(any()))
                .thenThrow(new UnauthorizedException("Credenciales inválidas"));

        String requestJson = """
                {
                    "email": "benjamin@test.cl",
                    "contrasena": "incorrecta"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));

        verify(authService, times(1)).login(any());
    }
}