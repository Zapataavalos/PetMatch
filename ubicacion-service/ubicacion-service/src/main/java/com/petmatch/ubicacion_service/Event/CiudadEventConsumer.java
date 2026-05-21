package com.petmatch.ubicacion_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.ubicacion_service.Config.RabbitMQConfig;
import com.petmatch.ubicacion_service.Model.CiudadReferencia;
import com.petmatch.ubicacion_service.Repository.CiudadReferenciaRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CiudadEventConsumer {

    private final CiudadReferenciaRepository ciudadReferenciaRepository;
    private final ObjectMapper objectMapper;

    public CiudadEventConsumer(CiudadReferenciaRepository ciudadReferenciaRepository) {
        this.ciudadReferenciaRepository = ciudadReferenciaRepository;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.CIUDAD_CREADA_QUEUE)
    public void consumirCiudadCreada(String mensajeJson) {
        CiudadEventDTO event = convertirMensajeAEvento(mensajeJson);

        CiudadReferencia ciudadReferencia = new CiudadReferencia(
                event.idCiudad(),
                event.nombreCiudad(),
                event.idRegion(),
                true
        );

        ciudadReferenciaRepository.save(ciudadReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.CIUDAD_ACTUALIZADA_QUEUE)
    public void consumirCiudadActualizada(String mensajeJson) {
        CiudadEventDTO event = convertirMensajeAEvento(mensajeJson);

        CiudadReferencia ciudadReferencia = ciudadReferenciaRepository
                .findById(event.idCiudad())
                .orElse(new CiudadReferencia());

        ciudadReferencia.setIdCiudad(event.idCiudad());
        ciudadReferencia.setNombreCiudad(event.nombreCiudad());
        ciudadReferencia.setIdRegion(event.idRegion());
        ciudadReferencia.setActivo(true);

        ciudadReferenciaRepository.save(ciudadReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.CIUDAD_ELIMINADA_QUEUE)
    public void consumirCiudadEliminada(String mensajeJson) {
        CiudadEventDTO event = convertirMensajeAEvento(mensajeJson);

        CiudadReferencia ciudadReferencia = ciudadReferenciaRepository
                .findById(event.idCiudad())
                .orElse(new CiudadReferencia());

        ciudadReferencia.setIdCiudad(event.idCiudad());
        ciudadReferencia.setNombreCiudad(event.nombreCiudad());
        ciudadReferencia.setIdRegion(event.idRegion());
        ciudadReferencia.setActivo(false);

        ciudadReferenciaRepository.save(ciudadReferencia);
    }

    private CiudadEventDTO convertirMensajeAEvento(String mensajeJson) {
        try {
            return objectMapper.readValue(mensajeJson, CiudadEventDTO.class);
        } catch (JsonProcessingException exception) {
            throw new AmqpRejectAndDontRequeueException(
                    "No fue posible convertir el mensaje de ciudad a objeto",
                    exception
            );
        }
    }
}