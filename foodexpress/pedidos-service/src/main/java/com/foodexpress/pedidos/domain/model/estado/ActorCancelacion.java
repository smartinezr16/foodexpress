package com.foodexpress.pedidos.domain.model.estado;

/** Quién solicita la cancelación de un pedido. Cada estado decide si se lo permite. */
public enum ActorCancelacion {
    CLIENTE,
    RESTAURANTE
}
