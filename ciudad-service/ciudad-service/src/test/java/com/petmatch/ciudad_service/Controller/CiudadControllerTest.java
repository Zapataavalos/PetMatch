package com.petmatch.ciudad_service.Controller;

import com.petmatch.ciudad_service.DTO.CiudadResponseDTO;
import com.petmatch.ciudad_service.Exception.GlobalExceptionHandler;
import com.petmatch.ciudad_service.Exception.ResourceNotFoundException;
import com.petmatch.ciudad_service.Service.CiudadService;
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

@WebMvcTest(CiudadController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CiudadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CiudadService ciudadService;

    @Test
    @DisplayName("GET /api/v1/ciudades debe listar todas las ciudades")
    void listarCiudades_debeRetornarStatus200() throws Exception {
        List<CiudadResponseDTO> ciudades = List.of(
                new CiudadResponseDTO(1, "SANTIAGO", 1),
                new CiudadResponseDTO(2, "PROVIDENCIA", 1)
        );

        when(ciudadService.listarCiudades()).thenReturn(ciudades);

        mockMvc.perform(get("/api/v1/ciudades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCiudad").value(1))
                .andExpect(jsonPath("$[0].nombreCiudad").value("SANTIAGO"))
                .andExpect(jsonPath("$[0].idRegion").value(1))
                .andExpect(jsonPath("$[1].idCiudad").value(2))
                .andExpect(jsonPath("$[1].nombreCiudad").value("PROVIDENCIA"))
                .andExpect(jsonPath("$[1].idRegion").value(1));

        verify(ciudadService, times(1)).listarCiudades();
    }

    @Test
    @DisplayName("GET /api/v1/ciudades/region/{idRegion} debe listar ciudades por región")
    void listarCiudadesPorRegion_debeRetornarStatus200() throws Exception {
        List<CiudadResponseDTO> ciudades = List.of(
                new CiudadResponseDTO(1, "SANTIAGO", 1),
                new CiudadResponseDTO(2, "PROVIDENCIA", 1)
        );

        when(ciudadService.listarCiudadesPorRegion(1)).thenReturn(ciudades);

        mockMvc.perform(get("/api/v1/ciudades/region/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCiudad").value(1))
                .andExpect(jsonPath("$[0].nombreCiudad").value("SANTIAGO"))
                .andExpect(jsonPath("$[0].idRegion").value(1));

        verify(ciudadService, times(1)).listarCiudadesPorRegion(1);
    }

    @Test
    @DisplayName("GET /api/v1/ciudades/{idCiudad} debe retornar una ciudad existente")
    void buscarCiudadPorId_cuandoExiste_debeRetornarStatus200() throws Exception {
        CiudadResponseDTO ciudad = new CiudadResponseDTO(1, "SANTIAGO", 1);

        when(ciudadService.buscarCiudadPorId(1)).thenReturn(ciudad);

        mockMvc.perform(get("/api/v1/ciudades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCiudad").value(1))
                .andExpect(jsonPath("$.nombreCiudad").value("SANTIAGO"))
                .andExpect(jsonPath("$.idRegion").value(1));

        verify(ciudadService, times(1)).buscarCiudadPorId(1);
    }

    @Test
    @DisplayName("GET /api/v1/ciudades/{idCiudad} debe retornar 404 si no existe")
    void buscarCiudadPorId_cuandoNoExiste_debeRetornarStatus404() throws Exception {
        when(ciudadService.buscarCiudadPorId(99))
                .thenThrow(new ResourceNotFoundException("No se encontró la ciudad con ID: 99"));

        mockMvc.perform(get("/api/v1/ciudades/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontró la ciudad con ID: 99"));

        verify(ciudadService, times(1)).buscarCiudadPorId(99);
    }

    @Test
    @DisplayName("POST /api/v1/ciudades debe crear una ciudad correctamente")
    void crearCiudad_conDatosValidos_debeRetornarStatus201() throws Exception {
        CiudadResponseDTO responseDTO = new CiudadResponseDTO(1, "SANTIAGO", 1);

        when(ciudadService.crearCiudad(any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombreCiudad": "Santiago",
                    "idRegion": 1
                }
                """;

        mockMvc.perform(post("/api/v1/ciudades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCiudad").value(1))
                .andExpect(jsonPath("$.nombreCiudad").value("SANTIAGO"))
                .andExpect(jsonPath("$.idRegion").value(1));

        verify(ciudadService, times(1)).crearCiudad(any());
    }

    @Test
    @DisplayName("POST /api/v1/ciudades debe retornar 400 si el nombre está vacío")
    void crearCiudad_conNombreVacio_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombreCiudad": "",
                    "idRegion": 1
                }
                """;

        mockMvc.perform(post("/api/v1/ciudades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(ciudadService);
    }

    @Test
    @DisplayName("POST /api/v1/ciudades debe retornar 400 si idRegion es nulo")
    void crearCiudad_conIdRegionNulo_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombreCiudad": "Santiago",
                    "idRegion": null
                }
                """;

        mockMvc.perform(post("/api/v1/ciudades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(ciudadService);
    }

    @Test
    @DisplayName("PUT /api/v1/ciudades/{idCiudad} debe actualizar una ciudad correctamente")
    void actualizarCiudad_conDatosValidos_debeRetornarStatus200() throws Exception {
        CiudadResponseDTO responseDTO = new CiudadResponseDTO(1, "PROVIDENCIA", 1);

        when(ciudadService.actualizarCiudad(eq(1), any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombreCiudad": "Providencia",
                    "idRegion": 1
                }
                """;

        mockMvc.perform(put("/api/v1/ciudades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCiudad").value(1))
                .andExpect(jsonPath("$.nombreCiudad").value("PROVIDENCIA"))
                .andExpect(jsonPath("$.idRegion").value(1));

        verify(ciudadService, times(1)).actualizarCiudad(eq(1), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/ciudades/{idCiudad} debe eliminar una ciudad correctamente")
    void eliminarCiudad_cuandoExiste_debeRetornarStatus204() throws Exception {
        doNothing().when(ciudadService).eliminarCiudad(1);

        mockMvc.perform(delete("/api/v1/ciudades/1"))
                .andExpect(status().isNoContent());

        verify(ciudadService, times(1)).eliminarCiudad(1);
    }
}