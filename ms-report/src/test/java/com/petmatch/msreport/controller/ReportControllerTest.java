package com.petmatch.msreport.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.msreport.dto.ReportRequest;
import com.petmatch.msreport.dto.ReportResponse;
import com.petmatch.msreport.model.ReportStatus;
import com.petmatch.msreport.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportService reportService;

    @Test
    void listarReportesRetornaOkYJson() throws Exception {
        when(reportService.listarReportes()).thenReturn(List.of(response(1L, "REP-001", "Milo")));

        mockMvc.perform(get("/api/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].codigo").value("REP-001"))
                .andExpect(jsonPath("$[0].nombre").value("Milo"));
    }

    @Test
    void crearReporteValidoRetornaCreated() throws Exception {
        ReportRequest request = new ReportRequest(
                "Milo",
                "Visto cerca del parque",
                "Santiago Centro",
                ReportStatus.PERDIDO,
                "https://example.com/report.jpg",
                -33.45,
                -70.66
        );
        when(reportService.crearReporte(any(ReportRequest.class))).thenReturn(response(7L, "REP-007", "Milo"));

        mockMvc.perform(post("/api/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.codigo").value("REP-007"));
    }

    @Test
    void eliminarReporteExistenteRetornaNoContent() throws Exception {
        doNothing().when(reportService).eliminarReporte(1L);

        mockMvc.perform(delete("/api/report/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarReporteInexistenteRetornaNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado"))
                .when(reportService)
                .eliminarReporte(99L);

        mockMvc.perform(delete("/api/report/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void marcarReporteComoEncontradoRetornaOk() throws Exception {
        when(reportService.marcarComoEncontrado(1L))
                .thenReturn(new ReportResponse(
                        1L,
                        "REP-001",
                        "Milo",
                        "Descripcion",
                        "Santiago",
                        ReportStatus.ENCONTRADO,
                        "https://example.com/report.jpg",
                        -33.45,
                        -70.66,
                        LocalDateTime.of(2026, 6, 4, 9, 0)
                ));

        mockMvc.perform(patch("/api/report/{id}/found", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("ENCONTRADO"));
    }

    private ReportResponse response(Long id, String codigo, String nombre) {
        return new ReportResponse(
                id,
                codigo,
                nombre,
                "Descripcion",
                "Santiago",
                ReportStatus.PERDIDO,
                "https://example.com/report.jpg",
                -33.45,
                -70.66,
                LocalDateTime.of(2026, 6, 4, 9, 0)
        );
    }
}
