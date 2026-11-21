package com.foodexpress.pedidos.application.dto;

import java.math.BigDecimal;

/** Valores configurables que intervienen en el cálculo del total de un pedido. */
public record ParametrosPedido(BigDecimal tasaImpuesto, BigDecimal costoDomicilio) {

    public ParametrosPedido {
        if (tasaImpuesto == null || tasaImpuesto.signum() < 0) {
            throw new IllegalArgumentException("La tasa de impuesto no puede ser negativa");
        }
        if (costoDomicilio == null || costoDomicilio.signum() < 0) {
            throw new IllegalArgumentException("El costo de domicilio no puede ser negativo");
        }
    }
}
