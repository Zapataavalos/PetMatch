package com.petmatch.region_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.region_service.Config.RabbitMQConfig;
import com.petmatch.region_service.Model.PaisReferencia;
import com.petmatch.region_service.Repository.PaisReferenciaRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaisEventConsumer {

    private final PaisReferenciaRepository paisReferenciaRepository;
    private final ObjectMapper objectMapper;

    public PaisEventConsumer(PaisReferenciaRepository paisReferenciaRepository) {
        this.paisReferenciaRepository = paisReferenciaRepository;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.PAIS_CREADO_QUEUE)
    public void consumirPaisCreado(String mensajeJson) {
        PaisEventDTO event = convertirMensajeAEvento(mensajeJson);

        PaisReferencia paisReferencia = new PaisReferencia(
                event.idPais(),
                event.nombrePais(),
                true
        );

        paisReferenciaRepository.save(paisReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.PAIS_ACTUALIZADO_QUEUE)
    public void consumirPaisActualizado(String mensajeJson) {
        PaisEventDTO event = convertirMensajeAEvento(mensajeJson);

        PaisReferencia paisReferencia = paisReferenciaRepository
                .findById(event.idPais())
                .orElse(new PaisReferencia());

        paisReferencia.setIdPais(event.idPais());
        paisReferencia.setNombrePais(event.nombrePais());
        paisReferencia.setActivo(true);

        paisReferenciaRepository.save(paisReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.PAIS_ELIMINADO_QUEUE)
    public void consumirPaisEliminado(String mensajeJson) {
        PaisEventDTO event = convertirMensajeAEvento(mensajeJson);

        PaisReferencia paisReferencia = paisReferenciaRepository
                .findById(event.idPais())
                .orElse(new PaisReferencia());

        paisReferencia.setIdPais(event.idPais());
        paisReferencia.setNombrePais(event.nombrePais());
        paisReferencia.setActivo(false);

        paisReferenciaRepository.save(paisReferencia);
    }

    private PaisEventDTO convertirMensajeAEvento(String mensajeJson) {
        try {
            return objectMapper.readValue(mensajeJson, PaisEventDTO.class);
        } catch (JsonProcessingException exception) {
            throw new AmqpRejectAndDontRequeueException(
                    "No fue posible convertir el mensaje de país a objeto",
                    exception
            );
        }
    }
}