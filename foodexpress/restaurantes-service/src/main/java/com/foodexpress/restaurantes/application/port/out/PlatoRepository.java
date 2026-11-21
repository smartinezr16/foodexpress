package com.foodexpress.restaurantes.application.port.out;

import com.foodexpress.restaurantes.domain.model.Plato;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlatoRepository {

    Plato guardar(Plato plato);

    Optional<Plato> buscarPorId(Long platoId);

    List<Plato> buscarPorRestaurante(Long restauranteId);

    List<Plato> buscarPorRestauranteYIds(Long restauranteId, Collection<Long> platoIds);

    void eliminar(Long platoId);
}
