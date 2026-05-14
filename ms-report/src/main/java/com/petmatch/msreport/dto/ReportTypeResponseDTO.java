package com.petmatch.msreport.dto;

import lombok.*;

/** Modela la respuesta de ms-report-type para enriquecer respuestas. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportTypeResponseDTO {
    private Long idReportType;
    private String name;
}
