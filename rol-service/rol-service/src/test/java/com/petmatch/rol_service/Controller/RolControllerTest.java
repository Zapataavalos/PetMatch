package com.petmatch.rol_service.Controller;

import com.petmatch.rol_service.DTO.RolResponseDTO;
import com.petmatch.rol_service.Exception.GlobalExceptionHandler;
import com.petmatch.rol_service.Exception.ResourceNotFoundException;
import com.petmatch.rol_service.Service.RolService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

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

@WebMvcTest(RolController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolService rolService;

    @Test
    @DisplayName("GET /api/v1/roles debe listar todos los roles")
    void listarRoles_debeRetornarStatus200() throws Exception {
        List<RolResponseDTO> roles = List.of(
                new RolResponseDTO(1, "ADMINISTRADOR"),
                new RolResponseDTO(2, "USUARIO")
        );

        when(rolService.listarRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idRol").value(1))
                .andExpect(jsonPath("$[0].nombreRol").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$[1].idRol").value(2))
                .andExpect(jsonPath("$[1].nombreRol").value("USUARIO"));

        verify(rolService, times(1)).listarRoles();
    }

    @Test
    @DisplayName("GET /api/v1/roles/{idRol} debe retornar un rol existente")
    void buscarRolPorId_cuandoExiste_debeRetornarStatus200() throws Exception {
        RolResponseDTO rol = new RolResponseDTO(1, "ADMINISTRADOR");

        when(rolService.buscarRolPorId(1)).thenReturn(rol);

        mockMvc.perform(get("/api/v1/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRol").value(1))
                .andExpect(jsonPath("$.nombreRol").value("ADMINISTRADOR"));

        verify(rolService, times(1)).buscarRolPorId(1);
    }

    @Test
    @DisplayName("GET /api/v1/roles/{idRol} debe retornar 404 si el rol no existe")
    void buscarRolPorId_cuandoNoExiste_debeRetornarStatus404() throws Exception {
        when(rolService.buscarRolPorId(99))
                .thenThrow(new ResourceNotFoundException("No se encontró el rol con ID: 99"));

        mockMvc.perform(get("/api/v1/roles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontró el rol con ID: 99"));

        verify(rolService, times(1)).buscarRolPorId(99);
    }

    @Test
    @DisplayName("POST /api/v1/roles debe crear un rol correctamente")
    void crearRol_conDatosValidos_debeRetornarStatus201() throws Exception {
        RolResponseDTO responseDTO = new RolResponseDTO(1, "ADMINISTRADOR");

        when(rolService.crearRol(any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombreRol": "Administrador"
                }
                """;

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idRol").value(1))
                .andExpect(jsonPath("$.nombreRol").value("ADMINISTRADOR"));

        verify(rolService, times(1)).crearRol(any());
    }

    @Test
    @DisplayName("POST /api/v1/roles debe retornar 400 si el nombre está vacío")
    void crearRol_conNombreVacio_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombreRol": ""
                }
                """;

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(rolService);
    }

    @Test
    @DisplayName("PUT /api/v1/roles/{idRol} debe actualizar un rol correctamente")
    void actualizarRol_conDatosValidos_debeRetornarStatus200() throws Exception {
        RolResponseDTO responseDTO = new RolResponseDTO(1, "ADMIN");

        when(rolService.actualizarRol(eq(1), any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombreRol": "Admin"
                }
                """;

        mockMvc.perform(put("/api/v1/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRol").value(1))
                .andExpect(jsonPath("$.nombreRol").value("ADMIN"));

        verify(rolService, times(1)).actualizarRol(eq(1), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/roles/{idRol} debe eliminar un rol correctamente")
    void eliminarRol_cuandoExiste_debeRetornarStatus204() throws Exception {
        doNothing().when(rolService).eliminarRol(1);

        mockMvc.perform(delete("/api/v1/roles/1"))
                .andExpect(status().isNoContent());

        verify(rolService, times(1)).eliminarRol(1);
    }
}