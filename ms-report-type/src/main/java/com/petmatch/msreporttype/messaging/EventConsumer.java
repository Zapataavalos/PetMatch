package com.petmatch.msreporttype.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Slf4j
@Component
public class EventConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleEvent(Map<String, Object> event) {
        log.info("[RabbitMQ] Recibido ← service={} action={} entity={}",
            event.get("service"), event.get("action"), event.get("entityType"));
    }
}
