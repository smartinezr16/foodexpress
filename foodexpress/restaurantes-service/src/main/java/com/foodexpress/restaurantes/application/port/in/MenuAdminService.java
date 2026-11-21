package com.foodexpress.restaurantes.application.port.in;

import com.foodexpress.restaurantes.application.dto.DatosPlato;
import com.foodexpress.restaurantes.domain.model.Plato;

import java.math.BigDecimal;

/**
 * Puerto de entrada para ADMINISTRAR el menú (lo usa el restaurante).
 *
 * ISP: separado de {@link MenuQueryService} para que los consumidores de solo
 * lectura no dependan de operaciones que no usan.
 */
public interface MenuAdminService {

    Plato agregarPlato(Long restauranteId, DatosPlato datos);

    Plato actualizarPlato(Long platoId, DatosPlato datos);

    Plato cambiarDisponibilidad(Long platoId, boolean habilitado, Integer unidadesDisponibles);

    void eliminarPlato(Long platoId);

    Plato agregarExtra(Long platoId, String nombre, BigDecimal precioAdicional);

    Plato quitarExtra(Long platoId, Long extraId);
}
