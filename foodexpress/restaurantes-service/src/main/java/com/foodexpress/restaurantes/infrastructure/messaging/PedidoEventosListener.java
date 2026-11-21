package com.foodexpress.restaurantes.infrastructure.messaging;

import com.foodexpress.restaurantes.application.port.in.InventarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adaptador de entrada: consume eventos de pedidos desde RabbitMQ.
 *
 * Cada mensaje se procesa en una sola transacción: el registro del evento y los
 * ajustes de inventario se confirman juntos o no se confirma ninguno. Si algo falla,
 * Spring reintenta (ver application.yml) y luego envía el mensaje a la cola de muertos.
 */
@Component
public class PedidoEventosListener {

    private static final Logger log = LoggerFactory.getLogger(PedidoEventosListener.class);

    private final InventarioService inventarioService;

    public PedidoEventosListener(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @RabbitListener(queues = RabbitConfig.COLA_PEDIDO_CREADO)
    @Transactional
    public void alCrearsePedido(PedidoEvento evento) {
        log.info("pedido.creado recibido: pedido={} eventId={}", evento.pedidoId(), evento.eventId());
        inventarioService.descontarPorPedidoCreado(evento.eventId(), evento.aMovimientos());
    }

    @RabbitListener(queues = RabbitConfig.COLA_PEDIDO_CANCELADO)
    @Transactional
    public void alCancelarsePedido(PedidoEvento evento) {
        log.info("pedido.cancelado recibido: pedido={} eventId={}", evento.pedidoId(), evento.eventId());
        inventarioService.reponerPorPedidoCancelado(evento.eventId(), evento.aMovimientos());
    }
}
