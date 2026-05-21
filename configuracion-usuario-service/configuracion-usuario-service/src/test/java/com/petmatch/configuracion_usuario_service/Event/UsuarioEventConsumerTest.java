package com.petmatch.configuracion_usuario_service.Event;

import com.petmatch.configuracion_usuario_service.Model.UsuarioReferencia;
import com.petmatch.configuracion_usuario_service.Repository.UsuarioReferenciaRepository;
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
class UsuarioEventConsumerTest {

    @Mock
    private UsuarioReferenciaRepository usuarioReferenciaRepository;

    @InjectMocks
    private UsuarioEventConsumer usuarioEventConsumer;

    @Test
    @DisplayName("Debe consumir evento de usuario creado y guardar referencia activa")
    void consumirUsuarioCreado_debeGuardarUsuarioReferenciaActivo() {
        String mensajeJson = """
                {
                    "idUsuario": 1,
                    "nombre": "BENJAMIN MENDEZ",
                    "email": "benjamin@test.cl",
                    "idRol": 1,
                    "activo": true
                }
                """;

        usuarioEventConsumer.consumirUsuarioCreado(mensajeJson);

        ArgumentCaptor<UsuarioReferencia> captor = ArgumentCaptor.forClass(UsuarioReferencia.class);

        verify(usuarioReferenciaRepository, times(1)).save(captor.capture());

        UsuarioReferencia usuarioGuardado = captor.getValue();

        assertThat(usuarioGuardado.getIdUsuario()).isEqualTo(1);
        assertThat(usuarioGuardado.getNombre()).isEqualTo("BENJAMIN MENDEZ");
        assertThat(usuarioGuardado.getEmail()).isEqualTo("benjamin@test.cl");
        assertThat(usuarioGuardado.getIdRol()).isEqualTo(1);
        assertThat(usuarioGuardado.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de usuario actualizado y actualizar referencia existente")
    void consumirUsuarioActualizado_cuandoExiste_debeActualizarUsuarioReferencia() {
        UsuarioReferencia existente = new UsuarioReferencia(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                1,
                true
        );

        when(usuarioReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idUsuario": 1,
                    "nombre": "BENJAMIN ACTUALIZADO",
                    "email": "nuevo@test.cl",
                    "idRol": 2,
                    "activo": true
                }
                """;

        usuarioEventConsumer.consumirUsuarioActualizado(mensajeJson);

        ArgumentCaptor<UsuarioReferencia> captor = ArgumentCaptor.forClass(UsuarioReferencia.class);

        verify(usuarioReferenciaRepository, times(1)).findById(1);
        verify(usuarioReferenciaRepository, times(1)).save(captor.capture());

        UsuarioReferencia usuarioGuardado = captor.getValue();

        assertThat(usuarioGuardado.getIdUsuario()).isEqualTo(1);
        assertThat(usuarioGuardado.getNombre()).isEqualTo("BENJAMIN ACTUALIZADO");
        assertThat(usuarioGuardado.getEmail()).isEqualTo("nuevo@test.cl");
        assertThat(usuarioGuardado.getIdRol()).isEqualTo(2);
        assertThat(usuarioGuardado.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de usuario actualizado aunque no exista referencia previa")
    void consumirUsuarioActualizado_cuandoNoExiste_debeCrearReferencia() {
        when(usuarioReferenciaRepository.findById(1)).thenReturn(Optional.empty());

        String mensajeJson = """
                {
                    "idUsuario": 1,
                    "nombre": "BENJAMIN MENDEZ",
                    "email": "benjamin@test.cl",
                    "idRol": 1,
                    "activo": true
                }
                """;

        usuarioEventConsumer.consumirUsuarioActualizado(mensajeJson);

        ArgumentCaptor<UsuarioReferencia> captor = ArgumentCaptor.forClass(UsuarioReferencia.class);

        verify(usuarioReferenciaRepository, times(1)).findById(1);
        verify(usuarioReferenciaRepository, times(1)).save(captor.capture());

        UsuarioReferencia usuarioGuardado = captor.getValue();

        assertThat(usuarioGuardado.getIdUsuario()).isEqualTo(1);
        assertThat(usuarioGuardado.getNombre()).isEqualTo("BENJAMIN MENDEZ");
        assertThat(usuarioGuardado.getEmail()).isEqualTo("benjamin@test.cl");
        assertThat(usuarioGuardado.getIdRol()).isEqualTo(1);
        assertThat(usuarioGuardado.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe consumir evento de usuario eliminado y marcar referencia inactiva")
    void consumirUsuarioEliminado_debeMarcarReferenciaInactiva() {
        UsuarioReferencia existente = new UsuarioReferencia(
                1,
                "BENJAMIN MENDEZ",
                "benjamin@test.cl",
                1,
                true
        );

        when(usuarioReferenciaRepository.findById(1)).thenReturn(Optional.of(existente));

        String mensajeJson = """
                {
                    "idUsuario": 1,
                    "nombre": "BENJAMIN MENDEZ",
                    "email": "benjamin@test.cl",
                    "idRol": 1,
                    "activo": false
                }
                """;

        usuarioEventConsumer.consumirUsuarioEliminado(mensajeJson);

        ArgumentCaptor<UsuarioReferencia> captor = ArgumentCaptor.forClass(UsuarioReferencia.class);

        verify(usuarioReferenciaRepository, times(1)).findById(1);
        verify(usuarioReferenciaRepository, times(1)).save(captor.capture());

        UsuarioReferencia usuarioGuardado = captor.getValue();

        assertThat(usuarioGuardado.getIdUsuario()).isEqualTo(1);
        assertThat(usuarioGuardado.getNombre()).isEqualTo("BENJAMIN MENDEZ");
        assertThat(usuarioGuardado.getEmail()).isEqualTo("benjamin@test.cl");
        assertThat(usuarioGuardado.getIdRol()).isEqualTo(1);
        assertThat(usuarioGuardado.getActivo()).isFalse();
    }

    @Test
    @DisplayName("Debe rechazar mensaje inválido de usuario")
    void consumirUsuarioCreado_conJsonInvalido_debeLanzarExcepcion() {
        String mensajeJsonInvalido = """
                {
                    "idUsuario": 1,
                    "nombre":
                }
                """;

        assertThatThrownBy(() -> usuarioEventConsumer.consumirUsuarioCreado(mensajeJsonInvalido))
                .isInstanceOf(AmqpRejectAndDontRequeueException.class)
                .hasMessageContaining("No fue posible convertir el mensaje de usuario a objeto");

        verify(usuarioReferenciaRepository, never()).save(any(UsuarioReferencia.class));
    }
}