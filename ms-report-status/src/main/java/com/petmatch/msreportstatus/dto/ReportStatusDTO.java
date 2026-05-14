package com.petmatch.msreportstatus.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportStatusDTO {
    @NotBlank(message = "El nombre del estado es obligatorio")
    private String name;
}
