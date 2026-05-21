package com.petmatch.region_service.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAIS_EXCHANGE = "pais.exchange";

    public static final String PAIS_CREADO_QUEUE = "region.pais.creado.queue";
    public static final String PAIS_ACTUALIZADO_QUEUE = "region.pais.actualizado.queue";
    public static final String PAIS_ELIMINADO_QUEUE = "region.pais.eliminado.queue";

    public static final String PAIS_CREADO_ROUTING_KEY = "pais.creado";
    public static final String PAIS_ACTUALIZADO_ROUTING_KEY = "pais.actualizado";
    public static final String PAIS_ELIMINADO_ROUTING_KEY = "pais.eliminado";

    @Bean
    public DirectExchange paisExchange() {
        return new DirectExchange(PAIS_EXCHANGE);
    }

    @Bean
    public Queue paisCreadoQueue() {
        return new Queue(PAIS_CREADO_QUEUE, true);
    }

    @Bean
    public Queue paisActualizadoQueue() {
        return new Queue(PAIS_ACTUALIZADO_QUEUE, true);
    }

    @Bean
    public Queue paisEliminadoQueue() {
        return new Queue(PAIS_ELIMINADO_QUEUE, true);
    }

    @Bean
    public Binding paisCreadoBinding() {
        return BindingBuilder
                .bind(paisCreadoQueue())
                .to(paisExchange())
                .with(PAIS_CREADO_ROUTING_KEY);
    }

    @Bean
    public Binding paisActualizadoBinding() {
        return BindingBuilder
                .bind(paisActualizadoQueue())
                .to(paisExchange())
                .with(PAIS_ACTUALIZADO_ROUTING_KEY);
    }

    @Bean
    public Binding paisEliminadoBinding() {
        return BindingBuilder
                .bind(paisEliminadoQueue())
                .to(paisExchange())
                .with(PAIS_ELIMINADO_ROUTING_KEY);
    }
}