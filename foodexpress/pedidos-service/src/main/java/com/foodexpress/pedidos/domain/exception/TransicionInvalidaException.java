package com.foodexpress.pedidos.domain.exception;

/** Se intentó una transición que la máquina de estados del pedido no permite. */
public class TransicionInvalidaException extends DominioException {

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
