package com.foodexpress.pedidos.domain.model;

import java.math.BigDecimal;

/** Pedido que el cliente recoge en el restaurante: no tiene costo de envío. */
public final class PedidoLocal extends Pedido {

    PedidoLocal(PedidoBuilder builder) {
        super(builder);
    }

    @Override
    public TipoPedido getTipo() {
        return TipoPedido.LOCAL;
    }

    @Override
    public BigDecimal costoEnvio() {
        return BigDecimal.ZERO;
    }
}
