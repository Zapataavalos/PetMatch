package com.petmatch.ciudad_service.Service;

import com.petmatch.ciudad_service.DTO.CiudadRequestDTO;
import com.petmatch.ciudad_service.DTO.CiudadResponseDTO;
import com.petmatch.ciudad_service.Event.CiudadEventPublisher;
import com.petmatch.ciudad_service.Exception.BadRequestException;
import com.petmatch.ciudad_service.Exception.ResourceNotFoundException;
import com.petmatch.ciudad_service.Model.Ciudad;
import com.petmatch.ciudad_service.Repository.CiudadRepository;
import com.petmatch.ciudad_service.Repository.RegionReferenciaRepository;
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
class CiudadServiceTest {

    @Mock
    private CiudadRepository ciudadRepository;

    @Mock
    private RegionReferenciaRepository regionReferenciaRepository;

    @Mock
    private CiudadEventPublisher ciudadEventPublisher;

    @InjectMocks
    private CiudadService ciudadService;

    @Test
    @DisplayName("Debe listar todas las ciudades correctamente")
    void listarCiudades_debeRetornarListaDeCiudades() {
        Ciudad santiago = new Ciudad(1, "SANTIAGO", 1);
        Ciudad providencia = new Ciudad(2, "PROVIDENCIA", 1);

        when(ciudadRepository.findAll()).thenReturn(List.of(santiago, providencia));

        List<CiudadResponseDTO> resultado = ciudadService.listarCiudades();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nombreCiudad()).isEqualTo("SANTIAGO");
        assertThat(resultado.get(1).nombreCiudad()).isEqualTo("PROVIDENCIA");

        verify(ciudadRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe listar ciudades por región si la región existe y está activa")
    void listarCiudadesPorRegion_cuandoRegionExiste_debeRetornarCiudades() {
        Integer idRegion = 1;

        Ciudad santiago = new Ciudad(1, "SANTIAGO", idRegion);
        Ciudad providencia = new Ciudad(2, "PROVIDENCIA", idRegion);

        when(regionReferenciaRepository.existsByIdRegionAndActivoTrue(idRegion)).thenReturn(true);
        when(ciudadRepository.findByIdRegion(idRegion)).thenReturn(List.of(santiago, providencia));

        List<CiudadResponseDTO> resultado = ciudadService.listarCiudadesPorRegion(idRegion);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).idRegion()).isEqualTo(idRegion);
        assertThat(resultado.get(1).idRegion()).isEqualTo(idRegion);

