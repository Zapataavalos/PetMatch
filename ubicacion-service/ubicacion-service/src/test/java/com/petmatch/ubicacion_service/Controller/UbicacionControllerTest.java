package com.petmatch.ubicacion_service.Controller;

import com.petmatch.ubicacion_service.DTO.UbicacionResponseDTO;
import com.petmatch.ubicacion_service.Exception.GlobalExceptionHandler;
import com.petmatch.ubicacion_service.Exception.ResourceNotFoundException;
import com.petmatch.ubicacion_service.Service.UbicacionService;
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

@WebMvcTest(UbicacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UbicacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UbicacionService ubicacionService;

    @Test
    @DisplayName("GET /api/v1/ubicaciones debe listar ubicaciones")
    void listarUbicaciones_debeRetornarStatus200() throws Exception {
        List<UbicacionResponseDTO> ubicaciones = List.of(
                new UbicacionResponseDTO(
                        1,
                        "AV. PROVIDENCIA",
                        "1234",
                        "CERCA DEL METRO",
                        "7500000",
                        -33.4263,
                        -70.6170,
                        1
                )
        );

        when(ubicacionService.listarUbicaciones()).thenReturn(ubicaciones);

        mockMvc.perform(get("/api/v1/ubicaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUbicacion").value(1))
                .andExpect(jsonPath("$[0].direccion").value("AV. PROVIDENCIA"))
                .andExpect(jsonPath("$[0].numero").value("1234"))
                .andExpect(jsonPath("$[0].idCiudad").value(1));

        verify(ubicacionService, times(1)).listarUbicaciones();
    }

    @Test
    @DisplayName("GET /api/v1/ubicaciones/ciudad/{idCiudad} debe listar ubicaciones por ciudad")
    void listarUbicacionesPorCiudad_debeRetornarStatus200() throws Exception {
        List<UbicacionResponseDTO> ubicaciones = List.of(
                new UbicacionResponseDTO(
                        1,
                        "AV. PROVIDENCIA",
                        "1234",
                        "CERCA DEL METRO",
                        "7500000",
                        -33.4263,
                        -70.6170,
                        1
                )
        );

        when(ubicacionService.listarUbicacionesPorCiudad(1)).thenReturn(ubicaciones);

        mockMvc.perform(get("/api/v1/ubicaciones/ciudad/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUbicacion").value(1))
                .andExpect(jsonPath("$[0].idCiudad").value(1));

        verify(ubicacionService, times(1)).listarUbicacionesPorCiudad(1);
    }

    @Test
    @DisplayName("GET /api/v1/ubicaciones/{idUbicacion} debe retornar ubicación")
    void buscarUbicacionPorId_cuandoExiste_debeRetornarStatus200() throws Exception {
        UbicacionResponseDTO responseDTO = new UbicacionResponseDTO(
                1,
                "AV. PROVIDENCIA",
                "1234",
                "CERCA DEL METRO",
                "7500000",
                -33.4263,
                -70.6170,
                1
        );

        when(ubicacionService.buscarUbicacionPorId(1)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/ubicaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUbicacion").value(1))
                .andExpect(jsonPath("$.direccion").value("AV. PROVIDENCIA"))
                .andExpect(jsonPath("$.numero").value("1234"))
                .andExpect(jsonPath("$.idCiudad").value(1));

        verify(ubicacionService, times(1)).buscarUbicacionPorId(1);
    }

    @Test
    @DisplayName("GET /api/v1/ubicaciones/{idUbicacion} debe retornar 404 si no existe")
    void buscarUbicacionPorId_cuandoNoExiste_debeRetornarStatus404() throws Exception {
        when(ubicacionService.buscarUbicacionPorId(99))
                .thenThrow(new ResourceNotFoundException("No se encontró la ubicación con ID: 99"));

        mockMvc.perform(get("/api/v1/ubicaciones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontró la ubicación con ID: 99"));

        verify(ubicacionService, times(1)).buscarUbicacionPorId(99);
    }

    @Test
    @DisplayName("POST /api/v1/ubicaciones debe crear ubicación")
    void crearUbicacion_conDatosValidos_debeRetornarStatus201() throws Exception {
        UbicacionResponseDTO responseDTO = new UbicacionResponseDTO(
                1,
                "AV. PROVIDENCIA",
                "1234",
                "CERCA DEL METRO",
                "7500000",
                -33.4263,
                -70.6170,
                1
        );

        when(ubicacionService.crearUbicacion(any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "direccion": "Av. Providencia",
                    "numero": "1234",
                    "referencia": "Cerca del metro",
                    "codigoPostal": "7500000",
                    "latitud": -33.4263,
                    "longitud": -70.6170,
                    "idCiudad": 1
                }
                """;

        mockMvc.perform(post("/api/v1/ubicaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUbicacion").value(1))
                .andExpect(jsonPath("$.direccion").value("AV. PROVIDENCIA"))
                .andExpect(jsonPath("$.numero").value("1234"))
                .andExpect(jsonPath("$.idCiudad").value(1));

        verify(ubicacionService, times(1)).crearUbicacion(any());
    }

    @Test
    @DisplayName("POST /api/v1/ubicaciones debe retornar 400 si dirección está vacía")
    void crearUbicacion_conDireccionVacia_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "direccion": "",
                    "numero": "1234",
                    "referencia": "Cerca del metro",
                    "codigoPostal": "7500000",
                    "latitud": -33.4263,
                    "longitud": -70.6170,
                    "idCiudad": 1
                }
                """;

        mockMvc.perform(post("/api/v1/ubicaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(ubicacionService);
    }

    @Test
    @DisplayName("POST /api/v1/ubicaciones debe retornar 400 si latitud está fuera de rango")
    void crearUbicacion_conLatitudFueraDeRango_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "direccion": "Av. Providencia",
                    "numero": "1234",
                    "referencia": "Cerca del metro",
                    "codigoPostal": "7500000",
                    "latitud": -100.0,
                    "longitud": -70.6170,
                    "idCiudad": 1
                }
                """;

        mockMvc.perform(post("/api/v1/ubicaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(ubicacionService);
    }

    @Test
    @DisplayName("POST /api/v1/ubicaciones debe retornar 400 si idCiudad es nulo")
    void crearUbicacion_conIdCiudadNulo_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "direccion": "Av. Providencia",
                    "numero": "1234",
                    "referencia": "Cerca del metro",
                    "codigoPostal": "7500000",
                    "latitud": -33.4263,
                    "longitud": -70.6170,
                    "idCiudad": null
                }
                """;

        mockMvc.perform(post("/api/v1/ubicaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(ubicacionService);
    }

    @Test
    @DisplayName("PUT /api/v1/ubicaciones/{idUbicacion} debe actualizar ubicación")
    void actualizarUbicacion_conDatosValidos_debeRetornarStatus200() throws Exception {
        UbicacionResponseDTO responseDTO = new UbicacionResponseDTO(
                1,
                "AV. LAS CONDES",
                "5678",
                "EDIFICIO CORPORATIVO",
                "7550000",
                -33.4089,
                -70.5675,
                1
        );

        when(ubicacionService.actualizarUbicacion(eq(1), any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "direccion": "Av. Las Condes",
                    "numero": "5678",
                    "referencia": "Edificio corporativo",
                    "codigoPostal": "7550000",
                    "latitud": -33.4089,
                    "longitud": -70.5675,
                    "idCiudad": 1
                }
                """;

        mockMvc.perform(put("/api/v1/ubicaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUbicacion").value(1))
                .andExpect(jsonPath("$.direccion").value("AV. LAS CONDES"))
                .andExpect(jsonPath("$.numero").value("5678"))
                .andExpect(jsonPath("$.idCiudad").value(1));

        verify(ubicacionService, times(1)).actualizarUbicacion(eq(1), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/ubicaciones/{idUbicacion} debe eliminar ubicación")
    void eliminarUbicacion_debeRetornarStatus204() throws Exception {
        doNothing().when(ubicacionService).eliminarUbicacion(1);

        mockMvc.perform(delete("/api/v1/ubicaciones/1"))
                .andExpect(status().isNoContent());

        verify(ubicacionService, times(1)).eliminarUbicacion(1);
    }
}