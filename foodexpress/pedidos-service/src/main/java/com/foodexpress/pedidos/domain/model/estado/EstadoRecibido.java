package com.foodexpress.pedidos.domain.model.estado;

/** El restaurante aún no empieza a cocinar: cualquiera de las partes puede cancelar. */
public final class EstadoRecibido implements EstadoPedido {

    @Override
    public EstadoPedidoTipo tipo() {
        return EstadoPedidoTipo.RECIBIDO;
    }

    @Override
    public EstadoPedido siguiente() {
        return new EstadoEnPreparacion();
    }

    @Override
    public EstadoPedido cancelar(ActorCancelacion actor) {
        return new EstadoCancelado();
    }
}
