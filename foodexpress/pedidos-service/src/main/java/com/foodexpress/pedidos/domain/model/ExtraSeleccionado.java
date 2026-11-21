package com.foodexpress.pedidos.domain.model;

import com.foodexpress.pedidos.domain.exception.PedidoInvalidoException;

import java.math.BigDecimal;

/**
 * Extra elegido para un ítem. Guarda una copia del nombre y del precio al momento
 * de la compra, de modo que un cambio posterior en el menú no altere el pedido.
 */
public record ExtraSeleccionado(Long extraId, String nombre, BigDecimal precioAdicional) {

    public ExtraSeleccionado {
        if (nombre == null || nombre.isBlank()) {
            throw new PedidoInvalidoException("El extra debe tener nombre");
        }
        if (precioAdicional == null || precioAdicional.signum() < 0) {
            throw new PedidoInvalidoException("El precio del extra no puede ser negativo");
        }
    }
}
