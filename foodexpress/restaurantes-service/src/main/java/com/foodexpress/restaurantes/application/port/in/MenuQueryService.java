package com.foodexpress.restaurantes.application.port.in;

import com.foodexpress.restaurantes.domain.model.Plato;

import java.util.Collection;
import java.util.List;

/**
 * Puerto de entrada para CONSULTAR el menú.
 *
 * ISP: quien solo lee el menú (los clientes, el servicio de Pedidos) depende de
 * esta interfaz y no de las operaciones administrativas de {@link MenuAdminService}.
 */
public interface MenuQueryService {

    /** Menú completo de un restaurante. Es la consulta más frecuente del sistema. */
    List<Plato> consultarMenu(Long restauranteId);

    /** Platos puntuales de un restaurante, con su disponibilidad al instante (sin caché). */
    List<Plato> obtenerPlatos(Long restauranteId, Collection<Long> platoIds);

    Plato obtenerPlato(Long platoId);
}
