package com.foodexpress.restaurantes.domain.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public static RecursoNoEncontradoException restaurante(Long id) {
        return new RecursoNoEncontradoException("No existe el restaurante " + id);
    }

    public static RecursoNoEncontradoException plato(Long id) {
        return new RecursoNoEncontradoException("No existe el plato " + id);
    }
}
