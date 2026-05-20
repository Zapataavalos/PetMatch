package com.petmatch.msreporttype;

import com.petmatch.msreporttype.dto.ReportTypeDTO;
import com.petmatch.msreporttype.dto.ReportTypeResponseDTO;
import com.petmatch.msreporttype.exception.ResourceNotFoundException;
import com.petmatch.msreporttype.mapper.ReportTypeMapper;
import com.petmatch.msreporttype.model.ReportType;
import com.petmatch.msreporttype.repository.ReportTypeRepository;
import com.petmatch.msreporttype.service.ReportTypeService;
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
class ReportTypeServiceTest {

    @Mock ReportTypeRepository repo;
    @Mock ReportTypeMapper mapper;
    @InjectMocks ReportTypeService service;

    private ReportType entity;
    private ReportTypeResponseDTO responseDTO;
    private ReportTypeDTO dto;

    @BeforeEach
    void setUp() {
        entity      = ReportType.builder().idReportType(1L).name("PERDIDA").build();
        responseDTO = ReportTypeResponseDTO.builder().idReportType(1L).name("PERDIDA").build();
        dto         = ReportTypeDTO.builder().name("PERDIDA").build();
    }

    @Test
    void getAll_returnsAllTypes() {
        when(repo.findAll()).thenReturn(List.of(entity));
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        List<ReportTypeResponseDTO> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("PERDIDA");
    }

    @Test
    void getById_existingId_returnsDTO() {
        when(repo.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        ReportTypeResponseDTO result = service.getById(1L);

        assertThat(result.getIdReportType()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("PERDIDA");
    }

    @Test
    void getById_nonExistingId_throwsResourceNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_newType_savesAndReturnsDTO() {
        when(repo.findByNameIgnoreCase("PERDIDA")).thenReturn(Optional.empty());
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        ReportTypeResponseDTO result = service.save(dto);

        assertThat(result.getName()).isEqualTo("PERDIDA");
        verify(repo).save(entity);
    }

    @Test
    void save_duplicateName_throwsIllegalArgumentException() {
        when(repo.findByNameIgnoreCase("PERDIDA")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PERDIDA");

        verify(repo, never()).save(any());
    }

    @Test
    void update_existingId_updatesName() {
        ReportTypeDTO updateDto = ReportTypeDTO.builder().name("ENCONTRADA").build();
        when(repo.findById(1L)).thenReturn(Optional.of(entity));
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(
                ReportTypeResponseDTO.builder().idReportType(1L).name("ENCONTRADA").build());

        ReportTypeResponseDTO result = service.update(1L, updateDto);

        assertThat(result.getName()).isEqualTo("ENCONTRADA");
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
