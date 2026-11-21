package com.foodexpress.pedidos.application.exception;

/** El plato no pertenece al menú del restaurante o no está disponible en este momento. */
public class PlatoNoDisponibleException extends RuntimeException {

    public PlatoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
