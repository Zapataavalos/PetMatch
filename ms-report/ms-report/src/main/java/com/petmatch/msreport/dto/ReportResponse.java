package com.petmatch.msreport.dto;

import com.petmatch.msreport.model.ReportStatus;

import java.time.LocalDateTime;

public record ReportResponse(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        String ubicacion,
        ReportStatus estado,
        String imagenUrl,
        Double latitud,
        Double longitud,
        LocalDateTime createdAt
) {
}
