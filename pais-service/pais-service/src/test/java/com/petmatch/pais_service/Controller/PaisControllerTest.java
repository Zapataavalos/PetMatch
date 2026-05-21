package com.petmatch.pais_service.Controller;

import com.petmatch.pais_service.Dto.PaisResponseDTO;
import com.petmatch.pais_service.Exception.GlobalExceptionHandler;
import com.petmatch.pais_service.Exception.ResourceNotFoundException;
import com.petmatch.pais_service.Service.PaisService;
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

@WebMvcTest(PaisController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PaisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaisService paisService;

    @Test
    @DisplayName("GET /api/v1/paises debe listar todos los países")
    void listarPaises_debeRetornarStatus200() throws Exception {
        List<PaisResponseDTO> paises = List.of(
                new PaisResponseDTO(1, "CHILE"),
                new PaisResponseDTO(2, "ARGENTINA")
        );

        when(paisService.listarPaises()).thenReturn(paises);

        mockMvc.perform(get("/api/v1/paises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPais").value(1))
                .andExpect(jsonPath("$[0].nombrePais").value("CHILE"))
                .andExpect(jsonPath("$[1].idPais").value(2))
                .andExpect(jsonPath("$[1].nombrePais").value("ARGENTINA"));

        verify(paisService, times(1)).listarPaises();
    }

    @Test
    @DisplayName("GET /api/v1/paises/{idPais} debe retornar un país existente")
    void buscarPaisPorId_cuandoExiste_debeRetornarStatus200() throws Exception {
        PaisResponseDTO pais = new PaisResponseDTO(1, "CHILE");

        when(paisService.buscarPaisPorId(1)).thenReturn(pais);

        mockMvc.perform(get("/api/v1/paises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPais").value(1))
                .andExpect(jsonPath("$.nombrePais").value("CHILE"));

        verify(paisService, times(1)).buscarPaisPorId(1);
    }

    @Test
    @DisplayName("GET /api/v1/paises/{idPais} debe retornar 404 si el país no existe")
    void buscarPaisPorId_cuandoNoExiste_debeRetornarStatus404() throws Exception {
        when(paisService.buscarPaisPorId(99))
                .thenThrow(new ResourceNotFoundException("No se encontró el país con ID: 99"));

        mockMvc.perform(get("/api/v1/paises/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontró el país con ID: 99"));

        verify(paisService, times(1)).buscarPaisPorId(99);
    }

    @Test
    @DisplayName("POST /api/v1/paises debe crear un país correctamente")
    void crearPais_conDatosValidos_debeRetornarStatus201() throws Exception {
        PaisResponseDTO responseDTO = new PaisResponseDTO(1, "CHILE");

        when(paisService.crearPais(any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombrePais": "Chile"
                }
                """;

        mockMvc.perform(post("/api/v1/paises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPais").value(1))
                .andExpect(jsonPath("$.nombrePais").value("CHILE"));

        verify(paisService, times(1)).crearPais(any());
    }

    @Test
    @DisplayName("POST /api/v1/paises debe retornar 400 si el nombre está vacío")
    void crearPais_conNombreVacio_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombrePais": ""
                }
                """;

        mockMvc.perform(post("/api/v1/paises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(paisService);
    }

    @Test
    @DisplayName("PUT /api/v1/paises/{idPais} debe actualizar un país correctamente")
    void actualizarPais_conDatosValidos_debeRetornarStatus200() throws Exception {
        PaisResponseDTO responseDTO = new PaisResponseDTO(1, "ARGENTINA");

        when(paisService.actualizarPais(eq(1), any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombrePais": "Argentina"
                }
                """;

        mockMvc.perform(put("/api/v1/paises/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPais").value(1))
                .andExpect(jsonPath("$.nombrePais").value("ARGENTINA"));

        verify(paisService, times(1)).actualizarPais(eq(1), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/paises/{idPais} debe eliminar un país correctamente")
    void eliminarPais_cuandoExiste_debeRetornarStatus204() throws Exception {
        doNothing().when(paisService).eliminarPais(1);

        mockMvc.perform(delete("/api/v1/paises/1"))
                .andExpect(status().isNoContent());

        verify(paisService, times(1)).eliminarPais(1);
    }
}