package com.petmatch.msreporttype.mapper;
import com.petmatch.msreporttype.dto.ReportTypeDTO;
import com.petmatch.msreporttype.dto.ReportTypeResponseDTO;
import com.petmatch.msreporttype.model.ReportType;
import org.springframework.stereotype.Component;
@Component
public class ReportTypeMapper {
    public ReportType toEntity(ReportTypeDTO dto) { return ReportType.builder().name(dto.getName()).build(); }
    public ReportTypeResponseDTO toDTO(ReportType e) { return ReportTypeResponseDTO.builder().idReportType(e.getIdReportType()).name(e.getName()).build(); }
}
