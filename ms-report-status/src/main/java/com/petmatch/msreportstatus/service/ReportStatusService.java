package com.petmatch.msreportstatus.service;

import com.petmatch.msreportstatus.dto.ReportStatusRequest;
import com.petmatch.msreportstatus.dto.ReportStatusResponse;
import com.petmatch.msreportstatus.messaging.EventPublisher;
import com.petmatch.msreportstatus.model.ReportStatusCatalog;
import com.petmatch.msreportstatus.repository.ReportStatusRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class ReportStatusService {

    private final ReportStatusRepository reportStatusRepository;
    private final EventPublisher eventPublisher;

    public ReportStatusService(ReportStatusRepository reportStatusRepository, EventPublisher eventPublisher) {
        this.reportStatusRepository = reportStatusRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<ReportStatusResponse> listar() {
        return reportStatusRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ReportStatusResponse buscar(Long id) {
        return toResponse(findById(id));
    }

    public ReportStatusResponse crear(ReportStatusRequest request) {
        String nombre = normalizeName(request.nombre());
        if (reportStatusRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de reporte duplicado");
        }

        ReportStatusCatalog status = new ReportStatusCatalog();
        apply(status, request);
        ReportStatusCatalog saved = reportStatusRepository.save(status);
        ReportStatusResponse response = toResponse(saved);
        eventPublisher.publish("CREATED", "REPORT_STATUS", response);
        return response;
    }

    public ReportStatusResponse actualizar(Long id, ReportStatusRequest request) {
        ReportStatusCatalog status = findById(id);
        String nombre = normalizeName(request.nombre());
        if (reportStatusRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de reporte duplicado");
        }

        apply(status, request);
        ReportStatusCatalog saved = reportStatusRepository.save(status);
        ReportStatusResponse response = toResponse(saved);
        eventPublisher.publish("UPDATED", "REPORT_STATUS", response);
        return response;
    }

    public void eliminar(Long id) {
        if (!reportStatusRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Estado de reporte no encontrado");
        }

        reportStatusRepository.deleteById(id);
        eventPublisher.publish("DELETED", "REPORT_STATUS", Map.of("id", id));
    }

    private ReportStatusCatalog findById(Long id) {
        return reportStatusRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estado de reporte no encontrado"));
    }

    private void apply(ReportStatusCatalog status, ReportStatusRequest request) {
        status.setNombre(normalizeName(request.nombre()));
        status.setDescripcion(normalizeText(request.descripcion(), "Sin descripcion"));
    }

    private ReportStatusResponse toResponse(ReportStatusCatalog status) {
        return new ReportStatusResponse(status.getId(), status.getNombre(), status.getDescripcion());
    }

    private String normalizeName(String value) {
        return value.trim().toUpperCase();
    }

    private String normalizeText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value.trim();
    }
}
