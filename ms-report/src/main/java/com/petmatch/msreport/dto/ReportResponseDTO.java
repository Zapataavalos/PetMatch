package com.petmatch.msreport.dto;
import lombok.*;
import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportResponseDTO {
    private Long idReport;
    private LocalDateTime date;
    private String description;
    private Long idUser;
    private Long idPet;
    private Long idLocation;
    private Long idType;
    private String typeName;
    private Long idStatus;
    private String statusName;
}
