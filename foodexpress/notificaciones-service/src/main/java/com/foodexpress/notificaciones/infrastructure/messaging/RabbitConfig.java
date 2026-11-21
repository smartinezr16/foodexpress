package com.foodexpress.notificaciones.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topología de RabbitMQ para el servicio de Notificaciones: una cola durable por
 * cada evento de pedidos.
 *
 * Atributo de calidad DISPONIBILIDAD: si este servicio está caído, los mensajes
 * se acumulan en las colas y se procesan cuando vuelve a arrancar. Pedidos sigue
 * funcionando mientras tanto.
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_PEDIDOS = "foodexpress.pedidos";
    public static final String EXCHANGE_MUERTOS = "foodexpress.pedidos.dlx";

    public static final String COLA_PEDIDO_CREADO = "notificaciones.pedido.creado";
    public static final String COLA_ESTADO_CAMBIADO = "notificaciones.pedido.estado.cambiado";
    public static final String COLA_PEDIDO_CANCELADO = "notificaciones.pedido.cancelado";
    public static final String COLA_MUERTOS = "notificaciones.dlq";

    private static final String RK_MUERTOS = "notificaciones";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE_PEDIDOS, true, false);
    }

    @Bean
    public DirectExchange mensajesMuertosExchange() {
        return new DirectExchange(EXCHANGE_MUERTOS, true, false);
    }

    @Bean
    public Queue colaPedidoCreado() {
        return colaConMuertos(COLA_PEDIDO_CREADO);
    }

    @Bean
    public Queue colaEstadoCambiado() {
        return colaConMuertos(COLA_ESTADO_CAMBIADO);
    }

    @Bean
    public Queue colaPedidoCancelado() {
        return colaConMuertos(COLA_PEDIDO_CANCELADO);
    }

    @Bean
    public Queue colaMensajesMuertos() {
        return QueueBuilder.durable(COLA_MUERTOS).build();
    }

    @Bean
    public Binding bindingPedidoCreado() {
        return BindingBuilder.bind(colaPedidoCreado()).to(pedidosExchange()).with("pedido.creado");
    }

    @Bean
    public Binding bindingEstadoCambiado() {
        return BindingBuilder.bind(colaEstadoCambiado()).to(pedidosExchange()).with("pedido.estado.cambiado");
    }

    @Bean
    public Binding bindingPedidoCancelado() {
        return BindingBuilder.bind(colaPedidoCancelado()).to(pedidosExchange()).with("pedido.cancelado");
    }

    @Bean
    public Binding bindingMensajesMuertos() {
        return BindingBuilder.bind(colaMensajesMuertos()).to(mensajesMuertosExchange()).with(RK_MUERTOS);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /** Cola durable cuyos mensajes rechazados (tras los reintentos) pasan a la cola de muertos. */
    private Queue colaConMuertos(String nombre) {
        return QueueBuilder.durable(nombre)
                .deadLetterExchange(EXCHANGE_MUERTOS)
                .deadLetterRoutingKey(RK_MUERTOS)
                .build();
    }
}
