package com.petmatch.ciudad_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.ciudad_service.Config.RabbitMQConfig;
import com.petmatch.ciudad_service.Model.RegionReferencia;
import com.petmatch.ciudad_service.Repository.RegionReferenciaRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RegionEventConsumer {

    private final RegionReferenciaRepository regionReferenciaRepository;
    private final ObjectMapper objectMapper;

    public RegionEventConsumer(RegionReferenciaRepository regionReferenciaRepository) {
        this.regionReferenciaRepository = regionReferenciaRepository;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.REGION_CREADA_QUEUE)
    public void consumirRegionCreada(String mensajeJson) {
        RegionEventDTO event = convertirMensajeAEvento(mensajeJson);

        RegionReferencia regionReferencia = new RegionReferencia(
                event.idRegion(),
                event.nombreRegion(),
                event.idPais(),
                true
        );

        regionReferenciaRepository.save(regionReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.REGION_ACTUALIZADA_QUEUE)
    public void consumirRegionActualizada(String mensajeJson) {
        RegionEventDTO event = convertirMensajeAEvento(mensajeJson);

        RegionReferencia regionReferencia = regionReferenciaRepository
                .findById(event.idRegion())
                .orElse(new RegionReferencia());

        regionReferencia.setIdRegion(event.idRegion());
        regionReferencia.setNombreRegion(event.nombreRegion());
        regionReferencia.setIdPais(event.idPais());
        regionReferencia.setActivo(true);

        regionReferenciaRepository.save(regionReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.REGION_ELIMINADA_QUEUE)
    public void consumirRegionEliminada(String mensajeJson) {
        RegionEventDTO event = convertirMensajeAEvento(mensajeJson);

        RegionReferencia regionReferencia = regionReferenciaRepository
                .findById(event.idRegion())
                .orElse(new RegionReferencia());

        regionReferencia.setIdRegion(event.idRegion());
        regionReferencia.setNombreRegion(event.nombreRegion());
        regionReferencia.setIdPais(event.idPais());
        regionReferencia.setActivo(false);

        regionReferenciaRepository.save(regionReferencia);
    }

    private RegionEventDTO convertirMensajeAEvento(String mensajeJson) {
        try {
            return objectMapper.readValue(mensajeJson, RegionEventDTO.class);
        } catch (JsonProcessingException exception) {
            throw new AmqpRejectAndDontRequeueException(
                    "No fue posible convertir el mensaje de región a objeto",
                    exception
            );
        }
    }
}