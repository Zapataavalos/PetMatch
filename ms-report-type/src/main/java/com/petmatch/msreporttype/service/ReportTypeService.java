package com.petmatch.msreporttype.service;

import com.petmatch.msreporttype.dto.ReportTypeRequest;
import com.petmatch.msreporttype.dto.ReportTypeResponse;
import com.petmatch.msreporttype.messaging.EventPublisher;
import com.petmatch.msreporttype.model.ReportType;
import com.petmatch.msreporttype.repository.ReportTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class ReportTypeService {

    private final ReportTypeRepository reportTypeRepository;
    private final EventPublisher eventPublisher;

    public ReportTypeService(ReportTypeRepository reportTypeRepository, EventPublisher eventPublisher) {
        this.reportTypeRepository = reportTypeRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<ReportTypeResponse> listar() {
        return reportTypeRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ReportTypeResponse buscar(Long id) {
        return toResponse(findById(id));
    }

    public ReportTypeResponse crear(ReportTypeRequest request) {
        String nombre = normalizeName(request.nombre());
        if (reportTypeRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de reporte duplicado");
        }

        ReportType reportType = new ReportType();
        apply(reportType, request);
        ReportType saved = reportTypeRepository.save(reportType);
        ReportTypeResponse response = toResponse(saved);
        eventPublisher.publish("CREATED", "REPORT_TYPE", response);
        return response;
    }

    public ReportTypeResponse actualizar(Long id, ReportTypeRequest request) {
        ReportType reportType = findById(id);
        String nombre = normalizeName(request.nombre());
        if (reportTypeRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de reporte duplicado");
        }

        apply(reportType, request);
        ReportType saved = reportTypeRepository.save(reportType);
        ReportTypeResponse response = toResponse(saved);
        eventPublisher.publish("UPDATED", "REPORT_TYPE", response);
        return response;
    }

    public void eliminar(Long id) {
        if (!reportTypeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de reporte no encontrado");
        }

        reportTypeRepository.deleteById(id);
        eventPublisher.publish("DELETED", "REPORT_TYPE", Map.of("id", id));
    }

    private ReportType findById(Long id) {
        return reportTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de reporte no encontrado"));
    }

    private void apply(ReportType reportType, ReportTypeRequest request) {
        reportType.setNombre(normalizeName(request.nombre()));
        reportType.setDescripcion(normalizeText(request.descripcion(), "Sin descripcion"));
    }

    private ReportTypeResponse toResponse(ReportType reportType) {
        return new ReportTypeResponse(reportType.getId(), reportType.getNombre(), reportType.getDescripcion());
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
