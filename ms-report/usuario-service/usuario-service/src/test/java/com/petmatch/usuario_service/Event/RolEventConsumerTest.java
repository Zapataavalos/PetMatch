package com.petmatch.usuario_service.Event;

import com.petmatch.usuario_service.Model.RolReferencia;
import com.petmatch.usuario_service.Repository.RolReferenciaRepository;
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
class RolEventConsumerTest {

    @Mock
    private RolReferenciaRepository rolReferenciaRepository;

    @InjectMocks
    private RolEventConsumer rolEventConsumer;

    @Test
    @DisplayName("Debe consumir evento de rol creado y guardar referencia activa")
    void consumirRolCreado_debeGuardarRolReferenciaActivo() {
        String mensajeJson = """
                {
                    "idRol": 1,
                    "nombreRol": "ADMINISTRADOR",
                    "activo": true
                }
                """;

        rolEventConsumer.consumirRolCreado(mensajeJson);

        ArgumentCaptor<RolReferencia> captor = ArgumentCaptor.forClass(RolReferencia.class);

        verify(rolReferenciaRepository, times(1)).save(captor.capture());

        RolReferencia rolGuardado = captor.getValue();

        assertThat(rolGuardado.getIdRol()).isEqualTo(1);
        assertThat(rolGuardado.getNombreRol()).isEqualTo("ADMINISTRADOR");
        assertThat(rolGuardado.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de rol actualizado")
    void consumirRolActualizado_debeActualizarRolReferencia() {
        RolReferencia existente = new RolReferencia(1, "USUARIO", true);

        when(rolReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idRol": 1,
                    "nombreRol": "ADMINISTRADOR",
                    "activo": true
                }
                """;

        rolEventConsumer.consumirRolActualizado(mensajeJson);

        ArgumentCaptor<RolReferencia> captor = ArgumentCaptor.forClass(RolReferencia.class);

        verify(rolReferenciaRepository, times(1)).findById(1);
        verify(rolReferenciaRepository, times(1)).save(captor.capture());

        RolReferencia rolGuardado = captor.getValue();

        assertThat(rolGuardado.getIdRol()).isEqualTo(1);
        assertThat(rolGuardado.getNombreRol()).isEqualTo("ADMINISTRADOR");
        assertThat(rolGuardado.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de rol eliminado y marcar referencia inactiva")
    void consumirRolEliminado_debeMarcarRolReferenciaInactivo() {
        RolReferencia existente = new RolReferencia(1, "ADMINISTRADOR", true);

        when(rolReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idRol": 1,
                    "nombreRol": "ADMINISTRADOR",
                    "activo": false
                }
                """;

        rolEventConsumer.consumirRolEliminado(mensajeJson);

        ArgumentCaptor<RolReferencia> captor = ArgumentCaptor.forClass(RolReferencia.class);

        verify(rolReferenciaRepository, times(1)).findById(1);
        verify(rolReferenciaRepository, times(1)).save(captor.capture());

        RolReferencia rolGuardado = captor.getValue();

        assertThat(rolGuardado.getIdRol()).isEqualTo(1);
        assertThat(rolGuardado.getNombreRol()).isEqualTo("ADMINISTRADOR");
        assertThat(rolGuardado.getActivo()).isFalse();
    }

    @Test
    @DisplayName("Debe rechazar mensaje inválido")
    void consumirRolCreado_conJsonInvalido_debeLanzarExcepcion() {
        String mensajeJsonInvalido = """
                {
                    "idRol": 1,
                    "nombreRol":
                }
                """;

        assertThatThrownBy(() -> rolEventConsumer.consumirRolCreado(mensajeJsonInvalido))
                .isInstanceOf(AmqpRejectAndDontRequeueException.class)
                .hasMessageContaining("No fue posible convertir el mensaje de rol a objeto");

        verify(rolReferenciaRepository, never()).save(any(RolReferencia.class));
    }
}