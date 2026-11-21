package com.foodexpress.restaurantes.application.exception;

/** Otra operación modificó el mismo plato mientras se procesaba esta. */
public class ConflictoConcurrenciaException extends RuntimeException {

    public ConflictoConcurrenciaException(Long platoId) {
        super("El plato " + platoId + " fue modificado por otra operación. Intente de nuevo");
    }
}
