package com.foodexpress.restaurantes.application.port.in;

import com.foodexpress.restaurantes.application.dto.DatosRestaurante;
import com.foodexpress.restaurantes.domain.model.EstadoRestaurante;
import com.foodexpress.restaurantes.domain.model.Restaurante;

import java.util.List;

/** Puerto de entrada para el registro y la operación de restaurantes. */
public interface RestauranteService {

    Restaurante registrar(DatosRestaurante datos);

    Restaurante actualizar(Long restauranteId, DatosRestaurante datos);

    Restaurante cambiarEstado(Long restauranteId, EstadoRestaurante estado);

    Restaurante obtener(Long restauranteId);

    List<Restaurante> listar();
}
