package com.petmatch.msrace;

import com.petmatch.msrace.client.AnimalTypeClient;
import com.petmatch.msrace.dto.AnimalTypeResponseDTO;
import com.petmatch.msrace.dto.RaceDTO;
import com.petmatch.msrace.exception.ResourceNotFoundException;
import com.petmatch.msrace.mapper.RaceMapper;
import com.petmatch.msrace.model.Race;
import com.petmatch.msrace.repository.RaceRepository;
import com.petmatch.msrace.service.RaceService;
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
class RaceServiceTest {

    @Mock RaceRepository raceRepository;
    @Mock RaceMapper raceMapper;
    @Mock AnimalTypeClient animalTypeClient;
    @InjectMocks RaceService service;

    private Race entity;
    private RaceDTO dto;

    @BeforeEach
    void setUp() {
        entity = Race.builder().idRace(1L).name("Labrador").idAnimalType(1L).build();
        dto    = RaceDTO.builder().name("Labrador").idAnimalType(1L).build();
    }

    @Test
    void getAll_returnsAllRaces() {
        when(raceRepository.findAll()).thenReturn(List.of(entity));
        when(raceMapper.toDTO(entity)).thenReturn(dto);

        List<RaceDTO> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Labrador");
    }

    @Test
    void getById_existingId_returnsDTO() {
        when(raceRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(raceMapper.toDTO(entity)).thenReturn(dto);

        RaceDTO result = service.getById(1L);

        assertThat(result.getName()).isEqualTo("Labrador");
    }

    @Test
    void getById_nonExistingId_throwsResourceNotFoundException() {
        when(raceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByAnimalType_returnsFilteredRaces() {
        when(raceRepository.findByIdAnimalType(1L)).thenReturn(List.of(entity));
        when(raceMapper.toDTO(entity)).thenReturn(dto);

        List<RaceDTO> result = service.getByAnimalType(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdAnimalType()).isEqualTo(1L);
    }

    @Test
    void save_validDTO_savesAndReturnsDTO() {
        when(animalTypeClient.getById(1L)).thenReturn(new AnimalTypeResponseDTO(1L, "Perro"));
        when(raceMapper.toEntity(dto)).thenReturn(entity);
        when(raceRepository.save(entity)).thenReturn(entity);
        when(raceMapper.toDTO(entity)).thenReturn(dto);

        RaceDTO result = service.save(dto);

        assertThat(result.getName()).isEqualTo("Labrador");
        verify(raceRepository).save(entity);
    }

    @Test
    void save_animalTypeNotFound_throwsResourceNotFoundException() {
        when(animalTypeClient.getById(1L)).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("1");

        verify(raceRepository, never()).save(any());
    }

    @Test
    void save_animalTypeServiceDown_throwsRuntimeException() {
        when(animalTypeClient.getById(1L)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ms-animal-type");
    }

    @Test
    void update_existingId_updatesAndReturnsDTO() {
        RaceDTO updateDto = RaceDTO.builder().name("Poodle").idAnimalType(1L).build();
        Race updatedEntity = Race.builder().idRace(1L).name("Poodle").idAnimalType(1L).build();

        when(raceRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(animalTypeClient.getById(1L)).thenReturn(new AnimalTypeResponseDTO(1L, "Perro"));
        when(raceMapper.toEntity(updateDto)).thenReturn(updatedEntity);
        when(raceRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(raceMapper.toDTO(updatedEntity)).thenReturn(updateDto);

        RaceDTO result = service.update(1L, updateDto);

        assertThat(result.getName()).isEqualTo("Poodle");
        verify(raceRepository).save(updatedEntity);
    }

    @Test
    void update_nonExistingId_throwsResourceNotFoundException() {
        when(raceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(raceRepository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(raceRepository).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(raceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(raceRepository, never()).deleteById(any());
    }
}
