package com.petmatch.region_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.region_service.Config.RabbitMQConfig;
import com.petmatch.region_service.Model.Region;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RegionEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RegionEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void publicarRegionCreada(Region region) {
        RegionEventDTO event = new RegionEventDTO(
                region.getIdRegion(),
                region.getNombreRegion(),
                region.getIdPais(),
                true
        );

        publicarEvento(RabbitMQConfig.REGION_CREADA_ROUTING_KEY, event);
    }

    public void publicarRegionActualizada(Region region) {
        RegionEventDTO event = new RegionEventDTO(
                region.getIdRegion(),
                region.getNombreRegion(),
                region.getIdPais(),
                true
        );

        publicarEvento(RabbitMQConfig.REGION_ACTUALIZADA_ROUTING_KEY, event);
    }

    public void publicarRegionEliminada(Region region) {
        RegionEventDTO event = new RegionEventDTO(
                region.getIdRegion(),
                region.getNombreRegion(),
                region.getIdPais(),
                false
        );

        publicarEvento(RabbitMQConfig.REGION_ELIMINADA_ROUTING_KEY, event);
    }

    private void publicarEvento(String routingKey, RegionEventDTO event) {
        try {
            String mensajeJson = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.REGION_EXCHANGE,
                    routingKey,
                    mensajeJson
            );

        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Error al convertir el evento de región a JSON", exception);
        }
    }
}