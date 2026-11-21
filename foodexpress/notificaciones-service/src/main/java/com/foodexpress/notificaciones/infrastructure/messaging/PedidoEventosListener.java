package com.foodexpress.notificaciones.infrastructure.messaging;

import com.foodexpress.notificaciones.application.usecase.NotificarCambioEstadoUseCase;
import com.foodexpress.notificaciones.application.usecase.NotificarPedidoCanceladoUseCase;
import com.foodexpress.notificaciones.application.usecase.NotificarPedidoCreadoUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adaptador de entrada: consume los tres eventos de pedidos desde RabbitMQ y
 * delega cada uno en su caso de uso.
 *
 * Cada mensaje se procesa en una transacción: si algo falla se revierte, Spring
 * reintenta (ver application.yml) y, agotados los reintentos, el mensaje pasa a
 * la cola de mensajes muertos.
 */
@Component
public class PedidoEventosListener {

    private static final Logger log = LoggerFactory.getLogger(PedidoEventosListener.class);

    private final NotificarPedidoCreadoUseCase notificarPedidoCreado;
    private final NotificarCambioEstadoUseCase notificarCambioEstado;
    private final NotificarPedidoCanceladoUseCase notificarPedidoCancelado;

    public PedidoEventosListener(NotificarPedidoCreadoUseCase notificarPedidoCreado,
                                 NotificarCambioEstadoUseCase notificarCambioEstado,
                                 NotificarPedidoCanceladoUseCase notificarPedidoCancelado) {
        this.notificarPedidoCreado = notificarPedidoCreado;
        this.notificarCambioEstado = notificarCambioEstado;
        this.notificarPedidoCancelado = notificarPedidoCancelado;
    }

    @RabbitListener(queues = RabbitConfig.COLA_PEDIDO_CREADO)
    @Transactional
    public void alCrearsePedido(PedidoEventoMensaje mensaje) {
        log.info("pedido.creado recibido: pedido={} eventId={}", mensaje.pedidoId(), mensaje.eventId());
        notificarPedidoCreado.ejecutar(mensaje.aEvento());
    }

    @RabbitListener(queues = RabbitConfig.COLA_ESTADO_CAMBIADO)
    @Transactional
    public void alCambiarEstado(PedidoEventoMensaje mensaje) {
        log.info("pedido.estado.cambiado recibido: pedido={} {} -> {}",
                mensaje.pedidoId(), mensaje.estadoAnterior(), mensaje.estadoNuevo());
        notificarCambioEstado.ejecutar(mensaje.aEvento());
    }

    @RabbitListener(queues = RabbitConfig.COLA_PEDIDO_CANCELADO)
    @Transactional
    public void alCancelarsePedido(PedidoEventoMensaje mensaje) {
        log.info("pedido.cancelado recibido: pedido={} por={}", mensaje.pedidoId(), mensaje.canceladoPor());
        notificarPedidoCancelado.ejecutar(mensaje.aEvento());
    }
}