        verify(regionReferenciaRepository, times(1)).existsByIdRegionAndActivoTrue(idRegion);
        verify(ciudadRepository, times(1)).findByIdRegion(idRegion);
    }

    @Test
    @DisplayName("No debe listar ciudades por región si la región no existe o está inactiva")
    void listarCiudadesPorRegion_cuandoRegionNoExiste_debeLanzarExcepcion() {
        Integer idRegion = 99;

        when(regionReferenciaRepository.existsByIdRegionAndActivoTrue(idRegion)).thenReturn(false);

        assertThatThrownBy(() -> ciudadService.listarCiudadesPorRegion(idRegion))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No existe una región activa registrada con ID: 99");

        verify(regionReferenciaRepository, times(1)).existsByIdRegionAndActivoTrue(idRegion);
        verify(ciudadRepository, never()).findByIdRegion(any());
    }

    @Test
    @DisplayName("Debe buscar una ciudad por ID correctamente")
    void buscarCiudadPorId_cuandoExiste_debeRetornarCiudad() {
        Ciudad ciudad = new Ciudad(1, "SANTIAGO", 1);

        when(ciudadRepository.findById(1)).thenReturn(Optional.of(ciudad));

        CiudadResponseDTO resultado = ciudadService.buscarCiudadPorId(1);

        assertThat(resultado.idCiudad()).isEqualTo(1);
        assertThat(resultado.nombreCiudad()).isEqualTo("SANTIAGO");
        assertThat(resultado.idRegion()).isEqualTo(1);

        verify(ciudadRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la ciudad no existe")
    void buscarCiudadPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(ciudadRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ciudadService.buscarCiudadPorId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró la ciudad con ID: 99");

        verify(ciudadRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe crear una ciudad correctamente si la regiÃ³n existe")
    void crearCiudad_cuandoRegionExisteYNoHayDuplicado_debeCrearCiudad() {
        CiudadRequestDTO requestDTO = new CiudadRequestDTO("Santiago", 1);

        when(regionReferenciaRepository.existsByIdRegionAndActivoTrue(1)).thenReturn(true);
        when(ciudadRepository.existsByNombreCiudadIgnoreCaseAndIdRegion("SANTIAGO", 1)).thenReturn(false);
        when(ciudadRepository.save(any(Ciudad.class))).thenAnswer(invocation -> {
            Ciudad ciudad = invocation.getArgument(0);
            ciudad.setIdCiudad(1);
            return ciudad;
        });

        CiudadResponseDTO resultado = ciudadService.crearCiudad(requestDTO);

        assertThat(resultado.idCiudad()).isEqualTo(1);
        assertThat(resultado.nombreCiudad()).isEqualTo("SANTIAGO");
        assertThat(resultado.idRegion()).isEqualTo(1);

        verify(regionReferenciaRepository, times(1)).existsByIdRegionAndActivoTrue(1);
        verify(ciudadRepository, times(1)).existsByNombreCiudadIgnoreCaseAndIdRegion("SANTIAGO", 1);
        verify(ciudadRepository, times(1)).save(any(Ciudad.class));
        verify(ciudadEventPublisher, times(1)).publicarCiudadCreada(any(Ciudad.class));
    }


    @Test
    @DisplayName("No debe crear ciudad si la región no existe o está inactiva")
    void crearCiudad_cuandoRegionNoExiste_debeLanzarResourceNotFound() {
        CiudadRequestDTO requestDTO = new CiudadRequestDTO("Santiago", 99);

        when(regionReferenciaRepository.existsByIdRegionAndActivoTrue(99)).thenReturn(false);

        assertThatThrownBy(() -> ciudadService.crearCiudad(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No existe una región activa registrada con ID: 99");

        verify(ciudadRepository, never()).save(any(Ciudad.class));
    }

    @Test
    @DisplayName("No debe crear ciudad duplicada para la misma región")
    void crearCiudad_cuandoCiudadDuplicada_debeLanzarBadRequest() {
        CiudadRequestDTO requestDTO = new CiudadRequestDTO("Santiago", 1);

        when(regionReferenciaRepository.existsByIdRegionAndActivoTrue(1)).thenReturn(true);
        when(ciudadRepository.existsByNombreCiudadIgnoreCaseAndIdRegion("SANTIAGO", 1)).thenReturn(true);

        assertThatThrownBy(() -> ciudadService.crearCiudad(requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe una ciudad registrada");

        verify(ciudadRepository, never()).save(any(Ciudad.class));
    }

    @Test
    @DisplayName("Debe actualizar una ciudad correctamente")
    void actualizarCiudad_cuandoExisteYNoEstaDuplicada_debeActualizarCiudad() {
        Ciudad ciudadExistente = new Ciudad(1, "SANTIAGO", 1);
        CiudadRequestDTO requestDTO = new CiudadRequestDTO("Providencia", 1);

        when(ciudadRepository.findById(1)).thenReturn(Optional.of(ciudadExistente));
        when(regionReferenciaRepository.existsByIdRegionAndActivoTrue(1)).thenReturn(true);
        when(ciudadRepository.existsByNombreCiudadIgnoreCaseAndIdRegionAndIdCiudadNot("PROVIDENCIA", 1, 1)).thenReturn(false);
        when(ciudadRepository.save(any(Ciudad.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CiudadResponseDTO resultado = ciudadService.actualizarCiudad(1, requestDTO);

        assertThat(resultado.idCiudad()).isEqualTo(1);
        assertThat(resultado.nombreCiudad()).isEqualTo("PROVIDENCIA");
        assertThat(resultado.idRegion()).isEqualTo(1);

        verify(ciudadRepository, times(1)).findById(1);
        verify(ciudadRepository, times(1)).save(any(Ciudad.class));
        verify(ciudadEventPublisher, times(1)).publicarCiudadActualizada(ciudadExistente);
    }


    @Test
    @DisplayName("No debe actualizar una ciudad si el nuevo nombre ya existe en la región")
    void actualizarCiudad_cuandoDuplicada_debeLanzarBadRequest() {
        Ciudad ciudadExistente = new Ciudad(1, "SANTIAGO", 1);
        CiudadRequestDTO requestDTO = new CiudadRequestDTO("Providencia", 1);

        when(ciudadRepository.findById(1)).thenReturn(Optional.of(ciudadExistente));
        when(regionReferenciaRepository.existsByIdRegionAndActivoTrue(1)).thenReturn(true);
        when(ciudadRepository.existsByNombreCiudadIgnoreCaseAndIdRegionAndIdCiudadNot("PROVIDENCIA", 1, 1)).thenReturn(true);

        assertThatThrownBy(() -> ciudadService.actualizarCiudad(1, requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe otra ciudad registrada");

        verify(ciudadRepository, never()).save(any(Ciudad.class));
    }

    @Test
    @DisplayName("Debe eliminar una ciudad correctamente")
    void eliminarCiudad_cuandoExiste_debeEliminarCiudad() {
        Ciudad ciudad = new Ciudad(1, "SANTIAGO", 1);

        when(ciudadRepository.findById(1)).thenReturn(Optional.of(ciudad));

        ciudadService.eliminarCiudad(1);

        verify(ciudadRepository, times(1)).findById(1);
        verify(ciudadRepository, times(1)).delete(ciudad);
        verify(ciudadEventPublisher, times(1)).publicarCiudadEliminada(ciudad);
    }
}

   
