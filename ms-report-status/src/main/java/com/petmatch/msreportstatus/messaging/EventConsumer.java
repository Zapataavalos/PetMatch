package com.petmatch.msreportstatus.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleEvent(Map<String, Object> event) {
        log.info("[RabbitMQ] Recibido ← service={} action={} entity={}",
            event.get("service"), event.get("action"), event.get("entityType"));
    }
}
