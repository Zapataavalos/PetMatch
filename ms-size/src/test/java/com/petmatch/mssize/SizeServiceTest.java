package com.petmatch.mssize;

import com.petmatch.mssize.dto.SizeDTO;
import com.petmatch.mssize.dto.SizeResponseDTO;
import com.petmatch.mssize.exception.ResourceNotFoundException;
import com.petmatch.mssize.mapper.SizeMapper;
import com.petmatch.mssize.model.Size;
import com.petmatch.mssize.repository.SizeRepository;
import com.petmatch.mssize.service.SizeService;
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
class SizeServiceTest {

    @Mock SizeRepository repository;
    @Mock SizeMapper mapper;
    @InjectMocks SizeService service;

    private Size entity;
    private SizeResponseDTO responseDTO;
    private SizeDTO dto;

    @BeforeEach
    void setUp() {
        entity      = Size.builder().idSize(1L).name("Grande").build();
        responseDTO = SizeResponseDTO.builder().idSize(1L).name("Grande").build();
        dto         = SizeDTO.builder().name("Grande").build();
    }

    @Test
    void getAll_returnsList() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        List<SizeResponseDTO> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Grande");
    }

    @Test
    void getById_existingId_returnsDTO() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        SizeResponseDTO result = service.getById(1L);

        assertThat(result.getIdSize()).isEqualTo(1L);
    }

    @Test
    void getById_nonExistingId_throwsResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_newName_savesSuccessfully() {
        when(repository.findByNameIgnoreCase("Grande")).thenReturn(Optional.empty());
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        SizeResponseDTO result = service.save(dto);

        assertThat(result.getName()).isEqualTo("Grande");
    }

    @Test
    void save_duplicateName_throwsIllegalArgumentException() {
        when(repository.findByNameIgnoreCase("Grande")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Grande");
    }

    @Test
    void update_existingId_updatesName() {
        SizeDTO updateDto = SizeDTO.builder().name("Pequeño").build();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(
                SizeResponseDTO.builder().idSize(1L).name("Pequeño").build());

        SizeResponseDTO result = service.update(1L, updateDto);

        assertThat(result.getName()).isEqualTo("Pequeño");
    }

    @Test
    void update_nonExistingId_throwsResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }
}
