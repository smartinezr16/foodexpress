package com.foodexpress.restaurantes.application.usecase;

import com.foodexpress.restaurantes.application.dto.DatosPlato;
import com.foodexpress.restaurantes.application.port.in.MenuAdminService;
import com.foodexpress.restaurantes.application.port.out.MenuCache;
import com.foodexpress.restaurantes.application.port.out.PlatoRepository;
import com.foodexpress.restaurantes.application.port.out.RestauranteRepository;
import com.foodexpress.restaurantes.domain.exception.RecursoNoEncontradoException;
import com.foodexpress.restaurantes.domain.model.Plato;

import java.math.BigDecimal;

/**
 * Administración del menú por parte del restaurante. Cada cambio invalida la
 * copia en caché del menú para que los clientes vean la carta actualizada.
 */
public class GestionarMenuUseCase implements MenuAdminService {

    private final RestauranteRepository restauranteRepository;
    private final PlatoRepository platoRepository;
    private final MenuCache menuCache;

    public GestionarMenuUseCase(RestauranteRepository restauranteRepository,
                                PlatoRepository platoRepository,
                                MenuCache menuCache) {
        this.restauranteRepository = restauranteRepository;
        this.platoRepository = platoRepository;
        this.menuCache = menuCache;
    }

    @Override
    public Plato agregarPlato(Long restauranteId, DatosPlato datos) {
        if (!restauranteRepository.existe(restauranteId)) {
            throw RecursoNoEncontradoException.restaurante(restauranteId);
        }
        Plato plato = Plato.nuevo(restauranteId, datos.nombre(), datos.descripcion(), datos.precio(),
                datos.categoria(), datos.unidadesDisponibles());
        return guardar(plato);
    }

    @Override
    public Plato actualizarPlato(Long platoId, DatosPlato datos) {
        Plato plato = buscar(platoId);
        plato.actualizarDatos(datos.nombre(), datos.descripcion(), datos.precio(), datos.categoria());
        plato.cambiarDisponibilidad(plato.isHabilitado(), datos.unidadesDisponibles());
        return guardar(plato);
    }

    @Override
    public Plato cambiarDisponibilidad(Long platoId, boolean habilitado, Integer unidadesDisponibles) {
        Plato plato = buscar(platoId);
        plato.cambiarDisponibilidad(habilitado, unidadesDisponibles);
        return guardar(plato);
    }

    @Override
    public void eliminarPlato(Long platoId) {
        Plato plato = buscar(platoId);
        platoRepository.eliminar(platoId);
        menuCache.invalidar(plato.getRestauranteId());
    }

    @Override
    public Plato agregarExtra(Long platoId, String nombre, BigDecimal precioAdicional) {
        Plato plato = buscar(platoId);
        plato.agregarExtra(nombre, precioAdicional);
        return guardar(plato);
    }

    @Override
    public Plato quitarExtra(Long platoId, Long extraId) {
        Plato plato = buscar(platoId);
        plato.quitarExtra(extraId);
        return guardar(plato);
    }

    private Plato buscar(Long platoId) {
        return platoRepository.buscarPorId(platoId)
                .orElseThrow(() -> RecursoNoEncontradoException.plato(platoId));
    }

    private Plato guardar(Plato plato) {
        Plato guardado = platoRepository.guardar(plato);
        menuCache.invalidar(guardado.getRestauranteId());
        return guardado;
    }
}
