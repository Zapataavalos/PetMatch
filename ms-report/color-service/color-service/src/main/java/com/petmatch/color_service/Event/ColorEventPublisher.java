package com.petmatch.color_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.color_service.Config.RabbitMQConfig;
import com.petmatch.color_service.Model.Color;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ColorEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ColorEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void publicarColorCreado(Color color) {
        ColorEventDTO event = new ColorEventDTO(
                color.getIdColor(),
                color.getNombreColor(),
                color.getCodigoHexadecimal(),
                true
        );

        publicarEvento(RabbitMQConfig.COLOR_CREADO_ROUTING_KEY, event);
    }

    public void publicarColorActualizado(Color color) {
        ColorEventDTO event = new ColorEventDTO(
                color.getIdColor(),
                color.getNombreColor(),
                color.getCodigoHexadecimal(),
                true
        );

        publicarEvento(RabbitMQConfig.COLOR_ACTUALIZADO_ROUTING_KEY, event);
    }

    public void publicarColorEliminado(Color color) {
        ColorEventDTO event = new ColorEventDTO(
                color.getIdColor(),
                color.getNombreColor(),
                color.getCodigoHexadecimal(),
                false
        );

        publicarEvento(RabbitMQConfig.COLOR_ELIMINADO_ROUTING_KEY, event);
    }

    private void publicarEvento(String routingKey, ColorEventDTO event) {
        try {
            String mensajeJson = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.COLOR_EXCHANGE,
                    routingKey,
                    mensajeJson
            );

        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Error al convertir el evento de color a JSON", exception);
        }
    }
}