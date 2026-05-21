package com.petmatch.region_service.Event;

import com.petmatch.region_service.Model.PaisReferencia;
import com.petmatch.region_service.Repository.PaisReferenciaRepository;
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
class PaisEventConsumerTest {

    @Mock
    private PaisReferenciaRepository paisReferenciaRepository;

    @InjectMocks
    private PaisEventConsumer paisEventConsumer;

    @Test
    @DisplayName("Debe consumir evento de país creado y guardar referencia activa")
    void consumirPaisCreado_debeGuardarPaisReferenciaActivo() {
        String mensajeJson = """
                {
                    "idPais": 1,
                    "nombrePais": "CHILE",
                    "activo": true
                }
                """;

        paisEventConsumer.consumirPaisCreado(mensajeJson);

        ArgumentCaptor<PaisReferencia> captor = ArgumentCaptor.forClass(PaisReferencia.class);

        verify(paisReferenciaRepository, times(1)).save(captor.capture());

        PaisReferencia paisGuardado = captor.getValue();

        assertThat(paisGuardado.getIdPais()).isEqualTo(1);
        assertThat(paisGuardado.getNombrePais()).isEqualTo("CHILE");
        assertThat(paisGuardado.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de país actualizado y actualizar referencia existente")
    void consumirPaisActualizado_cuandoExiste_debeActualizarPaisReferencia() {
        PaisReferencia existente = new PaisReferencia(1, "CHILE", true);

        when(paisReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idPais": 1,
                    "nombrePais": "CHILE ACTUALIZADO",
                    "activo": true
                }
                """;

        paisEventConsumer.consumirPaisActualizado(mensajeJson);

        ArgumentCaptor<PaisReferencia> captor = ArgumentCaptor.forClass(PaisReferencia.class);

        verify(paisReferenciaRepository, times(1)).findById(1);
        verify(paisReferenciaRepository, times(1)).save(captor.capture());

        PaisReferencia paisGuardado = captor.getValue();

        assertThat(paisGuardado.getIdPais()).isEqualTo(1);
        assertThat(paisGuardado.getNombrePais()).isEqualTo("CHILE ACTUALIZADO");
        assertThat(paisGuardado.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de país actualizado aunque no exista referencia previa")
    void consumirPaisActualizado_cuandoNoExiste_debeCrearPaisReferencia() {
        when(paisReferenciaRepository.findById(1)).thenReturn(Optional.empty());

        String mensajeJson = """
                {
                    "idPais": 1,
                    "nombrePais": "CHILE",
                    "activo": true
                }
                """;

        paisEventConsumer.consumirPaisActualizado(mensajeJson);

        ArgumentCaptor<PaisReferencia> captor = ArgumentCaptor.forClass(PaisReferencia.class);

        verify(paisReferenciaRepository, times(1)).findById(1);
        verify(paisReferenciaRepository, times(1)).save(captor.capture());

        PaisReferencia paisGuardado = captor.getValue();

        assertThat(paisGuardado.getIdPais()).isEqualTo(1);
        assertThat(paisGuardado.getNombrePais()).isEqualTo("CHILE");
        assertThat(paisGuardado.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de país eliminado y marcar referencia como inactiva")
    void consumirPaisEliminado_debeMarcarPaisReferenciaInactivo() {
        PaisReferencia existente = new PaisReferencia(1, "CHILE", true);

        when(paisReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idPais": 1,
                    "nombrePais": "CHILE",
                    "activo": false
                }
                """;

        paisEventConsumer.consumirPaisEliminado(mensajeJson);

        ArgumentCaptor<PaisReferencia> captor = ArgumentCaptor.forClass(PaisReferencia.class);

        verify(paisReferenciaRepository, times(1)).findById(1);
        verify(paisReferenciaRepository, times(1)).save(captor.capture());

        PaisReferencia paisGuardado = captor.getValue();

        assertThat(paisGuardado.getIdPais()).isEqualTo(1);
        assertThat(paisGuardado.getNombrePais()).isEqualTo("CHILE");
        assertThat(paisGuardado.getActivo()).isFalse();
    }

    @Test
    @DisplayName("Debe rechazar mensaje inválido y no guardar referencia")
    void consumirPaisCreado_conJsonInvalido_debeLanzarExcepcion() {
        String mensajeJsonInvalido = """
                {
                    "idPais": 1,
                    "nombrePais":
                }
                """;

        assertThatThrownBy(() -> paisEventConsumer.consumirPaisCreado(mensajeJsonInvalido))
                .isInstanceOf(AmqpRejectAndDontRequeueException.class)
                .hasMessageContaining("No fue posible convertir el mensaje de país a objeto");

        verify(paisReferenciaRepository, never()).save(any(PaisReferencia.class));
    }
}