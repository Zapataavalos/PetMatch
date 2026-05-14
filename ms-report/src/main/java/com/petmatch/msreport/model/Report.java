package com.petmatch.msreport.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * MER: REPORTE
 *  - idReport    PK
 *  - date        fecha/hora del reporte (asignada automáticamente)
 *  - description descripción del reporte
 *  - idUser      FK → ms-usuario (servicio externo)
 *  - idPet       FK → ms-pet     (validado vía Feign)
 *  - idLocation  FK → ms-geolocation (servicio externo)
 *  - idType      FK → ms-report-type  (asignado por Factory Method)
 *  - idStatus    FK → ms-report-status (asignado por Factory Method)
 */
@Entity
@Table(name = "REPORT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReport;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Long idUser;

    // FK → ms-pet
    @Column(nullable = false)
    private Long idPet;

    // FK → ms-geolocation (externo)
    @Column(nullable = false)
    private Long idLocation;

    // FK → ms-report-type (1=PERDIDA, 2=ENCONTRADA, 3=EN_PELIGRO)
    @Column(nullable = false)
    private Long idType;

    // FK → ms-report-status (1=ACTIVO, 2=RESUELTO, 3=URGENTE)
    @Column(nullable = false)
    private Long idStatus;
}
