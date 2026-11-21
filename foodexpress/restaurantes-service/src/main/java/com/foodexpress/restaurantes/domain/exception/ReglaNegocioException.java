package com.foodexpress.restaurantes.domain.exception;

/** Una regla del dominio de restaurantes y menús no se cumple. */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
