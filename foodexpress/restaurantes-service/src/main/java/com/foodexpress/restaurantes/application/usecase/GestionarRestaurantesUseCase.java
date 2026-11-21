package com.foodexpress.restaurantes.application.usecase;

import com.foodexpress.restaurantes.application.dto.DatosRestaurante;
import com.foodexpress.restaurantes.application.port.in.RestauranteService;
import com.foodexpress.restaurantes.application.port.out.RestauranteRepository;
import com.foodexpress.restaurantes.domain.exception.RecursoNoEncontradoException;
import com.foodexpress.restaurantes.domain.model.EstadoRestaurante;
import com.foodexpress.restaurantes.domain.model.Restaurante;

import java.util.List;

public class GestionarRestaurantesUseCase implements RestauranteService {

    private final RestauranteRepository restauranteRepository;

    public GestionarRestaurantesUseCase(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    @Override
    public Restaurante registrar(DatosRestaurante datos) {
        Restaurante restaurante = Restaurante.registrar(datos.nombre(), datos.direccion(), datos.telefono(),
                datos.horaApertura(), datos.horaCierre());
        return restauranteRepository.guardar(restaurante);
    }

    @Override
    public Restaurante actualizar(Long restauranteId, DatosRestaurante datos) {
        Restaurante restaurante = obtener(restauranteId);
        restaurante.actualizarDatos(datos.nombre(), datos.direccion(), datos.telefono(),
                datos.horaApertura(), datos.horaCierre());
        return restauranteRepository.guardar(restaurante);
    }

    @Override
    public Restaurante cambiarEstado(Long restauranteId, EstadoRestaurante estado) {
        Restaurante restaurante = obtener(restauranteId);
        if (estado == EstadoRestaurante.ABIERTO) {
            restaurante.abrir();
        } else {
            restaurante.cerrar();
        }
        return restauranteRepository.guardar(restaurante);
    }

    @Override
    public Restaurante obtener(Long restauranteId) {
        return restauranteRepository.buscarPorId(restauranteId)
                .orElseThrow(() -> RecursoNoEncontradoException.restaurante(restauranteId));
    }

    @Override
    public List<Restaurante> listar() {
        return restauranteRepository.listar();
    }
}
