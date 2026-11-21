package com.foodexpress.pedidos.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topología de RabbitMQ desde el lado del publicador.
 *
 * Pedidos solo conoce el exchange donde anuncia sus eventos. Las colas las declara
 * cada servicio consumidor, así que agregar un consumidor nuevo no exige cambiar
 * este servicio.
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_PEDIDOS = "foodexpress.pedidos";

    public static final String RK_PEDIDO_CREADO = "pedido.creado";
    public static final String RK_PEDIDO_ESTADO_CAMBIADO = "pedido.estado.cambiado";
    public static final String RK_PEDIDO_CANCELADO = "pedido.cancelado";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE_PEDIDOS, true, false);
    }

    /** Los eventos viajan como JSON para que cualquier servicio pueda leerlos. */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
