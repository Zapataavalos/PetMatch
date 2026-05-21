package com.petmatch.ciudad_service.Event;

import com.petmatch.ciudad_service.Model.RegionReferencia;
import com.petmatch.ciudad_service.Repository.RegionReferenciaRepository;
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
class RegionEventConsumerTest {

    @Mock
    private RegionReferenciaRepository regionReferenciaRepository;

    @InjectMocks
    private RegionEventConsumer regionEventConsumer;

    @Test
    @DisplayName("Debe consumir evento de región creada y guardar referencia activa")
    void consumirRegionCreada_debeGuardarRegionReferenciaActiva() {
        String mensajeJson = """
                {
                    "idRegion": 1,
                    "nombreRegion": "METROPOLITANA",
                    "idPais": 1,
                    "activo": true
                }
                """;

        regionEventConsumer.consumirRegionCreada(mensajeJson);

        ArgumentCaptor<RegionReferencia> captor = ArgumentCaptor.forClass(RegionReferencia.class);

        verify(regionReferenciaRepository, times(1)).save(captor.capture());

        RegionReferencia regionGuardada = captor.getValue();

        assertThat(regionGuardada.getIdRegion()).isEqualTo(1);
        assertThat(regionGuardada.getNombreRegion()).isEqualTo("METROPOLITANA");
        assertThat(regionGuardada.getIdPais()).isEqualTo(1);
        assertThat(regionGuardada.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de región actualizada y actualizar referencia existente")
    void consumirRegionActualizada_cuandoExiste_debeActualizarRegionReferencia() {
        RegionReferencia existente = new RegionReferencia(1, "METROPOLITANA", 1, true);

        when(regionReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idRegion": 1,
                    "nombreRegion": "VALPARAISO",
                    "idPais": 1,
                    "activo": true
                }
                """;

        regionEventConsumer.consumirRegionActualizada(mensajeJson);

        ArgumentCaptor<RegionReferencia> captor = ArgumentCaptor.forClass(RegionReferencia.class);

        verify(regionReferenciaRepository, times(1)).findById(1);
        verify(regionReferenciaRepository, times(1)).save(captor.capture());

        RegionReferencia regionGuardada = captor.getValue();

        assertThat(regionGuardada.getIdRegion()).isEqualTo(1);
        assertThat(regionGuardada.getNombreRegion()).isEqualTo("VALPARAISO");
        assertThat(regionGuardada.getIdPais()).isEqualTo(1);
        assertThat(regionGuardada.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de región actualizada aunque no exista referencia previa")
    void consumirRegionActualizada_cuandoNoExiste_debeCrearRegionReferencia() {
        when(regionReferenciaRepository.findById(1)).thenReturn(Optional.empty());

        String mensajeJson = """
                {
                    "idRegion": 1,
                    "nombreRegion": "METROPOLITANA",
                    "idPais": 1,
                    "activo": true
                }
                """;

        regionEventConsumer.consumirRegionActualizada(mensajeJson);

        ArgumentCaptor<RegionReferencia> captor = ArgumentCaptor.forClass(RegionReferencia.class);

        verify(regionReferenciaRepository, times(1)).findById(1);
        verify(regionReferenciaRepository, times(1)).save(captor.capture());

        RegionReferencia regionGuardada = captor.getValue();

        assertThat(regionGuardada.getIdRegion()).isEqualTo(1);
        assertThat(regionGuardada.getNombreRegion()).isEqualTo("METROPOLITANA");
        assertThat(regionGuardada.getIdPais()).isEqualTo(1);
        assertThat(regionGuardada.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de región eliminada y marcar referencia como inactiva")
    void consumirRegionEliminada_debeMarcarRegionReferenciaInactiva() {
        RegionReferencia existente = new RegionReferencia(1, "METROPOLITANA", 1, true);

        when(regionReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idRegion": 1,
                    "nombreRegion": "METROPOLITANA",
                    "idPais": 1,
                    "activo": false
                }
                """;

        regionEventConsumer.consumirRegionEliminada(mensajeJson);

        ArgumentCaptor<RegionReferencia> captor = ArgumentCaptor.forClass(RegionReferencia.class);

        verify(regionReferenciaRepository, times(1)).findById(1);
        verify(regionReferenciaRepository, times(1)).save(captor.capture());

        RegionReferencia regionGuardada = captor.getValue();

        assertThat(regionGuardada.getIdRegion()).isEqualTo(1);
        assertThat(regionGuardada.getNombreRegion()).isEqualTo("METROPOLITANA");
        assertThat(regionGuardada.getIdPais()).isEqualTo(1);
        assertThat(regionGuardada.getActivo()).isFalse();
    }

    @Test
    @DisplayName("Debe rechazar mensaje inválido y no guardar referencia")
    void consumirRegionCreada_conJsonInvalido_debeLanzarExcepcion() {
        String mensajeJsonInvalido = """
                {
                    "idRegion": 1,
                    "nombreRegion":
                }
                """;

        assertThatThrownBy(() -> regionEventConsumer.consumirRegionCreada(mensajeJsonInvalido))
                .isInstanceOf(AmqpRejectAndDontRequeueException.class)
                .hasMessageContaining("No fue posible convertir el mensaje de región a objeto");

        verify(regionReferenciaRepository, never()).save(any(RegionReferencia.class));
    }
}