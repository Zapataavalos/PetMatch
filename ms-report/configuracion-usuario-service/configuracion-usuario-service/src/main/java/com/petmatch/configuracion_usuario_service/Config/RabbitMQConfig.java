package com.petmatch.configuracion_usuario_service.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USUARIO_EXCHANGE = "usuario.exchange";
    public static final String COLOR_EXCHANGE = "color.exchange";

    public static final String USUARIO_CREADO_QUEUE = "config.usuario.creado.queue";
    public static final String USUARIO_ACTUALIZADO_QUEUE = "config.usuario.actualizado.queue";
    public static final String USUARIO_ELIMINADO_QUEUE = "config.usuario.eliminado.queue";

    public static final String COLOR_CREADO_QUEUE = "config.color.creado.queue";
    public static final String COLOR_ACTUALIZADO_QUEUE = "config.color.actualizado.queue";
    public static final String COLOR_ELIMINADO_QUEUE = "config.color.eliminado.queue";

    public static final String USUARIO_CREADO_ROUTING_KEY = "usuario.creado";
    public static final String USUARIO_ACTUALIZADO_ROUTING_KEY = "usuario.actualizado";
    public static final String USUARIO_ELIMINADO_ROUTING_KEY = "usuario.eliminado";

    public static final String COLOR_CREADO_ROUTING_KEY = "color.creado";
    public static final String COLOR_ACTUALIZADO_ROUTING_KEY = "color.actualizado";
    public static final String COLOR_ELIMINADO_ROUTING_KEY = "color.eliminado";

    @Bean
    public DirectExchange usuarioExchange() {
        return new DirectExchange(USUARIO_EXCHANGE);
    }

    @Bean
    public DirectExchange colorExchange() {
        return new DirectExchange(COLOR_EXCHANGE);
    }

    @Bean
    public Queue usuarioCreadoQueue() {
        return new Queue(USUARIO_CREADO_QUEUE, true);
    }

    @Bean
    public Queue usuarioActualizadoQueue() {
        return new Queue(USUARIO_ACTUALIZADO_QUEUE, true);
    }

    @Bean
    public Queue usuarioEliminadoQueue() {
        return new Queue(USUARIO_ELIMINADO_QUEUE, true);
    }

    @Bean
    public Queue colorCreadoQueue() {
        return new Queue(COLOR_CREADO_QUEUE, true);
    }

    @Bean
    public Queue colorActualizadoQueue() {
        return new Queue(COLOR_ACTUALIZADO_QUEUE, true);
    }

    @Bean
    public Queue colorEliminadoQueue() {
        return new Queue(COLOR_ELIMINADO_QUEUE, true);
    }

    @Bean
    public Binding usuarioCreadoBinding() {
        return BindingBuilder.bind(usuarioCreadoQueue()).to(usuarioExchange()).with(USUARIO_CREADO_ROUTING_KEY);
    }

    @Bean
    public Binding usuarioActualizadoBinding() {
        return BindingBuilder.bind(usuarioActualizadoQueue()).to(usuarioExchange()).with(USUARIO_ACTUALIZADO_ROUTING_KEY);
    }

    @Bean
    public Binding usuarioEliminadoBinding() {
        return BindingBuilder.bind(usuarioEliminadoQueue()).to(usuarioExchange()).with(USUARIO_ELIMINADO_ROUTING_KEY);
    }

    @Bean
    public Binding colorCreadoBinding() {
        return BindingBuilder.bind(colorCreadoQueue()).to(colorExchange()).with(COLOR_CREADO_ROUTING_KEY);
    }

    @Bean
    public Binding colorActualizadoBinding() {
        return BindingBuilder.bind(colorActualizadoQueue()).to(colorExchange()).with(COLOR_ACTUALIZADO_ROUTING_KEY);
    }

    @Bean
    public Binding colorEliminadoBinding() {
        return BindingBuilder.bind(colorEliminadoQueue()).to(colorExchange()).with(COLOR_ELIMINADO_ROUTING_KEY);
    }
}