package com.foodexpress.pedidos.domain.model.estado;

import com.foodexpress.pedidos.domain.exception.TransicionInvalidaException;

/** Estado final: el cliente ya recibió su pedido. */
public final class EstadoEntregado implements EstadoPedido {

    @Override
    public EstadoPedidoTipo tipo() {
        return EstadoPedidoTipo.ENTREGADO;
    }

    @Override
    public EstadoPedido siguiente() {
        throw new TransicionInvalidaException("El pedido ya fue entregado");
    }

    @Override
    public EstadoPedido cancelar(ActorCancelacion actor) {
        throw new TransicionInvalidaException("Un pedido entregado no puede cancelarse");
    }

    @Override
    public boolean esFinal() {
        return true;
    }
}
