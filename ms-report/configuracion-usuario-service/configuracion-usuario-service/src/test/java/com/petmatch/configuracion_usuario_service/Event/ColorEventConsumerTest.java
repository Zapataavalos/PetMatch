package com.petmatch.configuracion_usuario_service.Event;

import com.petmatch.configuracion_usuario_service.Model.ColorReferencia;
import com.petmatch.configuracion_usuario_service.Repository.ColorReferenciaRepository;
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
class ColorEventConsumerTest {

    @Mock
    private ColorReferenciaRepository colorReferenciaRepository;

    @InjectMocks
    private ColorEventConsumer colorEventConsumer;

    @Test
    @DisplayName("Debe consumir evento de color creado y guardar referencia activa")
    void consumirColorCreado_debeGuardarColorReferenciaActivo() {
        String mensajeJson = """
                {
                    "idColor": 1,
                    "nombreColor": "ROJO",
                    "codigoHexadecimal": "#FF0000",
                    "activo": true
                }
                """;

        colorEventConsumer.consumirColorCreado(mensajeJson);

        ArgumentCaptor<ColorReferencia> captor = ArgumentCaptor.forClass(ColorReferencia.class);

        verify(colorReferenciaRepository, times(1)).save(captor.capture());

        ColorReferencia colorGuardado = captor.getValue();

        assertThat(colorGuardado.getIdColor()).isEqualTo(1);
        assertThat(colorGuardado.getNombreColor()).isEqualTo("ROJO");
        assertThat(colorGuardado.getCodigoHexadecimal()).isEqualTo("#FF0000");
        assertThat(colorGuardado.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de color actualizado y actualizar referencia existente")
    void consumirColorActualizado_cuandoExiste_debeActualizarColorReferencia() {
        ColorReferencia existente = new ColorReferencia(1, "ROJO", "#FF0000", true);

        when(colorReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idColor": 1,
                    "nombreColor": "AZUL",
                    "codigoHexadecimal": "#0000FF",
                    "activo": true
                }
                """;

        colorEventConsumer.consumirColorActualizado(mensajeJson);

        ArgumentCaptor<ColorReferencia> captor = ArgumentCaptor.forClass(ColorReferencia.class);

        verify(colorReferenciaRepository, times(1)).findById(1);
        verify(colorReferenciaRepository, times(1)).save(captor.capture());

        ColorReferencia colorGuardado = captor.getValue();

        assertThat(colorGuardado.getIdColor()).isEqualTo(1);
        assertThat(colorGuardado.getNombreColor()).isEqualTo("AZUL");
        assertThat(colorGuardado.getCodigoHexadecimal()).isEqualTo("#0000FF");
        assertThat(colorGuardado.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de color actualizado aunque no exista referencia previa")
    void consumirColorActualizado_cuandoNoExiste_debeCrearReferencia() {
        when(colorReferenciaRepository.findById(1)).thenReturn(Optional.empty());

        String mensajeJson = """
                {
                    "idColor": 1,
                    "nombreColor": "ROJO",
                    "codigoHexadecimal": "#FF0000",
                    "activo": true
                }
                """;

        colorEventConsumer.consumirColorActualizado(mensajeJson);

        ArgumentCaptor<ColorReferencia> captor = ArgumentCaptor.forClass(ColorReferencia.class);

        verify(colorReferenciaRepository, times(1)).findById(1);
        verify(colorReferenciaRepository, times(1)).save(captor.capture());

        ColorReferencia colorGuardado = captor.getValue();

        assertThat(colorGuardado.getIdColor()).isEqualTo(1);
        assertThat(colorGuardado.getNombreColor()).isEqualTo("ROJO");
        assertThat(colorGuardado.getCodigoHexadecimal()).isEqualTo("#FF0000");
        assertThat(colorGuardado.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de color eliminado y marcar referencia inactiva")
    void consumirColorEliminado_debeMarcarReferenciaInactiva() {
        ColorReferencia existente = new ColorReferencia(1, "ROJO", "#FF0000", true);

        when(colorReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idColor": 1,
                    "nombreColor": "ROJO",
                    "codigoHexadecimal": "#FF0000",
                    "activo": false
                }
                """;

        colorEventConsumer.consumirColorEliminado(mensajeJson);

        ArgumentCaptor<ColorReferencia> captor = ArgumentCaptor.forClass(ColorReferencia.class);

        verify(colorReferenciaRepository, times(1)).findById(1);
        verify(colorReferenciaRepository, times(1)).save(captor.capture());

        ColorReferencia colorGuardado = captor.getValue();

        assertThat(colorGuardado.getIdColor()).isEqualTo(1);
        assertThat(colorGuardado.getNombreColor()).isEqualTo("ROJO");
        assertThat(colorGuardado.getCodigoHexadecimal()).isEqualTo("#FF0000");
        assertThat(colorGuardado.getActivo()).isFalse();
    }

    @Test
    @DisplayName("Debe rechazar mensaje inválido de color")
    void consumirColorCreado_conJsonInvalido_debeLanzarExcepcion() {
        String mensajeJsonInvalido = """
                {
                    "idColor": 1,
                    "nombreColor":
                }
                """;

        assertThatThrownBy(() -> colorEventConsumer.consumirColorCreado(mensajeJsonInvalido))
                .isInstanceOf(AmqpRejectAndDontRequeueException.class)
                .hasMessageContaining("No fue posible convertir el mensaje de color a objeto");

        verify(colorReferenciaRepository, never()).save(any(ColorReferencia.class));
    }
}