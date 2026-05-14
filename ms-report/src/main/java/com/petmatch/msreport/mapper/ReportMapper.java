package com.petmatch.msreport.mapper;

import com.petmatch.msreport.dto.ReportResponseDTO;
import com.petmatch.msreport.model.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportResponseDTO toDTO(Report r, String typeName, String statusName) {
        return ReportResponseDTO.builder()
                .idReport(r.getIdReport())
                .date(r.getDate())
                .description(r.getDescription())
                .idUser(r.getIdUser())
                .idPet(r.getIdPet())
                .idLocation(r.getIdLocation())
                .idType(r.getIdType())
                .typeName(typeName)
                .idStatus(r.getIdStatus())
                .statusName(statusName)
                .build();
    }
}
