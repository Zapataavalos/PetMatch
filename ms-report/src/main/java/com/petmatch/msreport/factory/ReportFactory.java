package com.petmatch.msreport.factory;

import com.petmatch.msreport.dto.ReportDTO;
import com.petmatch.msreport.model.Report;

import java.time.LocalDateTime;

/**
 * Factory Method Pattern — sección 4.3 del informe PetMatch.
 *
 * Crea objetos Report según el tipo indicado en el DTO,
 * asignando automáticamente el estado correspondiente:
 *
 *   Tipo 1 → PERDIDA    → Estado 1 (ACTIVO)
 *   Tipo 2 → ENCONTRADA → Estado 2 (RESUELTO)
 *   Tipo 3 → EN_PELIGRO → Estado 3 (URGENTE)
 *
 * El cliente solo envía idType; el Factory decide el estado.
 * Esto implementa el principio Open/Closed: para agregar un nuevo
 * tipo de reporte solo se añade un case aquí sin tocar el resto.
 */
public abstract class ReportFactory {

    public static Report crearReporte(ReportDTO dto) {
        return switch (dto.getIdType()) {
            case 1 -> crearReportePerdida(dto);
            case 2 -> crearReporteEncontrado(dto);
            case 3 -> crearReportePeligro(dto);
            default -> throw new IllegalArgumentException(
                    "Tipo de reporte inválido: " + dto.getIdType() +
                    ". Valores válidos: 1=PERDIDA, 2=ENCONTRADA, 3=EN_PELIGRO");
        };
    }

    private static Report crearReportePerdida(ReportDTO dto) {
        return Report.builder()
                .date(LocalDateTime.now())
                .description(dto.getDescription())
                .idUser(dto.getIdUser())
                .idPet(dto.getIdPet())
                .idLocation(dto.getIdLocation())
                .idType(1L)
                .idStatus(1L) // ACTIVO
                .build();
    }

    private static Report crearReporteEncontrado(ReportDTO dto) {
        return Report.builder()
                .date(LocalDateTime.now())
                .description(dto.getDescription())
                .idUser(dto.getIdUser())
                .idPet(dto.getIdPet())
                .idLocation(dto.getIdLocation())
                .idType(2L)
                .idStatus(2L) // RESUELTO
                .build();
    }

    private static Report crearReportePeligro(ReportDTO dto) {
        return Report.builder()
                .date(LocalDateTime.now())
                .description(dto.getDescription())
                .idUser(dto.getIdUser())
                .idPet(dto.getIdPet())
                .idLocation(dto.getIdLocation())
                .idType(3L)
                .idStatus(3L) // URGENTE
                .build();
    }
}
