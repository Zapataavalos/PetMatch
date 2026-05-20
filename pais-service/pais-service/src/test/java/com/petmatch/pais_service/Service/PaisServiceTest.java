package com.petmatch.pais_service.Service;

import com.petmatch.pais_service.Dto.PaisRequestDTO;
import com.petmatch.pais_service.Dto.PaisResponseDTO;
import com.petmatch.pais_service.Exception.BadRequestException;
import com.petmatch.pais_service.Exception.ResourceNotFoundException;
import com.petmatch.pais_service.Model.Pais;
import com.petmatch.pais_service.Repository.PaisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaisServiceTest {

    @Mock
    private PaisRepository paisRepository;

    @InjectMocks
    private PaisService paisService;

    @Test
    @DisplayName("Debe listar todos los países correctamente")
    void listarPaises_debeRetornarListaDePaises() {
        Pais chile = new Pais(1, "CHILE");
        Pais argentina = new Pais(2, "ARGENTINA");

        when(paisRepository.findAll()).thenReturn(List.of(chile, argentina));

        List<PaisResponseDTO> resultado = paisService.listarPaises();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nombrePais()).isEqualTo("CHILE");
        assertThat(resultado.get(1).nombrePais()).isEqualTo("ARGENTINA");

        verify(paisRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un país por ID correctamente")
    void buscarPaisPorId_cuandoExiste_debeRetornarPais() {
        Pais pais = new Pais(1, "CHILE");

        when(paisRepository.findById(1)).thenReturn(Optional.of(pais));

        PaisResponseDTO resultado = paisService.buscarPaisPorId(1);

        assertThat(resultado.idPais()).isEqualTo(1);
        assertThat(resultado.nombrePais()).isEqualTo("CHILE");

        verify(paisRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el país no existe")
    void buscarPaisPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(paisRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paisService.buscarPaisPorId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el país con ID: 99");

        verify(paisRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe crear un país correctamente")
    void crearPais_cuandoNombreNoExiste_debeCrearPais() {
        PaisRequestDTO requestDTO = new PaisRequestDTO("Chile");

        when(paisRepository.existsByNombrePaisIgnoreCase("CHILE")).thenReturn(false);

        when(paisRepository.save(any(Pais.class))).thenAnswer(invocation -> {
            Pais pais = invocation.getArgument(0);
            pais.setIdPais(1);
            return pais;
        });

        PaisResponseDTO resultado = paisService.crearPais(requestDTO);

        assertThat(resultado.idPais()).isEqualTo(1);
        assertThat(resultado.nombrePais()).isEqualTo("CHILE");

        verify(paisRepository, times(1)).existsByNombrePaisIgnoreCase("CHILE");
        verify(paisRepository, times(1)).save(any(Pais.class));
    }

    @Test
    @DisplayName("No debe crear un país duplicado")
    void crearPais_cuandoNombreExiste_debeLanzarBadRequest() {
        PaisRequestDTO requestDTO = new PaisRequestDTO("Chile");

        when(paisRepository.existsByNombrePaisIgnoreCase("CHILE")).thenReturn(true);

        assertThatThrownBy(() -> paisService.crearPais(requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe un país registrado");

        verify(paisRepository, times(1)).existsByNombrePaisIgnoreCase("CHILE");
        verify(paisRepository, never()).save(any(Pais.class));
    }

    @Test
    @DisplayName("Debe actualizar un país correctamente")
    void actualizarPais_cuandoExisteYNoEstaDuplicado_debeActualizarPais() {
        Pais paisExistente = new Pais(1, "CHILE");
        PaisRequestDTO requestDTO = new PaisRequestDTO("Argentina");

        when(paisRepository.findById(1)).thenReturn(Optional.of(paisExistente));
        when(paisRepository.existsByNombrePaisIgnoreCaseAndIdPaisNot("ARGENTINA", 1)).thenReturn(false);
        when(paisRepository.save(any(Pais.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaisResponseDTO resultado = paisService.actualizarPais(1, requestDTO);

        assertThat(resultado.idPais()).isEqualTo(1);
        assertThat(resultado.nombrePais()).isEqualTo("ARGENTINA");

        verify(paisRepository, times(1)).findById(1);
        verify(paisRepository, times(1)).save(any(Pais.class));
    }

    @Test
    @DisplayName("No debe actualizar un país si el nuevo nombre ya existe")
    void actualizarPais_cuandoNombreDuplicado_debeLanzarBadRequest() {
        Pais paisExistente = new Pais(1, "CHILE");
        PaisRequestDTO requestDTO = new PaisRequestDTO("Argentina");

        when(paisRepository.findById(1)).thenReturn(Optional.of(paisExistente));
        when(paisRepository.existsByNombrePaisIgnoreCaseAndIdPaisNot("ARGENTINA", 1)).thenReturn(true);

        assertThatThrownBy(() -> paisService.actualizarPais(1, requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe otro país registrado");

        verify(paisRepository, never()).save(any(Pais.class));
    }

    @Test
    @DisplayName("Debe eliminar un país correctamente")
    void eliminarPais_cuandoExiste_debeEliminarPais() {
        Pais pais = new Pais(1, "CHILE");

        when(paisRepository.findById(1)).thenReturn(Optional.of(pais));

        paisService.eliminarPais(1);

        verify(paisRepository, times(1)).findById(1);
        verify(paisRepository, times(1)).delete(pais);
    }
}