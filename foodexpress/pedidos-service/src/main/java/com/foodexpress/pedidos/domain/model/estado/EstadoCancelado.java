package com.foodexpress.pedidos.domain.model.estado;

import com.foodexpress.pedidos.domain.exception.TransicionInvalidaException;

/** Estado final: el pedido fue cancelado por el cliente o por el restaurante. */
public final class EstadoCancelado implements EstadoPedido {

    @Override
    public EstadoPedidoTipo tipo() {
        return EstadoPedidoTipo.CANCELADO;
    }

    @Override
    public EstadoPedido siguiente() {
        throw new TransicionInvalidaException("Un pedido cancelado no puede avanzar");
    }

    @Override
    public EstadoPedido cancelar(ActorCancelacion actor) {
        throw new TransicionInvalidaException("El pedido ya estaba cancelado");
    }

    @Override
    public boolean esFinal() {
        return true;
    }
}
