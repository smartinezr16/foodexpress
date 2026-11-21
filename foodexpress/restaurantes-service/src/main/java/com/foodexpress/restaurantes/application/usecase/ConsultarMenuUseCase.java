package com.foodexpress.restaurantes.application.usecase;

import com.foodexpress.restaurantes.application.port.in.MenuQueryService;
import com.foodexpress.restaurantes.application.port.out.PlatoRepository;
import com.foodexpress.restaurantes.application.port.out.RestauranteRepository;
import com.foodexpress.restaurantes.domain.exception.RecursoNoEncontradoException;
import com.foodexpress.restaurantes.domain.model.Plato;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/** Consultas de solo lectura sobre el menú. */
public class ConsultarMenuUseCase implements MenuQueryService {

    private final RestauranteRepository restauranteRepository;
    private final PlatoRepository platoRepository;

    public ConsultarMenuUseCase(RestauranteRepository restauranteRepository, PlatoRepository platoRepository) {
        this.restauranteRepository = restauranteRepository;
        this.platoRepository = platoRepository;
    }

    @Override
    public List<Plato> consultarMenu(Long restauranteId) {
        validarRestaurante(restauranteId);
        // Orden de carta: entradas, platos fuertes, bebidas y postres; dentro de cada grupo, por nombre.
        return platoRepository.buscarPorRestaurante(restauranteId).stream()
                .sorted(Comparator.comparing(Plato::getCategoria).thenComparing(Plato::getNombre))
                .toList();
    }

    @Override
    public List<Plato> obtenerPlatos(Long restauranteId, Collection<Long> platoIds) {
        validarRestaurante(restauranteId);
        if (platoIds == null || platoIds.isEmpty()) {
            return List.of();
        }
        return platoRepository.buscarPorRestauranteYIds(restauranteId, platoIds);
    }

    @Override
    public Plato obtenerPlato(Long platoId) {
        return platoRepository.buscarPorId(platoId)
                .orElseThrow(() -> RecursoNoEncontradoException.plato(platoId));
    }

    private void validarRestaurante(Long restauranteId) {
        if (!restauranteRepository.existe(restauranteId)) {
            throw RecursoNoEncontradoException.restaurante(restauranteId);
        }
    }
}
