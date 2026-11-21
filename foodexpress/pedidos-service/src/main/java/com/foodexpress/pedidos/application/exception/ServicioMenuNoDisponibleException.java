package com.foodexpress.pedidos.application.exception;

/** No fue posible consultar el menú (el servicio de Restaurantes no respondió). */
public class ServicioMenuNoDisponibleException extends RuntimeException {

    public ServicioMenuNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
