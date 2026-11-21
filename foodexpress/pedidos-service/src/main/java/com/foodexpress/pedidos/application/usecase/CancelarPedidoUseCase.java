package com.foodexpress.pedidos.application.usecase;

import com.foodexpress.pedidos.application.exception.PedidoNoEncontradoException;
import com.foodexpress.pedidos.application.port.out.PedidoEventPublisher;
import com.foodexpress.pedidos.application.port.out.PedidoRepository;
import com.foodexpress.pedidos.domain.model.Pedido;
import com.foodexpress.pedidos.domain.model.estado.ActorCancelacion;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedidoTipo;

/**
 * Cancela un pedido a solicitud del cliente o del restaurante. Si el estado actual
 * no lo permite, el dominio lanza TransicionInvalidaException y nada se guarda.
 */
public class CancelarPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PedidoEventPublisher eventPublisher;

    public CancelarPedidoUseCase(PedidoRepository pedidoRepository, PedidoEventPublisher eventPublisher) {
        this.pedidoRepository = pedidoRepository;
        this.eventPublisher = eventPublisher;
    }

    public Pedido ejecutar(Long pedidoId, ActorCancelacion actor, String motivo) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));

        EstadoPedidoTipo estadoAnterior = pedido.cancelar(actor, motivo);

        Pedido cancelado = pedidoRepository.guardar(pedido);
        eventPublisher.pedidoCancelado(cancelado, estadoAnterior);
        return cancelado;
    }
}
