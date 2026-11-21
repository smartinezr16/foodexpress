package com.foodexpress.restaurantes.application.dto;

/** Unidades de un plato que entran o salen del inventario por causa de un pedido. */
public record MovimientoInventario(Long platoId, int cantidad) {
}
