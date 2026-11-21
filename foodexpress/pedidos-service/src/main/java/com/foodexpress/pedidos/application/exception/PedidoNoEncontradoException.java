package com.foodexpress.pedidos.application.exception;

public class PedidoNoEncontradoException extends RuntimeException {

    public PedidoNoEncontradoException(Long pedidoId) {
        super("No existe el pedido " + pedidoId);
    }
}
