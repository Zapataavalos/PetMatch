package com.petmatch.msreport.service;

import com.petmatch.msreport.dto.ReportRequest;
import com.petmatch.msreport.dto.ReportResponse;
import com.petmatch.msreport.messaging.ReportEventPublisher;
import com.petmatch.msreport.model.Report;
import com.petmatch.msreport.model.ReportStatus;
import com.petmatch.msreport.repository.ReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class ReportService implements ReportOperations {

    private static final String DEFAULT_IMAGE_URL =
            "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=600&auto=format&fit=crop";
    private static final double DEFAULT_LATITUDE = -33.4489;
    private static final double DEFAULT_LONGITUDE = -70.6693;

    private final ReportRepository reportRepository;
    private final ReportEventPublisher eventPublisher;

    public ReportService(ReportRepository reportRepository, ReportEventPublisher eventPublisher) {
        this.reportRepository = reportRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<ReportResponse> listarReportes() {
        return reportRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ReportResponse crearReporte(ReportRequest request) {
        Report report = new Report();
        report.setCodigo("PENDING");
        report.setNombre(normalizeText(request.nombre(), "Mascota sin nombre"));
        report.setDescripcion(normalizeText(request.descripcion(), "Sin descripcion"));
        report.setUbicacion(normalizeText(request.ubicacion(), "Ubicacion no informada"));
        report.setEstado(request.estado());
        report.setImagenUrl(normalizeText(request.imagenUrl(), DEFAULT_IMAGE_URL));
        report.setLatitud(request.latitud() != null ? request.latitud() : DEFAULT_LATITUDE);
        report.setLongitud(request.longitud() != null ? request.longitud() : DEFAULT_LONGITUDE);

        Report saved = reportRepository.save(report);
        saved.setCodigo("REP-" + String.format("%03d", saved.getId()));
        saved = reportRepository.save(saved);

        ReportResponse response = toResponse(saved);
        eventPublisher.publish("CREATED", "REPORT", response);
        return response;
    }

    @Override
    public void eliminarReporte(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado");
        }

        reportRepository.deleteById(id);
        eventPublisher.publish("DELETED", "REPORT", Map.of("id", id));
    }

    @Override
    public ReportResponse marcarComoEncontrado(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado"));

        report.setEstado(ReportStatus.ENCONTRADO);
        Report saved = reportRepository.save(report);
        ReportResponse response = toResponse(saved);
        eventPublisher.publish("FOUND", "REPORT", response);
        return response;
    }

    private ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getCodigo(),
                report.getNombre(),
                report.getDescripcion(),
                report.getUbicacion(),
                report.getEstado(),
                report.getImagenUrl(),
                report.getLatitud(),
                report.getLongitud(),
                report.getCreatedAt()
        );
    }

    private String normalizeText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value.trim();
    }
}
