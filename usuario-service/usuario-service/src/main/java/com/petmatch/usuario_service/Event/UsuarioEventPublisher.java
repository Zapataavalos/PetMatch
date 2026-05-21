package com.petmatch.usuario_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.usuario_service.Config.RabbitMQConfig;
import com.petmatch.usuario_service.Model.Usuario;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public UsuarioEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void publicarUsuarioCreado(Usuario usuario) {
        UsuarioEventDTO event = new UsuarioEventDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getIdRol(),
                true
        );

        publicarEvento(RabbitMQConfig.USUARIO_CREADO_ROUTING_KEY, event);
    }

    public void publicarUsuarioActualizado(Usuario usuario) {
        UsuarioEventDTO event = new UsuarioEventDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getIdRol(),
                true
        );

        publicarEvento(RabbitMQConfig.USUARIO_ACTUALIZADO_ROUTING_KEY, event);
    }

    public void publicarUsuarioEliminado(Usuario usuario) {
        UsuarioEventDTO event = new UsuarioEventDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getIdRol(),
                false
        );

        publicarEvento(RabbitMQConfig.USUARIO_ELIMINADO_ROUTING_KEY, event);
    }

    private void publicarEvento(String routingKey, UsuarioEventDTO event) {
        try {
            String mensajeJson = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USUARIO_EXCHANGE,
                    routingKey,
                    mensajeJson
            );

        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Error al convertir el evento de usuario a JSON", exception);
        }
    }
}