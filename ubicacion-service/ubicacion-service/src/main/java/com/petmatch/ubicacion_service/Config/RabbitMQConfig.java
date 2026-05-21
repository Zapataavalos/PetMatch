package com.petmatch.ubicacion_service.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CIUDAD_EXCHANGE = "ciudad.exchange";

    public static final String CIUDAD_CREADA_QUEUE = "ubicacion.ciudad.creada.queue";
    public static final String CIUDAD_ACTUALIZADA_QUEUE = "ubicacion.ciudad.actualizada.queue";
    public static final String CIUDAD_ELIMINADA_QUEUE = "ubicacion.ciudad.eliminada.queue";

    public static final String CIUDAD_CREADA_ROUTING_KEY = "ciudad.creada";
    public static final String CIUDAD_ACTUALIZADA_ROUTING_KEY = "ciudad.actualizada";
    public static final String CIUDAD_ELIMINADA_ROUTING_KEY = "ciudad.eliminada";

    @Bean
    public DirectExchange ciudadExchange() {
        return new DirectExchange(CIUDAD_EXCHANGE);
    }

    @Bean
    public Queue ciudadCreadaQueue() {
        return new Queue(CIUDAD_CREADA_QUEUE, true);
    }

    @Bean
    public Queue ciudadActualizadaQueue() {
        return new Queue(CIUDAD_ACTUALIZADA_QUEUE, true);
    }

    @Bean
    public Queue ciudadEliminadaQueue() {
        return new Queue(CIUDAD_ELIMINADA_QUEUE, true);
    }

    @Bean
    public Binding ciudadCreadaBinding() {
        return BindingBuilder
                .bind(ciudadCreadaQueue())
                .to(ciudadExchange())
                .with(CIUDAD_CREADA_ROUTING_KEY);
    }

    @Bean
    public Binding ciudadActualizadaBinding() {
        return BindingBuilder
                .bind(ciudadActualizadaQueue())
                .to(ciudadExchange())
                .with(CIUDAD_ACTUALIZADA_ROUTING_KEY);
    }

    @Bean
    public Binding ciudadEliminadaBinding() {
        return BindingBuilder
                .bind(ciudadEliminadaQueue())
                .to(ciudadExchange())
                .with(CIUDAD_ELIMINADA_ROUTING_KEY);
    }
}