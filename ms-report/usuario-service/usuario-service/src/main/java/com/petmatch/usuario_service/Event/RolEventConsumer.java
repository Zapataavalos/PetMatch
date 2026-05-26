package com.petmatch.usuario_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.usuario_service.Config.RabbitMQConfig;
import com.petmatch.usuario_service.Model.RolReferencia;
import com.petmatch.usuario_service.Repository.RolReferenciaRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RolEventConsumer {

    private final RolReferenciaRepository rolReferenciaRepository;
    private final ObjectMapper objectMapper;

    public RolEventConsumer(RolReferenciaRepository rolReferenciaRepository) {
        this.rolReferenciaRepository = rolReferenciaRepository;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.ROL_CREADO_QUEUE)
    public void consumirRolCreado(String mensajeJson) {
        RolEventDTO event = convertirMensajeAEvento(mensajeJson);

        RolReferencia rolReferencia = new RolReferencia(
                event.idRol(),
                event.nombreRol(),
                true
        );

        rolReferenciaRepository.save(rolReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.ROL_ACTUALIZADO_QUEUE)
    public void consumirRolActualizado(String mensajeJson) {
        RolEventDTO event = convertirMensajeAEvento(mensajeJson);

        RolReferencia rolReferencia = rolReferenciaRepository
                .findById(event.idRol())
                .orElse(new RolReferencia());

        rolReferencia.setIdRol(event.idRol());
        rolReferencia.setNombreRol(event.nombreRol());
        rolReferencia.setActivo(true);

        rolReferenciaRepository.save(rolReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.ROL_ELIMINADO_QUEUE)
    public void consumirRolEliminado(String mensajeJson) {
        RolEventDTO event = convertirMensajeAEvento(mensajeJson);

        RolReferencia rolReferencia = rolReferenciaRepository
                .findById(event.idRol())
                .orElse(new RolReferencia());

        rolReferencia.setIdRol(event.idRol());
        rolReferencia.setNombreRol(event.nombreRol());
        rolReferencia.setActivo(false);

        rolReferenciaRepository.save(rolReferencia);
    }

    private RolEventDTO convertirMensajeAEvento(String mensajeJson) {
        try {
            return objectMapper.readValue(mensajeJson, RolEventDTO.class);
        } catch (JsonProcessingException exception) {
            throw new AmqpRejectAndDontRequeueException(
                    "No fue posible convertir el mensaje de rol a objeto",
                    exception
            );
        }
    }
}