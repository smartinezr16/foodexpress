package com.foodexpress.pedidos.domain.exception;

/** Raíz de las excepciones que representan reglas de negocio incumplidas. */
public class DominioException extends RuntimeException {

    public DominioException(String mensaje) {
        super(mensaje);
    }
}
