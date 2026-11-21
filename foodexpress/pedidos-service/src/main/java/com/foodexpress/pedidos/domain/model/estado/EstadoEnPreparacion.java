package com.foodexpress.pedidos.domain.model.estado;

import com.foodexpress.pedidos.domain.exception.TransicionInvalidaException;

/** La cocina ya está trabajando: solo el restaurante puede cancelar. */
public final class EstadoEnPreparacion implements EstadoPedido {

    @Override
    public EstadoPedidoTipo tipo() {
        return EstadoPedidoTipo.EN_PREPARACION;
    }

    @Override
    public EstadoPedido siguiente() {
        return new EstadoListo();
    }

    @Override
    public EstadoPedido cancelar(ActorCancelacion actor) {
        if (actor != ActorCancelacion.RESTAURANTE) {
            throw new TransicionInvalidaException(
                    "El pedido ya está en preparación: solo el restaurante puede cancelarlo");
        }
        return new EstadoCancelado();
    }
}
