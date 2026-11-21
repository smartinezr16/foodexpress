package com.foodexpress.restaurantes.domain.model;

import com.foodexpress.restaurantes.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;

/** Adición opcional de un plato (por ejemplo queso extra) con su precio adicional. */
public record Extra(Long id, String nombre, BigDecimal precioAdicional) {

    public Extra {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El extra debe tener nombre");
        }
        if (precioAdicional == null || precioAdicional.signum() < 0) {
            throw new ReglaNegocioException("El precio adicional del extra no puede ser negativo");
        }
        nombre = nombre.trim();
    }
}
