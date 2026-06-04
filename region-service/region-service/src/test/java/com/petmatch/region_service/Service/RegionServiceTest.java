package com.petmatch.region_service.Service;

import com.petmatch.region_service.DTO.RegionRequestDto;
import com.petmatch.region_service.DTO.RegionResponseDto;
import com.petmatch.region_service.Event.RegionEventPublisher;
import com.petmatch.region_service.Exception.BadRequestException;
import com.petmatch.region_service.Exception.ResourceNotFoundException;
import com.petmatch.region_service.Model.Region;
import com.petmatch.region_service.Repository.PaisReferenciaRepository;
import com.petmatch.region_service.Repository.RegionRepository;
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
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private PaisReferenciaRepository paisReferenciaRepository;

    @Mock
    private RegionEventPublisher regionEventPublisher;

    @InjectMocks
    private RegionService regionService;

    @Test
    @DisplayName("Debe listar todas las regiones correctamente")
    void listarRegiones_debeRetornarListaDeRegiones() {
        Region metropolitana = new Region(1, "METROPOLITANA", 1);
        Region valparaiso = new Region(2, "VALPARAISO", 1);

        when(regionRepository.findAll()).thenReturn(List.of(metropolitana, valparaiso));

        List<RegionResponseDto> resultado = regionService.listarRegiones();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nombreRegion()).isEqualTo("METROPOLITANA");
        assertThat(resultado.get(1).nombreRegion()).isEqualTo("VALPARAISO");

        verify(regionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe listar regiones por país si el país existe y está activo")
    void listarRegionesPorPais_cuandoPaisExiste_debeRetornarRegiones() {
        Integer idPais = 1;

        Region metropolitana = new Region(1, "METROPOLITANA", idPais);
        Region valparaiso = new Region(2, "VALPARAISO", idPais);

        when(paisReferenciaRepository.existsByIdPaisAndActivoTrue(idPais)).thenReturn(true);
        when(regionRepository.findByIdPais(idPais)).thenReturn(List.of(metropolitana, valparaiso));

        List<RegionResponseDto> resultado = regionService.listarRegionesPorPais(idPais);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).idPais()).isEqualTo(idPais);
        assertThat(resultado.get(1).idPais()).isEqualTo(idPais);

        verify(paisReferenciaRepository, times(1)).existsByIdPaisAndActivoTrue(idPais);
        verify(regionRepository, times(1)).findByIdPais(idPais);
    }

    @Test
    @DisplayName("No debe listar regiones por país si el país no existe o está inactivo")
    void listarRegionesPorPais_cuandoPaisNoExiste_debeLanzarExcepcion() {
        Integer idPais = 99;

        when(paisReferenciaRepository.existsByIdPaisAndActivoTrue(idPais)).thenReturn(false);

        assertThatThrownBy(() -> regionService.listarRegionesPorPais(idPais))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No existe un país activo registrado con ID: 99");

        verify(paisReferenciaRepository, times(1)).existsByIdPaisAndActivoTrue(idPais);
        verify(regionRepository, never()).findByIdPais(any());
    }

    @Test
    @DisplayName("Debe buscar una región por ID correctamente")
    void buscarRegionPorId_cuandoExiste_debeRetornarRegion() {
        Region region = new Region(1, "METROPOLITANA", 1);

        when(regionRepository.findById(1)).thenReturn(Optional.of(region));

        RegionResponseDto resultado = regionService.buscarRegionPorId(1);

        assertThat(resultado.idRegion()).isEqualTo(1);
        assertThat(resultado.nombreRegion()).isEqualTo("METROPOLITANA");
        assertThat(resultado.idPais()).isEqualTo(1);

        verify(regionRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la región no existe")
    void buscarRegionPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(regionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> regionService.buscarRegionPorId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró la región con ID: 99");

        verify(regionRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe crear una región correctamente si el país existe")
    void crearRegion_cuandoPaisExisteYNoHayDuplicado_debeCrearRegion() {
        RegionRequestDto requestDTO = new RegionRequestDto("Metropolitana", 1);

        when(paisReferenciaRepository.existsByIdPaisAndActivoTrue(1)).thenReturn(true);
        when(regionRepository.existsByNombreRegionIgnoreCaseAndIdPais("METROPOLITANA", 1)).thenReturn(false);

        when(regionRepository.save(any(Region.class))).thenAnswer(invocation -> {
            Region region = invocation.getArgument(0);
            region.setIdRegion(1);
            return region;
        });

        RegionResponseDto resultado = regionService.crearRegion(requestDTO);

        assertThat(resultado.idRegion()).isEqualTo(1);
        assertThat(resultado.nombreRegion()).isEqualTo("METROPOLITANA");
        assertThat(resultado.idPais()).isEqualTo(1);

        verify(paisReferenciaRepository, times(1)).existsByIdPaisAndActivoTrue(1);
        verify(regionRepository, times(1)).existsByNombreRegionIgnoreCaseAndIdPais("METROPOLITANA", 1);
        verify(regionRepository, times(1)).save(any(Region.class));
        verify(regionEventPublisher, times(1)).publicarRegionCreada(any(Region.class));
    }

    @Test
    @DisplayName("No debe crear región si el país no existe o está inactivo")
    void crearRegion_cuandoPaisNoExiste_debeLanzarResourceNotFound() {
        RegionRequestDto requestDTO = new RegionRequestDto("Metropolitana", 99);

        when(paisReferenciaRepository.existsByIdPaisAndActivoTrue(99)).thenReturn(false);

        assertThatThrownBy(() -> regionService.crearRegion(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No existe un país activo registrado con ID: 99");

        verify(regionRepository, never()).save(any(Region.class));
    }

    @Test
    @DisplayName("No debe crear región duplicada para el mismo país")
    void crearRegion_cuandoRegionDuplicada_debeLanzarBadRequest() {
        RegionRequestDto requestDTO = new RegionRequestDto("Metropolitana", 1);

        when(paisReferenciaRepository.existsByIdPaisAndActivoTrue(1)).thenReturn(true);
        when(regionRepository.existsByNombreRegionIgnoreCaseAndIdPais("METROPOLITANA", 1)).thenReturn(true);

        assertThatThrownBy(() -> regionService.crearRegion(requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe una región registrada");

        verify(regionRepository, never()).save(any(Region.class));
    }

    @Test
    @DisplayName("Debe actualizar una región correctamente")
    void actualizarRegion_cuandoExisteYNoEstaDuplicada_debeActualizarRegion() {
        Region regionExistente = new Region(1, "METROPOLITANA", 1);
        RegionRequestDto requestDTO = new RegionRequestDto("Valparaiso", 1);

        when(regionRepository.findById(1)).thenReturn(Optional.of(regionExistente));
        when(paisReferenciaRepository.existsByIdPaisAndActivoTrue(1)).thenReturn(true);
        when(regionRepository.existsByNombreRegionIgnoreCaseAndIdPaisAndIdRegionNot("VALPARAISO", 1, 1)).thenReturn(false);
        when(regionRepository.save(any(Region.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegionResponseDto resultado = regionService.actualizarRegion(1, requestDTO);

        assertThat(resultado.idRegion()).isEqualTo(1);
        assertThat(resultado.nombreRegion()).isEqualTo("VALPARAISO");
        assertThat(resultado.idPais()).isEqualTo(1);

        verify(regionRepository, times(1)).findById(1);
        verify(regionRepository, times(1)).save(any(Region.class));
        verify(regionEventPublisher, times(1)).publicarRegionActualizada(regionExistente);
    }

    @Test
    @DisplayName("No debe actualizar una región si el nuevo nombre ya existe en el país")
    void actualizarRegion_cuandoDuplicada_debeLanzarBadRequest() {
        Region regionExistente = new Region(1, "METROPOLITANA", 1);
        RegionRequestDto requestDTO = new RegionRequestDto("Valparaiso", 1);

        when(regionRepository.findById(1)).thenReturn(Optional.of(regionExistente));
        when(paisReferenciaRepository.existsByIdPaisAndActivoTrue(1)).thenReturn(true);
        when(regionRepository.existsByNombreRegionIgnoreCaseAndIdPaisAndIdRegionNot("VALPARAISO", 1, 1)).thenReturn(true);

        assertThatThrownBy(() -> regionService.actualizarRegion(1, requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe otra región registrada");

        verify(regionRepository, never()).save(any(Region.class));
    }

    @Test
    @DisplayName("Debe eliminar una región correctamente")
    void eliminarRegion_cuandoExiste_debeEliminarRegion() {
        Region region = new Region(1, "METROPOLITANA", 1);

        when(regionRepository.findById(1)).thenReturn(Optional.of(region));

        regionService.eliminarRegion(1);

        verify(regionRepository, times(1)).findById(1);
        verify(regionRepository, times(1)).delete(region);
        verify(regionEventPublisher, times(1)).publicarRegionEliminada(region);
    }
}
