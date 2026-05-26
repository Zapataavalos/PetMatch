package com.petmatch.region_service.Controller;

import com.petmatch.region_service.DTO.RegionResponseDto;
import com.petmatch.region_service.Exception.GlobalExceptionHandler;
import com.petmatch.region_service.Exception.ResourceNotFoundException;
import com.petmatch.region_service.Service.RegionService;
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

@WebMvcTest(RegionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegionService regionService;

    @Test
    @DisplayName("GET /api/v1/regiones debe listar todas las regiones")
    void listarRegiones_debeRetornarStatus200() throws Exception {
        List<RegionResponseDto> regiones = List.of(
                new RegionResponseDto(1, "METROPOLITANA", 1),
                new RegionResponseDto(2, "VALPARAISO", 1)
        );

        when(regionService.listarRegiones()).thenReturn(regiones);

        mockMvc.perform(get("/api/v1/regiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idRegion").value(1))
                .andExpect(jsonPath("$[0].nombreRegion").value("METROPOLITANA"))
                .andExpect(jsonPath("$[0].idPais").value(1))
                .andExpect(jsonPath("$[1].idRegion").value(2))
                .andExpect(jsonPath("$[1].nombreRegion").value("VALPARAISO"))
                .andExpect(jsonPath("$[1].idPais").value(1));

        verify(regionService, times(1)).listarRegiones();
    }

    @Test
    @DisplayName("GET /api/v1/regiones/pais/{idPais} debe listar regiones por país")
    void listarRegionesPorPais_debeRetornarStatus200() throws Exception {
        List<RegionResponseDto> regiones = List.of(
                new RegionResponseDto(1, "METROPOLITANA", 1),
                new RegionResponseDto(2, "VALPARAISO", 1)
        );

        when(regionService.listarRegionesPorPais(1)).thenReturn(regiones);

        mockMvc.perform(get("/api/v1/regiones/pais/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idRegion").value(1))
                .andExpect(jsonPath("$[0].nombreRegion").value("METROPOLITANA"))
                .andExpect(jsonPath("$[0].idPais").value(1));

        verify(regionService, times(1)).listarRegionesPorPais(1);
    }

    @Test
    @DisplayName("GET /api/v1/regiones/{idRegion} debe retornar una región existente")
    void buscarRegionPorId_cuandoExiste_debeRetornarStatus200() throws Exception {
        RegionResponseDto region = new RegionResponseDto(1, "METROPOLITANA", 1);

        when(regionService.buscarRegionPorId(1)).thenReturn(region);

        mockMvc.perform(get("/api/v1/regiones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRegion").value(1))
                .andExpect(jsonPath("$.nombreRegion").value("METROPOLITANA"))
                .andExpect(jsonPath("$.idPais").value(1));

        verify(regionService, times(1)).buscarRegionPorId(1);
    }

    @Test
    @DisplayName("GET /api/v1/regiones/{idRegion} debe retornar 404 si no existe")
    void buscarRegionPorId_cuandoNoExiste_debeRetornarStatus404() throws Exception {
        when(regionService.buscarRegionPorId(99))
                .thenThrow(new ResourceNotFoundException("No se encontró la región con ID: 99"));

        mockMvc.perform(get("/api/v1/regiones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontró la región con ID: 99"));

        verify(regionService, times(1)).buscarRegionPorId(99);
    }

    @Test
    @DisplayName("POST /api/v1/regiones debe crear una región correctamente")
    void crearRegion_conDatosValidos_debeRetornarStatus201() throws Exception {
        RegionResponseDto responseDTO = new RegionResponseDto(1, "METROPOLITANA", 1);

        when(regionService.crearRegion(any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombreRegion": "Metropolitana",
                    "idPais": 1
                }
                """;

        mockMvc.perform(post("/api/v1/regiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idRegion").value(1))
                .andExpect(jsonPath("$.nombreRegion").value("METROPOLITANA"))
                .andExpect(jsonPath("$.idPais").value(1));

        verify(regionService, times(1)).crearRegion(any());
    }

    @Test
    @DisplayName("POST /api/v1/regiones debe retornar 400 si el nombre está vacío")
    void crearRegion_conNombreVacio_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombreRegion": "",
                    "idPais": 1
                }
                """;

        mockMvc.perform(post("/api/v1/regiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(regionService);
    }

    @Test
    @DisplayName("POST /api/v1/regiones debe retornar 400 si idPais es nulo")
    void crearRegion_conIdPaisNulo_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombreRegion": "Metropolitana",
                    "idPais": null
                }
                """;

        mockMvc.perform(post("/api/v1/regiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(regionService);
    }

    @Test
    @DisplayName("PUT /api/v1/regiones/{idRegion} debe actualizar una región correctamente")
    void actualizarRegion_conDatosValidos_debeRetornarStatus200() throws Exception {
        RegionResponseDto responseDTO = new RegionResponseDto(1, "VALPARAISO", 1);

        when(regionService.actualizarRegion(eq(1), any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombreRegion": "Valparaiso",
                    "idPais": 1
                }
                """;

        mockMvc.perform(put("/api/v1/regiones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRegion").value(1))
                .andExpect(jsonPath("$.nombreRegion").value("VALPARAISO"))
                .andExpect(jsonPath("$.idPais").value(1));

        verify(regionService, times(1)).actualizarRegion(eq(1), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/regiones/{idRegion} debe eliminar una región correctamente")
    void eliminarRegion_cuandoExiste_debeRetornarStatus204() throws Exception {
        doNothing().when(regionService).eliminarRegion(1);

        mockMvc.perform(delete("/api/v1/regiones/1"))
                .andExpect(status().isNoContent());

        verify(regionService, times(1)).eliminarRegion(1);
    }
}