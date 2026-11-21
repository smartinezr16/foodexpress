package com.foodexpress.restaurantes.infrastructure.persistence;

import com.foodexpress.restaurantes.domain.model.Extra;
import com.foodexpress.restaurantes.domain.model.Plato;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Convierte entre el modelo de dominio del plato y sus entidades JPA. */
final class PlatoMapper {

    private PlatoMapper() {
    }

    static Plato aDominio(PlatoEntity entidad) {
        List<Extra> extras = entidad.getExtras().stream()
                .map(extra -> new Extra(extra.getId(), extra.getNombre(), extra.getPrecioAdicional()))
                .toList();
        return new Plato(entidad.getId(), entidad.getVersion(), entidad.getRestauranteId(), entidad.getNombre(),
                entidad.getDescripcion(), entidad.getPrecio(), entidad.getCategoria(), entidad.isHabilitado(),
                entidad.getUnidadesDisponibles(), extras);
    }

    /** Pasa el estado del plato de dominio a la entidad, conservando los ids de los extras existentes. */
    static void copiar(Plato plato, PlatoEntity entidad) {
        entidad.setRestauranteId(plato.getRestauranteId());
        entidad.setNombre(plato.getNombre());
        entidad.setDescripcion(plato.getDescripcion());
        entidad.setPrecio(plato.getPrecio());
        entidad.setCategoria(plato.getCategoria());
        entidad.setHabilitado(plato.isHabilitado());
        entidad.setUnidadesDisponibles(plato.getUnidadesDisponibles());

        Map<Long, ExtraEntity> existentes = new HashMap<>();
        for (ExtraEntity extra : entidad.getExtras()) {
            existentes.put(extra.getId(), extra);
        }

        List<ExtraEntity> actualizados = new ArrayList<>();
        for (Extra extra : plato.getExtras()) {
            ExtraEntity extraEntidad = extra.id() == null ? null : existentes.get(extra.id());
            if (extraEntidad == null) {
                extraEntidad = new ExtraEntity(entidad);
            }
            extraEntidad.setNombre(extra.nombre());
            extraEntidad.setPrecioAdicional(extra.precioAdicional());
            actualizados.add(extraEntidad);
        }
        // Los extras que ya no están en el dominio quedan huérfanos y JPA los elimina.
        entidad.getExtras().clear();
        entidad.getExtras().addAll(actualizados);
    }
}
