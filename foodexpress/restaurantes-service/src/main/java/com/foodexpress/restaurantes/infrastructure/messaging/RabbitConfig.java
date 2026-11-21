package com.foodexpress.restaurantes.infrastructure.messaging;

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
 * Topología de RabbitMQ para el servicio de Restaurantes.
 *
 * Este servicio escucha dos eventos de pedidos para mantener el inventario:
 * pedido.creado (descuenta unidades) y pedido.cancelado (las devuelve).
 * Los mensajes que fallan tras los reintentos van a una cola de mensajes muertos.
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_PEDIDOS = "foodexpress.pedidos";
    public static final String EXCHANGE_MUERTOS = "foodexpress.pedidos.dlx";

    public static final String COLA_PEDIDO_CREADO = "restaurantes.pedido.creado";
    public static final String COLA_PEDIDO_CANCELADO = "restaurantes.pedido.cancelado";
    public static final String COLA_MUERTOS = "restaurantes.dlq";

    private static final String RK_MUERTOS = "restaurantes";

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
        return QueueBuilder.durable(COLA_PEDIDO_CREADO)
                .deadLetterExchange(EXCHANGE_MUERTOS)
                .deadLetterRoutingKey(RK_MUERTOS)
                .build();
    }

    @Bean
    public Queue colaPedidoCancelado() {
        return QueueBuilder.durable(COLA_PEDIDO_CANCELADO)
                .deadLetterExchange(EXCHANGE_MUERTOS)
                .deadLetterRoutingKey(RK_MUERTOS)
                .build();
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
}
