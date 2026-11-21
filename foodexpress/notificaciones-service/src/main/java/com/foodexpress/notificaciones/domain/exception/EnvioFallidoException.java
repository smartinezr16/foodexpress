package com.foodexpress.notificaciones.domain.exception;

/** Un canal no pudo entregar la notificación. */
public class EnvioFallidoException extends RuntimeException {

    public EnvioFallidoException(String mensaje) {
        super(mensaje);
    }

    public EnvioFallidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
