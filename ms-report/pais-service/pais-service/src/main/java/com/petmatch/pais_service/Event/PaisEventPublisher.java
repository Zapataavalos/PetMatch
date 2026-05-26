package com.petmatch.pais_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.pais_service.Config.RabbitMQConfig;
import com.petmatch.pais_service.Model.Pais;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaisEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public PaisEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void publicarPaisCreado(Pais pais) {
        PaisEventDTO event = new PaisEventDTO(
                pais.getIdPais(),
                pais.getNombrePais(),
                true
        );

        publicarEvento(RabbitMQConfig.PAIS_CREADO_ROUTING_KEY, event);
    }

    public void publicarPaisActualizado(Pais pais) {
        PaisEventDTO event = new PaisEventDTO(
                pais.getIdPais(),
                pais.getNombrePais(),
                true
        );

        publicarEvento(RabbitMQConfig.PAIS_ACTUALIZADO_ROUTING_KEY, event);
    }

    public void publicarPaisEliminado(Pais pais) {
        PaisEventDTO event = new PaisEventDTO(
                pais.getIdPais(),
                pais.getNombrePais(),
                false
        );

        publicarEvento(RabbitMQConfig.PAIS_ELIMINADO_ROUTING_KEY, event);
    }

    private void publicarEvento(String routingKey, PaisEventDTO event) {
        try {
            String mensajeJson = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAIS_EXCHANGE,
                    routingKey,
                    mensajeJson
            );

        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Error al convertir el evento de país a JSON", exception);
        }
    }
}