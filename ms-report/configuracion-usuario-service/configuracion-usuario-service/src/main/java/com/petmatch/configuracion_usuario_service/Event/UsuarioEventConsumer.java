package com.petmatch.configuracion_usuario_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.configuracion_usuario_service.Config.RabbitMQConfig;
import com.petmatch.configuracion_usuario_service.Model.UsuarioReferencia;
import com.petmatch.configuracion_usuario_service.Repository.UsuarioReferenciaRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEventConsumer {

    private final UsuarioReferenciaRepository usuarioReferenciaRepository;
    private final ObjectMapper objectMapper;

    public UsuarioEventConsumer(UsuarioReferenciaRepository usuarioReferenciaRepository) {
        this.usuarioReferenciaRepository = usuarioReferenciaRepository;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.USUARIO_CREADO_QUEUE)
    public void consumirUsuarioCreado(String mensajeJson) {
        UsuarioEventDTO event = convertirMensajeAEvento(mensajeJson);

        UsuarioReferencia usuarioReferencia = new UsuarioReferencia(
                event.idUsuario(),
                event.nombre(),
                event.email(),
                event.idRol(),
                true
        );

        usuarioReferenciaRepository.save(usuarioReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.USUARIO_ACTUALIZADO_QUEUE)
    public void consumirUsuarioActualizado(String mensajeJson) {
        UsuarioEventDTO event = convertirMensajeAEvento(mensajeJson);

        UsuarioReferencia usuarioReferencia = usuarioReferenciaRepository
                .findById(event.idUsuario())
                .orElse(new UsuarioReferencia());

        usuarioReferencia.setIdUsuario(event.idUsuario());
        usuarioReferencia.setNombre(event.nombre());
        usuarioReferencia.setEmail(event.email());
        usuarioReferencia.setIdRol(event.idRol());
        usuarioReferencia.setActivo(true);

        usuarioReferenciaRepository.save(usuarioReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.USUARIO_ELIMINADO_QUEUE)
    public void consumirUsuarioEliminado(String mensajeJson) {
        UsuarioEventDTO event = convertirMensajeAEvento(mensajeJson);

        UsuarioReferencia usuarioReferencia = usuarioReferenciaRepository
                .findById(event.idUsuario())
                .orElse(new UsuarioReferencia());

        usuarioReferencia.setIdUsuario(event.idUsuario());
        usuarioReferencia.setNombre(event.nombre());
        usuarioReferencia.setEmail(event.email());
        usuarioReferencia.setIdRol(event.idRol());
        usuarioReferencia.setActivo(false);

        usuarioReferenciaRepository.save(usuarioReferencia);
    }

    private UsuarioEventDTO convertirMensajeAEvento(String mensajeJson) {
        try {
            return objectMapper.readValue(mensajeJson, UsuarioEventDTO.class);
        } catch (JsonProcessingException exception) {
            throw new AmqpRejectAndDontRequeueException(
                    "No fue posible convertir el mensaje de usuario a objeto",
                    exception
            );
        }
    }
}