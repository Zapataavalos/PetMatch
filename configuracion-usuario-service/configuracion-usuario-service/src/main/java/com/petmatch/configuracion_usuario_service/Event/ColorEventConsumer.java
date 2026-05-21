package com.petmatch.configuracion_usuario_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petmatch.configuracion_usuario_service.Config.RabbitMQConfig;
import com.petmatch.configuracion_usuario_service.Model.ColorReferencia;
import com.petmatch.configuracion_usuario_service.Repository.ColorReferenciaRepository;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ColorEventConsumer {

    private final ColorReferenciaRepository colorReferenciaRepository;
    private final ObjectMapper objectMapper;

    public ColorEventConsumer(ColorReferenciaRepository colorReferenciaRepository) {
        this.colorReferenciaRepository = colorReferenciaRepository;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.COLOR_CREADO_QUEUE)
    public void consumirColorCreado(String mensajeJson) {
        ColorEventDTO event = convertirMensajeAEvento(mensajeJson);

        ColorReferencia colorReferencia = new ColorReferencia(
                event.idColor(),
                event.nombreColor(),
                event.codigoHexadecimal(),
                true
        );

        colorReferenciaRepository.save(colorReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.COLOR_ACTUALIZADO_QUEUE)
    public void consumirColorActualizado(String mensajeJson) {
        ColorEventDTO event = convertirMensajeAEvento(mensajeJson);

        ColorReferencia colorReferencia = colorReferenciaRepository
                .findById(event.idColor())
                .orElse(new ColorReferencia());

        colorReferencia.setIdColor(event.idColor());
        colorReferencia.setNombreColor(event.nombreColor());
        colorReferencia.setCodigoHexadecimal(event.codigoHexadecimal());
        colorReferencia.setActivo(true);

        colorReferenciaRepository.save(colorReferencia);
    }

    @RabbitListener(queues = RabbitMQConfig.COLOR_ELIMINADO_QUEUE)
    public void consumirColorEliminado(String mensajeJson) {
        ColorEventDTO event = convertirMensajeAEvento(mensajeJson);

        ColorReferencia colorReferencia = colorReferenciaRepository
                .findById(event.idColor())
                .orElse(new ColorReferencia());

        colorReferencia.setIdColor(event.idColor());
        colorReferencia.setNombreColor(event.nombreColor());
        colorReferencia.setCodigoHexadecimal(event.codigoHexadecimal());
        colorReferencia.setActivo(false);

        colorReferenciaRepository.save(colorReferencia);
    }

    private ColorEventDTO convertirMensajeAEvento(String mensajeJson) {
        try {
            return objectMapper.readValue(mensajeJson, ColorEventDTO.class);
        } catch (JsonProcessingException exception) {
            throw new AmqpRejectAndDontRequeueException(
                    "No fue posible convertir el mensaje de color a objeto",
                    exception
            );
        }
    }
}