package com.petmatch.usuario_service.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Eventos recibidos desde rol-service
    public static final String ROL_EXCHANGE = "rol.exchange";

    public static final String ROL_CREADO_QUEUE = "usuario.rol.creado.queue";
    public static final String ROL_ACTUALIZADO_QUEUE = "usuario.rol.actualizado.queue";
    public static final String ROL_ELIMINADO_QUEUE = "usuario.rol.eliminado.queue";

    public static final String ROL_CREADO_ROUTING_KEY = "rol.creado";
    public static final String ROL_ACTUALIZADO_ROUTING_KEY = "rol.actualizado";
    public static final String ROL_ELIMINADO_ROUTING_KEY = "rol.eliminado";

    // Eventos publicados hacia configuracion-usuario-service
    public static final String USUARIO_EXCHANGE = "usuario.exchange";

    public static final String USUARIO_CREADO_ROUTING_KEY = "usuario.creado";
    public static final String USUARIO_ACTUALIZADO_ROUTING_KEY = "usuario.actualizado";
    public static final String USUARIO_ELIMINADO_ROUTING_KEY = "usuario.eliminado";

    @Bean
    public DirectExchange rolExchange() {
        return new DirectExchange(ROL_EXCHANGE);
    }

    @Bean
    public DirectExchange usuarioExchange() {
        return new DirectExchange(USUARIO_EXCHANGE);
    }

    @Bean
    public Queue rolCreadoQueue() {
        return new Queue(ROL_CREADO_QUEUE, true);
    }

    @Bean
    public Queue rolActualizadoQueue() {
        return new Queue(ROL_ACTUALIZADO_QUEUE, true);
    }

    @Bean
    public Queue rolEliminadoQueue() {
        return new Queue(ROL_ELIMINADO_QUEUE, true);
    }

    @Bean
    public Binding rolCreadoBinding() {
        return BindingBuilder
                .bind(rolCreadoQueue())
                .to(rolExchange())
                .with(ROL_CREADO_ROUTING_KEY);
    }

    @Bean
    public Binding rolActualizadoBinding() {
        return BindingBuilder
                .bind(rolActualizadoQueue())
                .to(rolExchange())
                .with(ROL_ACTUALIZADO_ROUTING_KEY);
    }

    @Bean
    public Binding rolEliminadoBinding() {
        return BindingBuilder
                .bind(rolEliminadoQueue())
                .to(rolExchange())
                .with(ROL_ELIMINADO_ROUTING_KEY);
    }
}