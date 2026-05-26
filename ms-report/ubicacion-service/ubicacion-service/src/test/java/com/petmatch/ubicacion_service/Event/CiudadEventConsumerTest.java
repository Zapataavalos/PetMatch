package com.petmatch.ubicacion_service.Event;

import com.petmatch.ubicacion_service.Model.CiudadReferencia;
import com.petmatch.ubicacion_service.Repository.CiudadReferenciaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CiudadEventConsumerTest {

    @Mock
    private CiudadReferenciaRepository ciudadReferenciaRepository;

    @InjectMocks
    private CiudadEventConsumer ciudadEventConsumer;

    @Test
    @DisplayName("Debe consumir evento de ciudad creada y guardar referencia activa")
    void consumirCiudadCreada_debeGuardarCiudadReferenciaActiva() {
        String mensajeJson = """
                {
                    "idCiudad": 1,
                    "nombreCiudad": "SANTIAGO",
                    "idRegion": 1,
                    "activo": true
                }
                """;

        ciudadEventConsumer.consumirCiudadCreada(mensajeJson);

        ArgumentCaptor<CiudadReferencia> captor = ArgumentCaptor.forClass(CiudadReferencia.class);

        verify(ciudadReferenciaRepository, times(1)).save(captor.capture());

        CiudadReferencia ciudadGuardada = captor.getValue();

        assertThat(ciudadGuardada.getIdCiudad()).isEqualTo(1);
        assertThat(ciudadGuardada.getNombreCiudad()).isEqualTo("SANTIAGO");
        assertThat(ciudadGuardada.getIdRegion()).isEqualTo(1);
        assertThat(ciudadGuardada.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de ciudad actualizada y actualizar referencia existente")
    void consumirCiudadActualizada_cuandoExiste_debeActualizarCiudadReferencia() {
        CiudadReferencia existente = new CiudadReferencia(1, "SANTIAGO", 1, true);

        when(ciudadReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idCiudad": 1,
                    "nombreCiudad": "PROVIDENCIA",
                    "idRegion": 1,
                    "activo": true
                }
                """;

        ciudadEventConsumer.consumirCiudadActualizada(mensajeJson);

        ArgumentCaptor<CiudadReferencia> captor = ArgumentCaptor.forClass(CiudadReferencia.class);

        verify(ciudadReferenciaRepository, times(1)).findById(1);
        verify(ciudadReferenciaRepository, times(1)).save(captor.capture());

        CiudadReferencia ciudadGuardada = captor.getValue();

        assertThat(ciudadGuardada.getIdCiudad()).isEqualTo(1);
        assertThat(ciudadGuardada.getNombreCiudad()).isEqualTo("PROVIDENCIA");
        assertThat(ciudadGuardada.getIdRegion()).isEqualTo(1);
        assertThat(ciudadGuardada.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de ciudad actualizada aunque no exista referencia previa")
    void consumirCiudadActualizada_cuandoNoExiste_debeCrearReferencia() {
        when(ciudadReferenciaRepository.findById(1)).thenReturn(Optional.empty());

        String mensajeJson = """
                {
                    "idCiudad": 1,
                    "nombreCiudad": "SANTIAGO",
                    "idRegion": 1,
                    "activo": true
                }
                """;

        ciudadEventConsumer.consumirCiudadActualizada(mensajeJson);

        ArgumentCaptor<CiudadReferencia> captor = ArgumentCaptor.forClass(CiudadReferencia.class);

        verify(ciudadReferenciaRepository, times(1)).findById(1);
        verify(ciudadReferenciaRepository, times(1)).save(captor.capture());

        CiudadReferencia ciudadGuardada = captor.getValue();

        assertThat(ciudadGuardada.getIdCiudad()).isEqualTo(1);
        assertThat(ciudadGuardada.getNombreCiudad()).isEqualTo("SANTIAGO");
        assertThat(ciudadGuardada.getIdRegion()).isEqualTo(1);
        assertThat(ciudadGuardada.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de ciudad eliminada y marcar referencia inactiva")
    void consumirCiudadEliminada_debeMarcarReferenciaInactiva() {
        CiudadReferencia existente = new CiudadReferencia(1, "SANTIAGO", 1, true);

        when(ciudadReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idCiudad": 1,
                    "nombreCiudad": "SANTIAGO",
                    "idRegion": 1,
                    "activo": false
                }
                """;

        ciudadEventConsumer.consumirCiudadEliminada(mensajeJson);

        ArgumentCaptor<CiudadReferencia> captor = ArgumentCaptor.forClass(CiudadReferencia.class);

        verify(ciudadReferenciaRepository, times(1)).findById(1);
        verify(ciudadReferenciaRepository, times(1)).save(captor.capture());

        CiudadReferencia ciudadGuardada = captor.getValue();

        assertThat(ciudadGuardada.getIdCiudad()).isEqualTo(1);
        assertThat(ciudadGuardada.getNombreCiudad()).isEqualTo("SANTIAGO");
        assertThat(ciudadGuardada.getIdRegion()).isEqualTo(1);
        assertThat(ciudadGuardada.getActivo()).isFalse();
    }

    @Test
    @DisplayName("Debe rechazar mensaje inválido de ciudad")
    void consumirCiudadCreada_conJsonInvalido_debeLanzarExcepcion() {
        String mensajeJsonInvalido = """
                {
                    "idCiudad": 1,
                    "nombreCiudad":
                }
                """;

        assertThatThrownBy(() -> ciudadEventConsumer.consumirCiudadCreada(mensajeJsonInvalido))
                .isInstanceOf(AmqpRejectAndDontRequeueException.class)
                .hasMessageContaining("No fue posible convertir el mensaje de ciudad a objeto");

        verify(ciudadReferenciaRepository, never()).save(any(CiudadReferencia.class));
    }
}