package com.foodexpress.restaurantes.application.usecase;

import com.foodexpress.restaurantes.application.port.out.EventoProcesadoRepository;
import com.foodexpress.restaurantes.application.port.out.MenuCache;
import com.foodexpress.restaurantes.application.port.out.PlatoRepository;
import com.foodexpress.restaurantes.application.port.out.RestauranteRepository;
import com.foodexpress.restaurantes.domain.model.Extra;
import com.foodexpress.restaurantes.domain.model.Plato;
import com.foodexpress.restaurantes.domain.model.Restaurante;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Implementaciones en memoria de los puertos de salida, para probar los casos de uso sin infraestructura. */
final class Fakes {

    private Fakes() {
    }

    static final class RestaurantesEnMemoria implements RestauranteRepository {

        private final Map<Long, Restaurante> restaurantes = new LinkedHashMap<>();
        private long secuencia = 0;

        @Override
        public Restaurante guardar(Restaurante r) {
            Long id = r.getId() == null ? ++secuencia : r.getId();
            Restaurante guardado = new Restaurante(id, r.getNombre(), r.getDireccion(), r.getTelefono(),
                    r.getHoraApertura(), r.getHoraCierre(), r.getEstado());
            restaurantes.put(id, guardado);
            return guardado;
        }

        @Override
        public Optional<Restaurante> buscarPorId(Long restauranteId) {
            return Optional.ofNullable(restaurantes.get(restauranteId));
        }

        @Override
        public boolean existe(Long restauranteId) {
            return restaurantes.containsKey(restauranteId);
        }

        @Override
        public List<Restaurante> listar() {
            return new ArrayList<>(restaurantes.values());
        }
    }

    /** Asigna ids a platos y extras nuevos, igual que lo haría la base de datos. */
    static final class PlatosEnMemoria implements PlatoRepository {

        private final Map<Long, Plato> platos = new LinkedHashMap<>();
        private long secuencia = 0;
        private long secuenciaExtras = 0;

        @Override
        public Plato guardar(Plato p) {
            Long id = p.getId() == null ? ++secuencia : p.getId();
            long version = p.getVersion() == null ? 0 : p.getVersion() + 1;
            List<Extra> extras = new ArrayList<>();
            for (Extra extra : p.getExtras()) {
                Long extraId = extra.id() == null ? ++secuenciaExtras : extra.id();
                extras.add(new Extra(extraId, extra.nombre(), extra.precioAdicional()));
            }
            Plato guardado = new Plato(id, version, p.getRestauranteId(), p.getNombre(), p.getDescripcion(),
                    p.getPrecio(), p.getCategoria(), p.isHabilitado(), p.getUnidadesDisponibles(), extras);
            platos.put(id, guardado);
            return guardado;
        }

        @Override
        public Optional<Plato> buscarPorId(Long platoId) {
            return Optional.ofNullable(platos.get(platoId));
        }

        @Override
        public List<Plato> buscarPorRestaurante(Long restauranteId) {
            return platos.values().stream().filter(p -> p.getRestauranteId().equals(restauranteId)).toList();
        }

        @Override
        public List<Plato> buscarPorRestauranteYIds(Long restauranteId, Collection<Long> platoIds) {
            return buscarPorRestaurante(restauranteId).stream().filter(p -> platoIds.contains(p.getId())).toList();
        }

        @Override
        public void eliminar(Long platoId) {
            platos.remove(platoId);
        }
    }

    static final class EventosEnMemoria implements EventoProcesadoRepository {

        private final Set<String> procesados = new HashSet<>();

        @Override
        public boolean registrarSiEsNuevo(String eventId) {
            return procesados.add(eventId);
        }
    }

    /** Anota qué menús se pidió invalidar. */
    static final class CacheEspia implements MenuCache {

        final List<Long> invalidados = new ArrayList<>();

        @Override
        public void invalidar(Long restauranteId) {
            invalidados.add(restauranteId);
        }
    }
}
