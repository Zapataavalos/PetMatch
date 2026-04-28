package com.petmatch.msreport.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
/**
 * MER: REPORTE
 *  - idReport    PK
 *  - date        fecha del reporte
 *  - description descripción
 *  - idUser      FK → ms-usuario (externo)
 *  - idPet       FK → ms-pet
 *  - idLocation  FK → ms-geolocation (externo)
 *  - idType      FK → ms-report-type
 *  - idStatus    FK → ms-report-status
 */
@Entity @Table(name = "REPORT")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReport;
    @Column(nullable = false)
    private LocalDateTime date;
    @Column(length = 500)
    private String description;
    @Column(nullable = false)
    private Long idUser;
    @Column(nullable = false)
    private Long idPet;
    @Column(nullable = false)
    private Long idLocation;
    // FK → ms-report-type
    @Column(nullable = false)
    private Long idType;
    // FK → ms-report-status
    @Column(nullable = false)
    private Long idStatus;
}
