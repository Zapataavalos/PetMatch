package com.petmatch.mspet;

import com.petmatch.mspet.dto.PetDTO;
import com.petmatch.mspet.exception.ResourceNotFoundException;
import com.petmatch.mspet.mapper.PetMapper;
import com.petmatch.mspet.model.Pet;
import com.petmatch.mspet.repository.PetRepository;
import com.petmatch.mspet.service.PetService;
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
class PetServiceTest {

    @Mock PetRepository repository;
    @Mock PetMapper mapper;
    @InjectMocks PetService service;

    private Pet entity;
    private PetDTO dto;

    @BeforeEach
    void setUp() {
        entity = Pet.builder().idPet(1L).name("Firulais").idUser(10L).idRace(2L).idSize(1L).build();
        dto    = PetDTO.builder().name("Firulais").idUser(10L).idRace(2L).idSize(1L).build();
    }

    @Test
    void getAll_returnsAllPets() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);

        List<PetDTO> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Firulais");
    }

    @Test
    void getById_existingId_returnsDTO() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);

        PetDTO result = service.getById(1L);

        assertThat(result.getName()).isEqualTo("Firulais");
    }

    @Test
    void getById_nonExistingId_throwsResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByUser_returnsOnlyUserPets() {
        when(repository.findByIdUser(10L)).thenReturn(List.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);

        List<PetDTO> result = service.getByUser(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdUser()).isEqualTo(10L);
    }

    @Test
    void save_validDTO_savesAndReturnsDTO() {
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(dto);

        PetDTO result = service.save(dto);

        assertThat(result.getName()).isEqualTo("Firulais");
        verify(repository).save(entity);
    }

    @Test
    void update_existingId_updatesAndReturnsDTO() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDTO(entity)).thenReturn(dto);

        PetDTO result = service.update(1L, dto);

        assertThat(result).isNotNull();
        verify(repository).save(entity);
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
