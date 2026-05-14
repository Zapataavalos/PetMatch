package com.petmatch.msreportstatus.model;
import jakarta.persistence.*;
import lombok.*;
/**
 * MER: ESTADO_REPORTE
 *  - idReportStatus  PK
 *  - name            (ACTIVO | RESUELTO | URGENTE)
 *
 * Referenciado por ms-report (REPORTE.idStatus).
 * Factory Method de ms-report asigna el estado según el tipo.
 */
@Entity @Table(name = "REPORT_STATUS")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportStatus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReportStatus;
    @Column(nullable = false, unique = true, length = 50)
    private String name;
}
