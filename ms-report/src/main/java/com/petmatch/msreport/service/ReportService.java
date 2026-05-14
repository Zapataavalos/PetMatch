package com.petmatch.msreport.service;

import com.petmatch.msreport.client.PetClient;
import com.petmatch.msreport.client.ReportStatusClient;
import com.petmatch.msreport.client.ReportTypeClient;
import com.petmatch.msreport.dto.ReportDTO;
import com.petmatch.msreport.dto.ReportResponseDTO;
import com.petmatch.msreport.exception.ResourceNotFoundException;
import com.petmatch.msreport.factory.ReportFactory;
import com.petmatch.msreport.mapper.ReportMapper;
import com.petmatch.msreport.model.Report;
import com.petmatch.msreport.repository.ReportRepository;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final PetClient petClient;
    private final ReportTypeClient reportTypeClient;
    private final ReportStatusClient reportStatusClient;

    /**
     * Circuit Breaker + Retry — sección 4.4 del informe.
     * Si el servicio falla más del 50% de las llamadas, el circuito
     * se abre y se llama a fallbackGetAll() devolviendo lista vacía.
     */
    @CircuitBreaker(name = "reportService", fallbackMethod = "fallbackGetAll")
    @Retry(name = "reportService")
    public List<ReportResponseDTO> getAll() {
        return reportRepository.findAll()
                .stream()
                .map(this::enrichReport)
                .toList();
    }

    // Fallback — circuito abierto: devuelve lista vacía en lugar de error
    public List<ReportResponseDTO> fallbackGetAll(Exception ex) {
        return Collections.emptyList();
    }

    public ReportResponseDTO getById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reporte con id " + id + " no encontrado"));
        return enrichReport(report);
    }

    public List<ReportResponseDTO> getByUser(Long idUser) {
        return reportRepository.findByIdUser(idUser)
                .stream().map(this::enrichReport).toList();
    }

    public List<ReportResponseDTO> getByPet(Long idPet) {
        return reportRepository.findByIdPet(idPet)
                .stream().map(this::enrichReport).toList();
    }

    public List<ReportResponseDTO> getByType(Long idType) {
        return reportRepository.findByIdType(idType)
                .stream().map(this::enrichReport).toList();
    }

    public List<ReportResponseDTO> getByStatus(Long idStatus) {
        return reportRepository.findByIdStatus(idStatus)
                .stream().map(this::enrichReport).toList();
    }

    /**
     * Crea un reporte usando Factory Method (sección 4.3).
     * 1. Valida que la mascota exista en ms-pet (Feign).
     * 2. El Factory asigna automáticamente tipo y estado.
     * 3. Persiste y devuelve el reporte enriquecido.
     */
    public ReportResponseDTO save(ReportDTO dto) {
        // Validar FK → ms-pet
        try {
            petClient.getById(dto.getIdPet());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Mascota con id " + dto.getIdPet() + " no existe en ms-pet");
        } catch (FeignException e) {
            throw new RuntimeException(
                    "Error al conectar con ms-pet: " + e.getMessage());
        }

        // Factory Method crea el reporte con estado automático
        Report report = ReportFactory.crearReporte(dto);
        return enrichReport(reportRepository.save(report));
    }

    /**
     * Actualiza únicamente el estado de un reporte.
     * Valida que el nuevo estado exista en ms-report-status.
     */
    public ReportResponseDTO updateStatus(Long id, Long idStatus) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reporte con id " + id + " no encontrado"));
        try {
            reportStatusClient.getById(idStatus);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException(
                    "Estado con id " + idStatus + " no existe en ms-report-status");
        }
        report.setIdStatus(idStatus);
        return enrichReport(reportRepository.save(report));
    }

    public void delete(Long id) {
        reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reporte con id " + id + " no encontrado"));
        reportRepository.deleteById(id);
    }

    /**
     * Enriquece el Report con los nombres de tipo y estado
     * consultando ms-report-type y ms-report-status vía Feign.
     * Si alguno no responde, deja el nombre en null (no rompe el flujo).
     */
    private ReportResponseDTO enrichReport(Report r) {
        String typeName = null;
        String statusName = null;
        try { typeName = reportTypeClient.getById(r.getIdType()).getName(); }
        catch (FeignException ignored) {}
        try { statusName = reportStatusClient.getById(r.getIdStatus()).getName(); }
        catch (FeignException ignored) {}
        return reportMapper.toDTO(r, typeName, statusName);
    }
}
