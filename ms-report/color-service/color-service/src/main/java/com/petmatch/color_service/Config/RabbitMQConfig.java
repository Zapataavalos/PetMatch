package com.petmatch.color_service.Config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLOR_EXCHANGE = "color.exchange";

    public static final String COLOR_CREADO_ROUTING_KEY = "color.creado";
    public static final String COLOR_ACTUALIZADO_ROUTING_KEY = "color.actualizado";
    public static final String COLOR_ELIMINADO_ROUTING_KEY = "color.eliminado";

    @Bean
    public DirectExchange colorExchange() {
        return new DirectExchange(COLOR_EXCHANGE);
    }
}