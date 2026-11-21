package com.foodexpress.pedidos.infrastructure.messaging;

import com.foodexpress.pedidos.application.port.out.PedidoEventPublisher;
import com.foodexpress.pedidos.domain.model.Pedido;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedidoTipo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Adaptador de salida: publica los eventos del pedido en RabbitMQ. */
@Component
public class PedidoEventPublisherRabbit implements PedidoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PedidoEventPublisherRabbit.class);

    private final RabbitTemplate rabbitTemplate;

    public PedidoEventPublisherRabbit(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void pedidoCreado(Pedido pedido) {
        publicar(RabbitConfig.RK_PEDIDO_CREADO, pedido, null);
    }

    @Override
    public void estadoCambiado(Pedido pedido, EstadoPedidoTipo estadoAnterior) {
        publicar(RabbitConfig.RK_PEDIDO_ESTADO_CAMBIADO, pedido, estadoAnterior);
    }

    @Override
    public void pedidoCancelado(Pedido pedido, EstadoPedidoTipo estadoAnterior) {
        publicar(RabbitConfig.RK_PEDIDO_CANCELADO, pedido, estadoAnterior);
    }

    private void publicar(String routingKey, Pedido pedido, EstadoPedidoTipo estadoAnterior) {
        PedidoEvento evento = PedidoEvento.de(routingKey, pedido, estadoAnterior);
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_PEDIDOS, routingKey, evento);
            log.info("Evento publicado: {} pedido={} eventId={}", routingKey, pedido.getId(), evento.eventId());
        } catch (AmqpException e) {
            // El pedido ya quedó guardado: no se le falla al cliente porque el broker no responda.
            // Limitación conocida: este evento se pierde. La solución de fondo es el patrón Outbox.
            log.error("No se pudo publicar {} para el pedido {}", routingKey, pedido.getId(), e);
        }
    }
}
