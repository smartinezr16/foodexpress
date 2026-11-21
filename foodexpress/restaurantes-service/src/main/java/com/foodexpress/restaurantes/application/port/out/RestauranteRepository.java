package com.foodexpress.restaurantes.application.port.out;

import com.foodexpress.restaurantes.domain.model.Restaurante;

import java.util.List;
import java.util.Optional;

public interface RestauranteRepository {

    Restaurante guardar(Restaurante restaurante);

    Optional<Restaurante> buscarPorId(Long restauranteId);

    boolean existe(Long restauranteId);

    List<Restaurante> listar();
}
