package com.foodexpress.pedidos.application.exception;

/** Otra operación modificó el pedido mientras se procesaba esta. */
public class ConflictoConcurrenciaException extends RuntimeException {

    public ConflictoConcurrenciaException(Long pedidoId) {
        super("El pedido " + pedidoId + " fue modificado por otra operación. Intente de nuevo");
    }
}
