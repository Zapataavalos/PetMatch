package com.petmatch.color_service.Controller;

import com.petmatch.color_service.DTO.ColorResponseDTO;
import com.petmatch.color_service.Exception.GlobalExceptionHandler;
import com.petmatch.color_service.Exception.ResourceNotFoundException;
import com.petmatch.color_service.Service.ColorService;
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

@WebMvcTest(ColorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ColorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ColorService colorService;

    @Test
    @DisplayName("GET /api/v1/colores debe listar todos los colores")
    void listarColores_debeRetornarStatus200() throws Exception {
        List<ColorResponseDTO> colores = List.of(
                new ColorResponseDTO(1, "ROJO", "#FF0000"),
                new ColorResponseDTO(2, "AZUL", "#0000FF")
        );

        when(colorService.listarColores()).thenReturn(colores);

        mockMvc.perform(get("/api/v1/colores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idColor").value(1))
                .andExpect(jsonPath("$[0].nombreColor").value("ROJO"))
                .andExpect(jsonPath("$[0].codigoHexadecimal").value("#FF0000"))
                .andExpect(jsonPath("$[1].idColor").value(2))
                .andExpect(jsonPath("$[1].nombreColor").value("AZUL"))
                .andExpect(jsonPath("$[1].codigoHexadecimal").value("#0000FF"));

        verify(colorService, times(1)).listarColores();
    }

    @Test
    @DisplayName("GET /api/v1/colores/{idColor} debe retornar un color existente")
    void buscarColorPorId_cuandoExiste_debeRetornarStatus200() throws Exception {
        ColorResponseDTO color = new ColorResponseDTO(1, "ROJO", "#FF0000");

        when(colorService.buscarColorPorId(1)).thenReturn(color);

        mockMvc.perform(get("/api/v1/colores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idColor").value(1))
                .andExpect(jsonPath("$.nombreColor").value("ROJO"))
                .andExpect(jsonPath("$.codigoHexadecimal").value("#FF0000"));

        verify(colorService, times(1)).buscarColorPorId(1);
    }

    @Test
    @DisplayName("GET /api/v1/colores/{idColor} debe retornar 404 si no existe")
    void buscarColorPorId_cuandoNoExiste_debeRetornarStatus404() throws Exception {
        when(colorService.buscarColorPorId(99))
                .thenThrow(new ResourceNotFoundException("No se encontró el color con ID: 99"));

        mockMvc.perform(get("/api/v1/colores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontró el color con ID: 99"));

        verify(colorService, times(1)).buscarColorPorId(99);
    }

    @Test
    @DisplayName("POST /api/v1/colores debe crear un color correctamente")
    void crearColor_conDatosValidos_debeRetornarStatus201() throws Exception {
        ColorResponseDTO responseDTO = new ColorResponseDTO(1, "ROJO", "#FF0000");

        when(colorService.crearColor(any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombreColor": "Rojo",
                    "codigoHexadecimal": "#FF0000"
                }
                """;

        mockMvc.perform(post("/api/v1/colores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idColor").value(1))
                .andExpect(jsonPath("$.nombreColor").value("ROJO"))
                .andExpect(jsonPath("$.codigoHexadecimal").value("#FF0000"));

        verify(colorService, times(1)).crearColor(any());
    }

    @Test
    @DisplayName("POST /api/v1/colores debe retornar 400 si el nombre está vacío")
    void crearColor_conNombreVacio_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombreColor": "",
                    "codigoHexadecimal": "#FF0000"
                }
                """;

        mockMvc.perform(post("/api/v1/colores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(colorService);
    }

    @Test
    @DisplayName("POST /api/v1/colores debe retornar 400 si el código hexadecimal está vacío")
    void crearColor_conHexVacio_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombreColor": "Rojo",
                    "codigoHexadecimal": ""
                }
                """;

        mockMvc.perform(post("/api/v1/colores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(colorService);
    }

    @Test
    @DisplayName("POST /api/v1/colores debe retornar 400 si el código hexadecimal tiene formato inválido")
    void crearColor_conHexInvalido_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombreColor": "Rojo",
                    "codigoHexadecimal": "FF0000"
                }
                """;

        mockMvc.perform(post("/api/v1/colores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(colorService);
    }

    @Test
    @DisplayName("PUT /api/v1/colores/{idColor} debe actualizar un color correctamente")
    void actualizarColor_conDatosValidos_debeRetornarStatus200() throws Exception {
        ColorResponseDTO responseDTO = new ColorResponseDTO(1, "AZUL", "#0000FF");

        when(colorService.actualizarColor(eq(1), any())).thenReturn(responseDTO);

        String requestJson = """
                {
                    "nombreColor": "Azul",
                    "codigoHexadecimal": "#0000FF"
                }
                """;

        mockMvc.perform(put("/api/v1/colores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idColor").value(1))
                .andExpect(jsonPath("$.nombreColor").value("AZUL"))
                .andExpect(jsonPath("$.codigoHexadecimal").value("#0000FF"));

        verify(colorService, times(1)).actualizarColor(eq(1), any());
    }

    @Test
    @DisplayName("PUT /api/v1/colores/{idColor} debe retornar 400 si el código hexadecimal es inválido")
    void actualizarColor_conHexInvalido_debeRetornarStatus400() throws Exception {
        String requestJson = """
                {
                    "nombreColor": "Azul",
                    "codigoHexadecimal": "#0000"
                }
                """;

        mockMvc.perform(put("/api/v1/colores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(colorService);
    }

    @Test
    @DisplayName("DELETE /api/v1/colores/{idColor} debe eliminar un color correctamente")
    void eliminarColor_cuandoExiste_debeRetornarStatus204() throws Exception {
        doNothing().when(colorService).eliminarColor(1);

        mockMvc.perform(delete("/api/v1/colores/1"))
                .andExpect(status().isNoContent());

        verify(colorService, times(1)).eliminarColor(1);
    }
}