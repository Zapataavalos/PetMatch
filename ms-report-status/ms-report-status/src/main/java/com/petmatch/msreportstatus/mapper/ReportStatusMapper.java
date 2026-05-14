package com.petmatch.msreportstatus.mapper;
import com.petmatch.msreportstatus.dto.ReportStatusDTO;
import com.petmatch.msreportstatus.dto.ReportStatusResponseDTO;
import com.petmatch.msreportstatus.model.ReportStatus;
import org.springframework.stereotype.Component;
@Component
public class ReportStatusMapper {
    public ReportStatus toEntity(ReportStatusDTO dto) { return ReportStatus.builder().name(dto.getName()).build(); }
    public ReportStatusResponseDTO toDTO(ReportStatus e) { return ReportStatusResponseDTO.builder().idReportStatus(e.getIdReportStatus()).name(e.getName()).build(); }
}
