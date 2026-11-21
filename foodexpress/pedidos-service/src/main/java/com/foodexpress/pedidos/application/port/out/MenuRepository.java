package com.foodexpress.pedidos.application.port.out;

import com.foodexpress.pedidos.application.dto.PlatoMenu;
import com.foodexpress.pedidos.application.dto.RestauranteInfo;

import java.util.Collection;
import java.util.List;

/**
 * Puerto de salida hacia el menú de los restaurantes.
 *
 * Para el caso de uso es "un repositorio de menús"; que detrás haya una llamada
 * HTTP al microservicio de Restaurantes es un detalle de infraestructura.
 */
public interface MenuRepository {

    /** @throws com.foodexpress.pedidos.application.exception.RestauranteNoDisponibleException si no existe */
    RestauranteInfo obtenerRestaurante(Long restauranteId);

    /** Devuelve los platos solicitados que pertenecen al restaurante; omite los que no existen. */
    List<PlatoMenu> obtenerPlatos(Long restauranteId, Collection<Long> platoIds);
}
