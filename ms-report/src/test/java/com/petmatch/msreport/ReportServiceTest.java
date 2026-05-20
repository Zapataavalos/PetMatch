package com.petmatch.msreport;

import com.petmatch.msreport.client.PetClient;
import com.petmatch.msreport.client.ReportStatusClient;
import com.petmatch.msreport.client.ReportTypeClient;
import com.petmatch.msreport.dto.*;
import com.petmatch.msreport.exception.ResourceNotFoundException;
import com.petmatch.msreport.mapper.ReportMapper;
import com.petmatch.msreport.model.Report;
import com.petmatch.msreport.repository.ReportRepository;
import com.petmatch.msreport.service.ReportService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock ReportRepository reportRepository;
    @Mock ReportMapper reportMapper;
    @Mock PetClient petClient;
    @Mock ReportTypeClient reportTypeClient;
    @Mock ReportStatusClient reportStatusClient;
    @InjectMocks ReportService service;

    private Report entity;
    private ReportDTO dto;
    private ReportResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        entity = Report.builder()
                .idReport(1L)
                .date(LocalDateTime.now())
                .description("Perdí a mi perro")
                .idUser(5L)
                .idPet(10L)
                .idLocation(3L)
                .idType(1L)
                .idStatus(1L)
                .build();

        dto = ReportDTO.builder()
                .description("Perdí a mi perro")
                .idUser(5L)
                .idPet(10L)
                .idLocation(3L)
                .idType(1)
                .build();

        responseDTO = ReportResponseDTO.builder()
                .idReport(1L)
                .description("Perdí a mi perro")
                .idUser(5L)
                .idPet(10L)
                .idLocation(3L)
                .idType(1L)
                .typeName("PERDIDA")
                .idStatus(1L)
                .statusName("ACTIVO")
                .build();
    }

    @Test
    void getById_existingId_returnsEnrichedDTO() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(reportTypeClient.getById(1L)).thenReturn(new ReportTypeResponseDTO(1L, "PERDIDA"));
        when(reportStatusClient.getById(1L)).thenReturn(new ReportStatusResponseDTO(1L, "ACTIVO"));
        when(reportMapper.toDTO(entity, "PERDIDA", "ACTIVO")).thenReturn(responseDTO);

        ReportResponseDTO result = service.getById(1L);

        assertThat(result.getIdReport()).isEqualTo(1L);
        assertThat(result.getTypeName()).isEqualTo("PERDIDA");
        assertThat(result.getStatusName()).isEqualTo("ACTIVO");
    }

    @Test
    void getById_nonExistingId_throwsResourceNotFoundException() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByUser_returnsUserReports() {
        when(reportRepository.findByIdUser(5L)).thenReturn(List.of(entity));
        when(reportTypeClient.getById(1L)).thenReturn(new ReportTypeResponseDTO(1L, "PERDIDA"));
        when(reportStatusClient.getById(1L)).thenReturn(new ReportStatusResponseDTO(1L, "ACTIVO"));
        when(reportMapper.toDTO(entity, "PERDIDA", "ACTIVO")).thenReturn(responseDTO);

        List<ReportResponseDTO> result = service.getByUser(5L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdUser()).isEqualTo(5L);
    }

    @Test
    void getByPet_returnsPetReports() {
        when(reportRepository.findByIdPet(10L)).thenReturn(List.of(entity));
        when(reportTypeClient.getById(1L)).thenReturn(new ReportTypeResponseDTO(1L, "PERDIDA"));
        when(reportStatusClient.getById(1L)).thenReturn(new ReportStatusResponseDTO(1L, "ACTIVO"));
        when(reportMapper.toDTO(entity, "PERDIDA", "ACTIVO")).thenReturn(responseDTO);

        List<ReportResponseDTO> result = service.getByPet(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdPet()).isEqualTo(10L);
    }

    @Test
    void save_validDTO_usesFactoryAndSaves() {
        when(petClient.getById(10L)).thenReturn(new PetResponseDTO(10L, "Firulais"));
        when(reportRepository.save(any(Report.class))).thenReturn(entity);
        when(reportTypeClient.getById(1L)).thenReturn(new ReportTypeResponseDTO(1L, "PERDIDA"));
        when(reportStatusClient.getById(1L)).thenReturn(new ReportStatusResponseDTO(1L, "ACTIVO"));
        when(reportMapper.toDTO(any(Report.class), eq("PERDIDA"), eq("ACTIVO"))).thenReturn(responseDTO);

        ReportResponseDTO result = service.save(dto);

        assertThat(result.getTypeName()).isEqualTo("PERDIDA");
        assertThat(result.getStatusName()).isEqualTo("ACTIVO");
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void save_petNotFound_throwsResourceNotFoundException() {
        when(petClient.getById(10L)).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("10");

        verify(reportRepository, never()).save(any());
    }

    @Test
    void save_invalidType_throwsIllegalArgumentException() {
        ReportDTO invalidDto = ReportDTO.builder()
                .description("Test").idUser(5L).idPet(10L).idLocation(3L).idType(99).build();

        when(petClient.getById(10L)).thenReturn(new PetResponseDTO(10L, "Firulais"));

        assertThatThrownBy(() -> service.save(invalidDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");

        verify(reportRepository, never()).save(any());
    }

    @Test
    void updateStatus_existingReport_updatesStatus() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(reportStatusClient.getById(2L)).thenReturn(new ReportStatusResponseDTO(2L, "RESUELTO"));
        when(reportRepository.save(entity)).thenReturn(entity);
        when(reportTypeClient.getById(1L)).thenReturn(new ReportTypeResponseDTO(1L, "PERDIDA"));
        when(reportMapper.toDTO(entity, "PERDIDA", "RESUELTO")).thenReturn(
                ReportResponseDTO.builder().idReport(1L).idStatus(2L).statusName("RESUELTO").typeName("PERDIDA").build());

        ReportResponseDTO result = service.updateStatus(1L, 2L);

        assertThat(result.getStatusName()).isEqualTo("RESUELTO");
        verify(reportRepository).save(entity);
    }

    @Test
    void updateStatus_reportNotFound_throwsResourceNotFoundException() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(99L, 2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateStatus_statusNotFound_throwsResourceNotFoundException() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(reportStatusClient.getById(99L)).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> service.updateStatus(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(reportRepository).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(reportRepository, never()).deleteById(any());
    }

    @Test
    void fallbackGetAll_returnsEmptyList() {
        List<ReportResponseDTO> result = service.fallbackGetAll(new RuntimeException("Circuit open"));

        assertThat(result).isEmpty();
    }
}
