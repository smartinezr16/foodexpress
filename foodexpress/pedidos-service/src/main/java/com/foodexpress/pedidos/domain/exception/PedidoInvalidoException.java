package com.foodexpress.pedidos.domain.exception;

/** El pedido que se intenta construir no cumple las reglas mínimas del dominio. */
public class PedidoInvalidoException extends DominioException {

    public PedidoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
