package com.petmatch.mspetcolor;

import com.petmatch.mspetcolor.dto.ColorDTO;
import com.petmatch.mspetcolor.dto.ColorResponseDTO;
import com.petmatch.mspetcolor.exception.ResourceNotFoundException;
import com.petmatch.mspetcolor.mapper.ColorMapper;
import com.petmatch.mspetcolor.model.Color;
import com.petmatch.mspetcolor.repository.ColorRepository;
import com.petmatch.mspetcolor.service.ColorService;
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
class ColorServiceTest {

    @Mock ColorRepository colorRepository;
    @Mock ColorMapper colorMapper;
    @InjectMocks ColorService service;

    private Color entity;
    private ColorResponseDTO responseDTO;
    private ColorDTO dto;

    @BeforeEach
    void setUp() {
        entity      = Color.builder().idColor(1L).name("Negro").build();
        responseDTO = ColorResponseDTO.builder().idColor(1L).name("Negro").build();
        dto         = ColorDTO.builder().name("Negro").build();
    }

    @Test
    void getAll_returnsListOfColors() {
        when(colorRepository.findAll()).thenReturn(List.of(entity));
        when(colorMapper.toDTO(entity)).thenReturn(responseDTO);

        List<ColorResponseDTO> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Negro");
    }

    @Test
    void getById_existingId_returnsDTO() {
        when(colorRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(colorMapper.toDTO(entity)).thenReturn(responseDTO);

        ColorResponseDTO result = service.getById(1L);

        assertThat(result.getIdColor()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Negro");
    }

    @Test
    void getById_nonExistingId_throwsResourceNotFoundException() {
        when(colorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_newColor_savesAndReturnsDTO() {
        when(colorRepository.findByNameIgnoreCase("Negro")).thenReturn(Optional.empty());
        when(colorMapper.toEntity(dto)).thenReturn(entity);
        when(colorRepository.save(entity)).thenReturn(entity);
        when(colorMapper.toDTO(entity)).thenReturn(responseDTO);

        ColorResponseDTO result = service.save(dto);

        assertThat(result.getName()).isEqualTo("Negro");
        verify(colorRepository).save(entity);
    }

    @Test
    void save_duplicateName_throwsIllegalArgumentException() {
        when(colorRepository.findByNameIgnoreCase("Negro")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Negro");

        verify(colorRepository, never()).save(any());
    }

    @Test
    void update_existingId_updatesName() {
        ColorDTO updateDto = ColorDTO.builder().name("Blanco").build();
        when(colorRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(colorRepository.save(entity)).thenReturn(entity);
        when(colorMapper.toDTO(entity)).thenReturn(
                ColorResponseDTO.builder().idColor(1L).name("Blanco").build());

        ColorResponseDTO result = service.update(1L, updateDto);

        assertThat(result.getName()).isEqualTo("Blanco");
        verify(colorRepository).save(entity);
    }

    @Test
    void update_nonExistingId_throwsResourceNotFoundException() {
        when(colorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(colorRepository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(colorRepository).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(colorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(colorRepository, never()).deleteById(any());
    }
}
