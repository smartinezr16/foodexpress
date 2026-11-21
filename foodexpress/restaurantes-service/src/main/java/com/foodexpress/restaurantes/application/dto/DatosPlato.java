package com.foodexpress.restaurantes.application.dto;

import com.foodexpress.restaurantes.domain.model.CategoriaPlato;

import java.math.BigDecimal;

/** unidadesDisponibles es opcional: null significa que el plato no lleva control de inventario. */
public record DatosPlato(String nombre, String descripcion, BigDecimal precio, CategoriaPlato categoria,
                         Integer unidadesDisponibles) {
}
