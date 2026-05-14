package com.petmatch.msreport.dto;

import lombok.*;

/** Modela la respuesta de ms-report-status para enriquecer respuestas. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportStatusResponseDTO {
    private Long idReportStatus;
    private String name;
}
