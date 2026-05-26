package com.petmatch.ubicacion_service.Service;

import com.petmatch.ubicacion_service.DTO.UbicacionRequestDTO;
import com.petmatch.ubicacion_service.DTO.UbicacionResponseDTO;
import com.petmatch.ubicacion_service.Exception.BadRequestException;
import com.petmatch.ubicacion_service.Exception.ResourceNotFoundException;
import com.petmatch.ubicacion_service.Model.Ubicacion;
import com.petmatch.ubicacion_service.Repository.CiudadReferenciaRepository;
import com.petmatch.ubicacion_service.Repository.UbicacionRepository;
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
class UbicacionServiceTest {

    @Mock
    private UbicacionRepository ubicacionRepository;

    @Mock
    private CiudadReferenciaRepository ciudadReferenciaRepository;

    @InjectMocks
    private UbicacionService ubicacionService;

    @Test
    @DisplayName("Debe listar todas las ubicaciones correctamente")
    void listarUbicaciones_debeRetornarLista() {
        Ubicacion ubicacion1 = new Ubicacion(
                1,
                "AV. PROVIDENCIA",
                "1234",
                "CERCA DEL METRO",
                "7500000",
                -33.4263,
                -70.6170,
                1
        );

        Ubicacion ubicacion2 = new Ubicacion(
                2,
                "AV. LAS CONDES",
                "5678",
                "EDIFICIO CORPORATIVO",
                "7550000",
                -33.4089,
                -70.5675,
                1
        );

        when(ubicacionRepository.findAll()).thenReturn(List.of(ubicacion1, ubicacion2));

        List<UbicacionResponseDTO> resultado = ubicacionService.listarUbicaciones();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).direccion()).isEqualTo("AV. PROVIDENCIA");
        assertThat(resultado.get(0).numero()).isEqualTo("1234");
        assertThat(resultado.get(0).idCiudad()).isEqualTo(1);

        verify(ubicacionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe listar ubicaciones por ciudad si la ciudad existe y está activa")
    void listarUbicacionesPorCiudad_cuandoCiudadExiste_debeRetornarLista() {
        Integer idCiudad = 1;

        Ubicacion ubicacion = new Ubicacion(
                1,
                "AV. PROVIDENCIA",
                "1234",
                "CERCA DEL METRO",
                "7500000",
                -33.4263,
                -70.6170,
                idCiudad
        );

        when(ciudadReferenciaRepository.existsByIdCiudadAndActivoTrue(idCiudad)).thenReturn(true);
        when(ubicacionRepository.findByIdCiudad(idCiudad)).thenReturn(List.of(ubicacion));

        List<UbicacionResponseDTO> resultado = ubicacionService.listarUbicacionesPorCiudad(idCiudad);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).idCiudad()).isEqualTo(idCiudad);
        assertThat(resultado.get(0).direccion()).isEqualTo("AV. PROVIDENCIA");

        verify(ciudadReferenciaRepository, times(1)).existsByIdCiudadAndActivoTrue(idCiudad);
        verify(ubicacionRepository, times(1)).findByIdCiudad(idCiudad);
    }

    @Test
    @DisplayName("No debe listar ubicaciones si la ciudad no existe o está inactiva")
    void listarUbicacionesPorCiudad_cuandoCiudadNoExiste_debeLanzarExcepcion() {
        Integer idCiudad = 99;

        when(ciudadReferenciaRepository.existsByIdCiudadAndActivoTrue(idCiudad)).thenReturn(false);

        assertThatThrownBy(() -> ubicacionService.listarUbicacionesPorCiudad(idCiudad))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No existe una ciudad activa registrada con ID: 99");

        verify(ciudadReferenciaRepository, times(1)).existsByIdCiudadAndActivoTrue(idCiudad);
        verify(ubicacionRepository, never()).findByIdCiudad(any());
    }

    @Test
    @DisplayName("Debe buscar ubicación por ID correctamente")
    void buscarUbicacionPorId_cuandoExiste_debeRetornarUbicacion() {
        Ubicacion ubicacion = new Ubicacion(
                1,
                "AV. PROVIDENCIA",
                "1234",
                "CERCA DEL METRO",
                "7500000",
                -33.4263,
                -70.6170,
                1
        );

        when(ubicacionRepository.findById(1)).thenReturn(Optional.of(ubicacion));

        UbicacionResponseDTO resultado = ubicacionService.buscarUbicacionPorId(1);

        assertThat(resultado.idUbicacion()).isEqualTo(1);
        assertThat(resultado.direccion()).isEqualTo("AV. PROVIDENCIA");
        assertThat(resultado.numero()).isEqualTo("1234");
        assertThat(resultado.idCiudad()).isEqualTo(1);

        verify(ubicacionRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la ubicación no existe")
    void buscarUbicacionPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(ubicacionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ubicacionService.buscarUbicacionPorId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró la ubicación con ID: 99");

        verify(ubicacionRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe crear ubicación correctamente")
    void crearUbicacion_cuandoDatosValidos_debeCrearUbicacion() {
        UbicacionRequestDTO requestDTO = new UbicacionRequestDTO(
                "Av. Providencia",
                "1234",
                "Cerca del metro",
                "7500000",
                -33.4263,
                -70.6170,
                1
        );

        when(ciudadReferenciaRepository.existsByIdCiudadAndActivoTrue(1)).thenReturn(true);
        when(ubicacionRepository.existsByDireccionIgnoreCaseAndNumeroIgnoreCaseAndIdCiudad(
                "AV. PROVIDENCIA",
                "1234",
                1
        )).thenReturn(false);

        when(ubicacionRepository.save(any(Ubicacion.class))).thenAnswer(invocation -> {
            Ubicacion ubicacion = invocation.getArgument(0);
            ubicacion.setIdUbicacion(1);
            return ubicacion;
        });

        UbicacionResponseDTO resultado = ubicacionService.crearUbicacion(requestDTO);

        assertThat(resultado.idUbicacion()).isEqualTo(1);
        assertThat(resultado.direccion()).isEqualTo("AV. PROVIDENCIA");
        assertThat(resultado.numero()).isEqualTo("1234");
        assertThat(resultado.referencia()).isEqualTo("CERCA DEL METRO");
        assertThat(resultado.codigoPostal()).isEqualTo("7500000");
        assertThat(resultado.latitud()).isEqualTo(-33.4263);
        assertThat(resultado.longitud()).isEqualTo(-70.6170);
        assertThat(resultado.idCiudad()).isEqualTo(1);

        verify(ciudadReferenciaRepository, times(1)).existsByIdCiudadAndActivoTrue(1);
        verify(ubicacionRepository, times(1))
                .existsByDireccionIgnoreCaseAndNumeroIgnoreCaseAndIdCiudad("AV. PROVIDENCIA", "1234", 1);
        verify(ubicacionRepository, times(1)).save(any(Ubicacion.class));
    }

    @Test
    @DisplayName("No debe crear ubicación si la ciudad no existe o está inactiva")
    void crearUbicacion_cuandoCiudadNoExiste_debeLanzarResourceNotFound() {
        UbicacionRequestDTO requestDTO = new UbicacionRequestDTO(
                "Av. Providencia",
                "1234",
                "Cerca del metro",
                "7500000",
                -33.4263,
                -70.6170,
                99
        );

        when(ciudadReferenciaRepository.existsByIdCiudadAndActivoTrue(99)).thenReturn(false);

        assertThatThrownBy(() -> ubicacionService.crearUbicacion(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No existe una ciudad activa registrada con ID: 99");

        verify(ubicacionRepository, never()).save(any(Ubicacion.class));
    }

    @Test
    @DisplayName("No debe crear ubicación duplicada en la misma ciudad")
    void crearUbicacion_cuandoDuplicada_debeLanzarBadRequest() {
        UbicacionRequestDTO requestDTO = new UbicacionRequestDTO(
                "Av. Providencia",
                "1234",
                "Cerca del metro",
                "7500000",
                -33.4263,
                -70.6170,
                1
        );

        when(ciudadReferenciaRepository.existsByIdCiudadAndActivoTrue(1)).thenReturn(true);
        when(ubicacionRepository.existsByDireccionIgnoreCaseAndNumeroIgnoreCaseAndIdCiudad(
                "AV. PROVIDENCIA",
                "1234",
                1
        )).thenReturn(true);

        assertThatThrownBy(() -> ubicacionService.crearUbicacion(requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe una ubicación registrada");

        verify(ubicacionRepository, never()).save(any(Ubicacion.class));
    }

    @Test
    @DisplayName("Debe actualizar ubicación correctamente")
    void actualizarUbicacion_cuandoDatosValidos_debeActualizarUbicacion() {
        Ubicacion ubicacionExistente = new Ubicacion(
                1,
                "AV. PROVIDENCIA",
                "1234",
                "CERCA DEL METRO",
                "7500000",
                -33.4263,
                -70.6170,
                1
        );

        UbicacionRequestDTO requestDTO = new UbicacionRequestDTO(
                "Av. Las Condes",
                "5678",
                "Edificio corporativo",
                "7550000",
                -33.4089,
                -70.5675,
                1
        );

        when(ubicacionRepository.findById(1)).thenReturn(Optional.of(ubicacionExistente));
        when(ciudadReferenciaRepository.existsByIdCiudadAndActivoTrue(1)).thenReturn(true);
        when(ubicacionRepository.existsByDireccionIgnoreCaseAndNumeroIgnoreCaseAndIdCiudadAndIdUbicacionNot(
                "AV. LAS CONDES",
                "5678",
                1,
                1
        )).thenReturn(false);
        when(ubicacionRepository.save(any(Ubicacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UbicacionResponseDTO resultado = ubicacionService.actualizarUbicacion(1, requestDTO);

        assertThat(resultado.idUbicacion()).isEqualTo(1);
        assertThat(resultado.direccion()).isEqualTo("AV. LAS CONDES");
        assertThat(resultado.numero()).isEqualTo("5678");
        assertThat(resultado.referencia()).isEqualTo("EDIFICIO CORPORATIVO");
        assertThat(resultado.codigoPostal()).isEqualTo("7550000");
        assertThat(resultado.idCiudad()).isEqualTo(1);

        verify(ubicacionRepository, times(1)).findById(1);
        verify(ubicacionRepository, times(1)).save(any(Ubicacion.class));
    }

    @Test
    @DisplayName("No debe actualizar ubicación si existe otra duplicada")
    void actualizarUbicacion_cuandoDuplicada_debeLanzarBadRequest() {
        Ubicacion ubicacionExistente = new Ubicacion(
                1,
                "AV. PROVIDENCIA",
                "1234",
                "CERCA DEL METRO",
                "7500000",
                -33.4263,
                -70.6170,
                1
        );

        UbicacionRequestDTO requestDTO = new UbicacionRequestDTO(
                "Av. Las Condes",
                "5678",
                "Edificio corporativo",
                "7550000",
                -33.4089,
                -70.5675,
                1
        );

        when(ubicacionRepository.findById(1)).thenReturn(Optional.of(ubicacionExistente));
        when(ciudadReferenciaRepository.existsByIdCiudadAndActivoTrue(1)).thenReturn(true);
        when(ubicacionRepository.existsByDireccionIgnoreCaseAndNumeroIgnoreCaseAndIdCiudadAndIdUbicacionNot(
                "AV. LAS CONDES",
                "5678",
                1,
                1
        )).thenReturn(true);

        assertThatThrownBy(() -> ubicacionService.actualizarUbicacion(1, requestDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Ya existe otra ubicación registrada");

        verify(ubicacionRepository, never()).save(any(Ubicacion.class));
    }

    @Test
    @DisplayName("Debe eliminar ubicación correctamente")
    void eliminarUbicacion_cuandoExiste_debeEliminarUbicacion() {
        Ubicacion ubicacion = new Ubicacion(
                1,
                "AV. PROVIDENCIA",
                "1234",
                "CERCA DEL METRO",
                "7500000",
                -33.4263,
                -70.6170,
                1
        );

        when(ubicacionRepository.findById(1)).thenReturn(Optional.of(ubicacion));

        ubicacionService.eliminarUbicacion(1);

        verify(ubicacionRepository, times(1)).findById(1);
        verify(ubicacionRepository, times(1)).delete(ubicacion);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar ubicación inexistente")
    void eliminarUbicacion_cuandoNoExiste_debeLanzarExcepcion() {
        when(ubicacionRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ubicacionService.eliminarUbicacion(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró la ubicación con ID: 99");

        verify(ubicacionRepository, never()).delete(any(Ubicacion.class));
    }
}