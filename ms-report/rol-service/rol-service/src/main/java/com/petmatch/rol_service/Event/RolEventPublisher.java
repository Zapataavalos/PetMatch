package com.petmatch.rol_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.rol_service.Config.RabbitMQConfig;
import com.petmatch.rol_service.Model.Rol;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RolEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RolEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void publicarRolCreado(Rol rol) {
        RolEventDTO event = new RolEventDTO(
                rol.getIdRol(),
                rol.getNombreRol(),
                true
        );

        publicarEvento(RabbitMQConfig.ROL_CREADO_ROUTING_KEY, event);
    }

    public void publicarRolActualizado(Rol rol) {
        RolEventDTO event = new RolEventDTO(
                rol.getIdRol(),
                rol.getNombreRol(),
                true
        );

        publicarEvento(RabbitMQConfig.ROL_ACTUALIZADO_ROUTING_KEY, event);
    }

    public void publicarRolEliminado(Rol rol) {
        RolEventDTO event = new RolEventDTO(
                rol.getIdRol(),
                rol.getNombreRol(),
                false
        );

        publicarEvento(RabbitMQConfig.ROL_ELIMINADO_ROUTING_KEY, event);
    }

    private void publicarEvento(String routingKey, RolEventDTO event) {
        try {
            String mensajeJson = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ROL_EXCHANGE,
                    routingKey,
                    mensajeJson
            );

        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Error al convertir el evento de rol a JSON", exception);
        }
    }
}