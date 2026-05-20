package com.petmatch.msreportstatus;

import com.petmatch.msreportstatus.dto.ReportStatusDTO;
import com.petmatch.msreportstatus.dto.ReportStatusResponseDTO;
import com.petmatch.msreportstatus.exception.ResourceNotFoundException;
import com.petmatch.msreportstatus.mapper.ReportStatusMapper;
import com.petmatch.msreportstatus.model.ReportStatus;
import com.petmatch.msreportstatus.repository.ReportStatusRepository;
import com.petmatch.msreportstatus.service.ReportStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportStatusServiceTest {

    @Mock ReportStatusRepository repo;
    @Mock ReportStatusMapper mapper;
    @InjectMocks ReportStatusService service;

    private ReportStatus entity;
    private ReportStatusResponseDTO responseDTO;
    private ReportStatusDTO dto;

    @BeforeEach
    void setUp() {
        entity      = ReportStatus.builder().idReportStatus(1L).name("ACTIVO").build();
        responseDTO = ReportStatusResponseDTO.builder().idReportStatus(1L).name("ACTIVO").build();
        dto         = ReportStatusDTO.builder().name("ACTIVO").build();
    }

    @Test
    void getAll_returnsAllStatuses() {
        when(repo.findAll()).thenReturn(List.of(entity));
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        List<ReportStatusResponseDTO> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("ACTIVO");
    }

    @Test
    void getById_existingId_returnsDTO() {
        when(repo.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        ReportStatusResponseDTO result = service.getById(1L);

        assertThat(result.getIdReportStatus()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("ACTIVO");
    }

    @Test
    void getById_nonExistingId_throwsResourceNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_newStatus_savesAndReturnsDTO() {
        when(repo.findByNameIgnoreCase("ACTIVO")).thenReturn(Optional.empty());
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        ReportStatusResponseDTO result = service.save(dto);

        assertThat(result.getName()).isEqualTo("ACTIVO");
        verify(repo).save(entity);
    }

    @Test
    void save_duplicateName_throwsIllegalArgumentException() {
        when(repo.findByNameIgnoreCase("ACTIVO")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ACTIVO");

        verify(repo, never()).save(any());
    }

    @Test
    void update_existingId_updatesName() {
        ReportStatusDTO updateDto = ReportStatusDTO.builder().name("RESUELTO").build();
        when(repo.findById(1L)).thenReturn(Optional.of(entity));
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(
                ReportStatusResponseDTO.builder().idReportStatus(1L).name("RESUELTO").build());

        ReportStatusResponseDTO result = service.update(1L, updateDto);

        assertThat(result.getName()).isEqualTo("RESUELTO");
        verify(repo).save(entity);
    }

    @Test
    void update_nonExistingId_throwsResourceNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(repo.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(repo, never()).deleteById(any());
    }
}
