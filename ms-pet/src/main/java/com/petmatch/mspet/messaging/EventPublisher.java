package com.petmatch.mspet.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class EventPublisher implements PetEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(String action, String entityType, Object data) {
        Map<String, Object> event = Map.of(
            "service",    "ms-pet",
            "action",     action,
            "entityType", entityType,
            "data",       data,
            "timestamp",  LocalDateTime.now().toString()
        );
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
            );
            log.info("[RabbitMQ] Publicado → action={} entity={}", action, entityType);
        } catch (Exception e) {
            log.error("[RabbitMQ] Error publicando: {}", e.getMessage());
        }
    }
}
