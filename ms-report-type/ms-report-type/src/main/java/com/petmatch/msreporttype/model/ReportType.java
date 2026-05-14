package com.petmatch.msreporttype.model;
import jakarta.persistence.*;
import lombok.*;
/**
 * MER: TIPO_REPORTE
 *  - idReportType  PK
 *  - name          (PERDIDA | ENCONTRADA | EN_PELIGRO)
 *
 * Referenciado por ms-report (REPORTE.idType).
 * Factory Method de ms-report usa estos ids.
 */
@Entity @Table(name = "REPORT_TYPE")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReportType;
    @Column(nullable = false, unique = true, length = 50)
    private String name;
}
