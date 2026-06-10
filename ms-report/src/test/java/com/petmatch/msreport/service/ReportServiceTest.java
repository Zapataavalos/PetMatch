package com.petmatch.msreport.service;

import com.petmatch.msreport.dto.ReportRequest;
import com.petmatch.msreport.dto.ReportResponse;
import com.petmatch.msreport.messaging.ReportEventPublisher;
import com.petmatch.msreport.model.Report;
import com.petmatch.msreport.model.ReportStatus;
import com.petmatch.msreport.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportEventPublisher eventPublisher;

    @InjectMocks
    private ReportService reportService;

    @Test
    void listarReportesRetornaReportesOrdenados() {
        when(reportRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(report(1L, "REP-001", "Milo")));

        List<ReportResponse> result = reportService.listarReportes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).codigo()).isEqualTo("REP-001");
        assertThat(result.get(0).nombre()).isEqualTo("Milo");
    }

    @Test
    void crearReporteValidoGeneraCodigoYPublicaEvento() {
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            report.setId(7L);
            report.setCreatedAt(LocalDateTime.of(2026, 6, 4, 10, 0));
            return report;
        });

        ReportResponse result = reportService.crearReporte(new ReportRequest(
                "Milo",
                "Visto cerca del parque",
                "Santiago Centro",
                ReportStatus.PERDIDO,
                "https://example.com/report.jpg",
                -33.45,
                -70.66
        ));

        assertThat(result.id()).isEqualTo(7L);
        assertThat(result.codigo()).isEqualTo("REP-007");
        assertThat(result.nombre()).isEqualTo("Milo");
        verify(reportRepository, times(2)).save(any(Report.class));
        verify(eventPublisher).publish(eq("CREATED"), eq("REPORT"), eq(result));
    }

    @Test
    void crearReporteAplicaValoresPorDefecto() {
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            report.setId(8L);
            return report;
        });

        ReportResponse result = reportService.crearReporte(new ReportRequest(
                " ",
                "",
                null,
                ReportStatus.EN_REFUGIO,
                " ",
                null,
                null
        ));

        assertThat(result.codigo()).isEqualTo("REP-008");
        assertThat(result.nombre()).isEqualTo("Mascota sin nombre");
        assertThat(result.descripcion()).isEqualTo("Sin descripcion");
        assertThat(result.ubicacion()).isEqualTo("Ubicacion no informada");
        assertThat(result.imagenUrl()).contains("images.unsplash.com");
        assertThat(result.latitud()).isEqualTo(-33.4489);
        assertThat(result.longitud()).isEqualTo(-70.6693);
    }

    @Test
    void eliminarReporteExistenteEliminaYPublicaEvento() {
        when(reportRepository.existsById(1L)).thenReturn(true);

        reportService.eliminarReporte(1L);

        verify(reportRepository).deleteById(1L);
        verify(eventPublisher).publish("DELETED", "REPORT", Map.of("id", 1L));
    }

    @Test
    void eliminarReporteInexistenteLanzaNotFound() {
        when(reportRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> reportService.eliminarReporte(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void marcarComoEncontradoActualizaEstadoYPublicaEvento() {
        Report report = report(1L, "REP-001", "Milo");
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(reportRepository.save(report)).thenReturn(report);

        ReportResponse result = reportService.marcarComoEncontrado(1L);

        assertThat(result.estado()).isEqualTo(ReportStatus.ENCONTRADO);
        verify(reportRepository).save(report);
        verify(eventPublisher).publish("FOUND", "REPORT", result);
    }

    @Test
    void marcarComoEncontradoInexistenteLanzaNotFound() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.marcarComoEncontrado(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Report report(Long id, String codigo, String nombre) {
        Report report = new Report();
        report.setId(id);
        report.setCodigo(codigo);
        report.setNombre(nombre);
        report.setDescripcion("Descripcion");
        report.setUbicacion("Santiago");
        report.setEstado(ReportStatus.PERDIDO);
        report.setImagenUrl("https://example.com/report.jpg");
        report.setLatitud(-33.45);
        report.setLongitud(-70.66);
        report.setCreatedAt(LocalDateTime.of(2026, 6, 4, 9, 0));
        return report;
    }
}
