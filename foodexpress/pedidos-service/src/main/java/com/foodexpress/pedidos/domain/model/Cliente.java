package com.foodexpress.pedidos.domain.model;

import com.foodexpress.pedidos.domain.exception.PedidoInvalidoException;

/** Datos de contacto del cliente que hace el pedido. */
public record Cliente(String nombre, String email) {

    public Cliente {
        if (nombre == null || nombre.isBlank()) {
            throw new PedidoInvalidoException("El nombre del cliente es obligatorio");
        }
        if (email == null || email.isBlank()) {
            throw new PedidoInvalidoException("El correo del cliente es obligatorio");
        }
    }
}
