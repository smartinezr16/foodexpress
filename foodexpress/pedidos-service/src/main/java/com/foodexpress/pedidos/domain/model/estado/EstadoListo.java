package com.foodexpress.pedidos.domain.model.estado;

import com.foodexpress.pedidos.domain.exception.TransicionInvalidaException;

/** La comida ya está preparada: el pedido solo puede entregarse. */
public final class EstadoListo implements EstadoPedido {

    @Override
    public EstadoPedidoTipo tipo() {
        return EstadoPedidoTipo.LISTO;
    }

    @Override
    public EstadoPedido siguiente() {
        return new EstadoEntregado();
    }

    @Override
    public EstadoPedido cancelar(ActorCancelacion actor) {
        throw new TransicionInvalidaException("El pedido ya está listo y no puede cancelarse");
    }
}
