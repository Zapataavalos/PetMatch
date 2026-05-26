package com.petmatch.ciudad_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.ciudad_service.Config.RabbitMQConfig;
import com.petmatch.ciudad_service.Model.Ciudad;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CiudadEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public CiudadEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void publicarCiudadCreada(Ciudad ciudad) {
        CiudadEventDTO event = new CiudadEventDTO(
                ciudad.getIdCiudad(),
                ciudad.getNombreCiudad(),
                ciudad.getIdRegion(),
                true
        );

        publicarEvento(RabbitMQConfig.CIUDAD_CREADA_ROUTING_KEY, event);
    }

    public void publicarCiudadActualizada(Ciudad ciudad) {
        CiudadEventDTO event = new CiudadEventDTO(
                ciudad.getIdCiudad(),
                ciudad.getNombreCiudad(),
                ciudad.getIdRegion(),
                true
        );

        publicarEvento(RabbitMQConfig.CIUDAD_ACTUALIZADA_ROUTING_KEY, event);
    }

    public void publicarCiudadEliminada(Ciudad ciudad) {
        CiudadEventDTO event = new CiudadEventDTO(
                ciudad.getIdCiudad(),
                ciudad.getNombreCiudad(),
                ciudad.getIdRegion(),
                false
        );

        publicarEvento(RabbitMQConfig.CIUDAD_ELIMINADA_ROUTING_KEY, event);
    }

    private void publicarEvento(String routingKey, CiudadEventDTO event) {
        try {
            String mensajeJson = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CIUDAD_EXCHANGE,
                    routingKey,
                    mensajeJson
            );

        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Error al convertir el evento de ciudad a JSON", exception);
        }
    }
}