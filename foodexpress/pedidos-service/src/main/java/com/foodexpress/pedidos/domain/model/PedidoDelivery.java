package com.foodexpress.pedidos.domain.model;

import java.math.BigDecimal;
import java.util.Optional;

/** Pedido a domicilio: agrega dirección de entrega y costo de envío. */
public final class PedidoDelivery extends Pedido {

    private final String direccionEntrega;
    private final BigDecimal costoEnvio;

    PedidoDelivery(PedidoBuilder builder) {
        super(builder);
        this.direccionEntrega = builder.direccionEntrega;
        this.costoEnvio = builder.costoEnvio;
    }

    @Override
    public TipoPedido getTipo() {
        return TipoPedido.DELIVERY;
    }

    @Override
    public BigDecimal costoEnvio() {
        return costoEnvio;
    }

    @Override
    public Optional<String> direccionEntrega() {
        return Optional.of(direccionEntrega);
    }
}
