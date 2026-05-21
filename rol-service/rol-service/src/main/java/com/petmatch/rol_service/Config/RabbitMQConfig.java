package com.petmatch.rol_service.Config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ROL_EXCHANGE = "rol.exchange";

    public static final String ROL_CREADO_ROUTING_KEY = "rol.creado";
    public static final String ROL_ACTUALIZADO_ROUTING_KEY = "rol.actualizado";
    public static final String ROL_ELIMINADO_ROUTING_KEY = "rol.eliminado";

    @Bean
    public DirectExchange rolExchange() {
        return new DirectExchange(ROL_EXCHANGE);
    }
}