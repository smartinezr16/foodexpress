package com.foodexpress.pedidos.application.exception;

/** El restaurante no existe o está cerrado, así que no puede recibir pedidos. */
public class RestauranteNoDisponibleException extends RuntimeException {

    public RestauranteNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
