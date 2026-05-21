package com.petmatch.ciudad_service.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String REGION_EXCHANGE = "region.exchange";

    public static final String REGION_CREADA_QUEUE = "ciudad.region.creada.queue";
    public static final String REGION_ACTUALIZADA_QUEUE = "ciudad.region.actualizada.queue";
    public static final String REGION_ELIMINADA_QUEUE = "ciudad.region.eliminada.queue";

    public static final String REGION_CREADA_ROUTING_KEY = "region.creada";
    public static final String REGION_ACTUALIZADA_ROUTING_KEY = "region.actualizada";
    public static final String REGION_ELIMINADA_ROUTING_KEY = "region.eliminada";

    @Bean
    public DirectExchange regionExchange() {
        return new DirectExchange(REGION_EXCHANGE);
    }

    @Bean
    public Queue regionCreadaQueue() {
        return new Queue(REGION_CREADA_QUEUE, true);
    }

    @Bean
    public Queue regionActualizadaQueue() {
        return new Queue(REGION_ACTUALIZADA_QUEUE, true);
    }

    @Bean
    public Queue regionEliminadaQueue() {
        return new Queue(REGION_ELIMINADA_QUEUE, true);
    }

    @Bean
    public Binding regionCreadaBinding() {
        return BindingBuilder
                .bind(regionCreadaQueue())
                .to(regionExchange())
                .with(REGION_CREADA_ROUTING_KEY);
    }

    @Bean
    public Binding regionActualizadaBinding() {
        return BindingBuilder
                .bind(regionActualizadaQueue())
                .to(regionExchange())
                .with(REGION_ACTUALIZADA_ROUTING_KEY);
    }

    @Bean
    public Binding regionEliminadaBinding() {
        return BindingBuilder
                .bind(regionEliminadaQueue())
                .to(regionExchange())
                .with(REGION_ELIMINADA_ROUTING_KEY);
    }
}