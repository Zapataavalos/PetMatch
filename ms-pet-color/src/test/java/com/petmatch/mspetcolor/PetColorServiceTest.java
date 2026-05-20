package com.petmatch.mspetcolor;

import com.petmatch.mspetcolor.client.PetClient;
import com.petmatch.mspetcolor.dto.PetColorDTO;
import com.petmatch.mspetcolor.dto.PetColorResponseDTO;
import com.petmatch.mspetcolor.dto.PetResponseDTO;
import com.petmatch.mspetcolor.exception.ResourceNotFoundException;
import com.petmatch.mspetcolor.mapper.PetColorMapper;
import com.petmatch.mspetcolor.model.Color;
import com.petmatch.mspetcolor.model.PetColor;
import com.petmatch.mspetcolor.repository.ColorRepository;
import com.petmatch.mspetcolor.repository.PetColorRepository;
import com.petmatch.mspetcolor.service.PetColorService;
import feign.FeignException;
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
class PetColorServiceTest {

    @Mock PetColorRepository petColorRepository;
    @Mock ColorRepository colorRepository;
    @Mock PetColorMapper petColorMapper;
    @Mock PetClient petClient;
    @InjectMocks PetColorService service;

    private PetColor petColor;
    private Color color;
    private PetColorDTO dto;
    private PetColorResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        color      = Color.builder().idColor(1L).name("Negro").build();
        petColor   = PetColor.builder().idPetColor(1L).idPet(10L).idColor(1L).build();
        dto        = PetColorDTO.builder().idPet(10L).idColor(1L).build();
        responseDTO = PetColorResponseDTO.builder().idPetColor(1L).idPet(10L).idColor(1L).colorName("Negro").build();
    }

    @Test
    void getAll_returnsMappedList() {
        when(petColorRepository.findAll()).thenReturn(List.of(petColor));
        when(colorRepository.findById(1L)).thenReturn(Optional.of(color));
        when(petColorMapper.toDTO(petColor, color)).thenReturn(responseDTO);

        List<PetColorResponseDTO> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getColorName()).isEqualTo("Negro");
    }

    @Test
    void getByPet_returnsPetColors() {
        when(petColorRepository.findByIdPet(10L)).thenReturn(List.of(petColor));
        when(colorRepository.findById(1L)).thenReturn(Optional.of(color));
        when(petColorMapper.toDTO(petColor, color)).thenReturn(responseDTO);

        List<PetColorResponseDTO> result = service.getByPet(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdPet()).isEqualTo(10L);
    }

    @Test
    void getById_existingId_returnsDTO() {
        when(petColorRepository.findById(1L)).thenReturn(Optional.of(petColor));
        when(colorRepository.findById(1L)).thenReturn(Optional.of(color));
        when(petColorMapper.toDTO(petColor, color)).thenReturn(responseDTO);

        PetColorResponseDTO result = service.getById(1L);

        assertThat(result.getIdPetColor()).isEqualTo(1L);
    }

    @Test
    void getById_nonExistingId_throwsResourceNotFoundException() {
        when(petColorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_validDTO_savesAndReturnsDTO() {
        when(petClient.getById(10L)).thenReturn(new PetResponseDTO(10L, "Firulais"));
        when(colorRepository.findById(1L)).thenReturn(Optional.of(color));
        when(petColorRepository.existsByIdPetAndIdColor(10L, 1L)).thenReturn(false);
        when(petColorMapper.toEntity(dto)).thenReturn(petColor);
        when(petColorRepository.save(petColor)).thenReturn(petColor);
        when(colorRepository.findById(1L)).thenReturn(Optional.of(color));
        when(petColorMapper.toDTO(petColor, color)).thenReturn(responseDTO);

        PetColorResponseDTO result = service.save(dto);

        assertThat(result.getIdPet()).isEqualTo(10L);
        verify(petColorRepository).save(petColor);
    }

    @Test
    void save_petNotFound_throwsResourceNotFoundException() {
        when(petClient.getById(10L)).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("10");

        verify(petColorRepository, never()).save(any());
    }

    @Test
    void save_colorNotFound_throwsResourceNotFoundException() {
        when(petClient.getById(10L)).thenReturn(new PetResponseDTO(10L, "Firulais"));
        when(colorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("1");

        verify(petColorRepository, never()).save(any());
    }

    @Test
    void save_duplicateAssignment_throwsIllegalArgumentException() {
        when(petClient.getById(10L)).thenReturn(new PetResponseDTO(10L, "Firulais"));
        when(colorRepository.findById(1L)).thenReturn(Optional.of(color));
        when(petColorRepository.existsByIdPetAndIdColor(10L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya tiene ese color");

        verify(petColorRepository, never()).save(any());
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(petColorRepository.findById(1L)).thenReturn(Optional.of(petColor));

        service.delete(1L);

        verify(petColorRepository).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(petColorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(petColorRepository, never()).deleteById(any());
    }
}
