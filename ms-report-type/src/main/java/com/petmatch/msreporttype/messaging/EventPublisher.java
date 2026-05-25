package com.petmatch.msreporttype.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(String action, String entityType, Object data) {
        Map<String, Object> event = Map.of(
            "service",    "ms-report-type",
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
