package com.petmatch.msanimaltype;

import com.petmatch.msanimaltype.dto.AnimalTypeDTO;
import com.petmatch.msanimaltype.dto.AnimalTypeResponseDTO;
import com.petmatch.msanimaltype.exception.ResourceNotFoundException;
import com.petmatch.msanimaltype.mapper.AnimalTypeMapper;
import com.petmatch.msanimaltype.model.AnimalType;
import com.petmatch.msanimaltype.repository.AnimalTypeRepository;
import com.petmatch.msanimaltype.service.AnimalTypeService;
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
class AnimalTypeServiceTest {

    @Mock AnimalTypeRepository repository;
    @Mock AnimalTypeMapper mapper;
    @InjectMocks AnimalTypeService service;

    private AnimalType entity;
    private AnimalTypeResponseDTO responseDTO;
    private AnimalTypeDTO dto;

    @BeforeEach
    void setUp() {
        entity      = AnimalType.builder().idAnimalType(1L).name("Perro").build();
        responseDTO = AnimalTypeResponseDTO.builder().idAnimalType(1L).name("Perro").build();
        dto         = AnimalTypeDTO.builder().name("Perro").build();
    }

    @Test
    void getAll_returnsListOfDTO() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        List<AnimalTypeResponseDTO> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Perro");
    }

    @Test
    void getById_existingId_returnsDTO() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        AnimalTypeResponseDTO result = service.getById(1L);

        assertThat(result.getIdAnimalType()).isEqualTo(1L);
    }

    @Test
    void getById_nonExistingId_throwsResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_newName_savesAndReturnsDTO() {
        when(repository.findByNameIgnoreCase("Perro")).thenReturn(Optional.empty());
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(responseDTO);

        AnimalTypeResponseDTO result = service.save(dto);

        assertThat(result.getName()).isEqualTo("Perro");
        verify(repository).save(entity);
    }

    @Test
    void save_duplicateName_throwsIllegalArgumentException() {
        when(repository.findByNameIgnoreCase("Perro")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Perro");
    }

    @Test
    void update_existingId_updatesAndReturnsDTO() {
        AnimalTypeDTO updateDto = AnimalTypeDTO.builder().name("Gato").build();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(
                AnimalTypeResponseDTO.builder().idAnimalType(1L).name("Gato").build());

        AnimalTypeResponseDTO result = service.update(1L, updateDto);

        assertThat(result.getName()).isEqualTo("Gato");
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
