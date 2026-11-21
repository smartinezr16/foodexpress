package com.foodexpress.restaurantes.infrastructure.web.dto;

import com.foodexpress.restaurantes.application.dto.DatosPlato;
import com.foodexpress.restaurantes.domain.model.CategoriaPlato;
import com.foodexpress.restaurantes.domain.model.Plato;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/** Cuerpos de petición y respuesta de la API del menú. */
public final class MenuDtos {

    private MenuDtos() {
    }

    public record PlatoRequest(
            @NotBlank(message = "El nombre del plato es obligatorio") String nombre,
            @Size(max = 500) String descripcion,
            @NotNull(message = "El precio es obligatorio") @Positive(message = "El precio debe ser mayor que cero")
            BigDecimal precio,
            @NotNull(message = "La categoría es obligatoria (ENTRADA, PLATO_FUERTE, BEBIDA o POSTRE)")
            CategoriaPlato categoria,
            @PositiveOrZero(message = "Las unidades disponibles no pueden ser negativas")
            Integer unidadesDisponibles) {

        public DatosPlato aDatos() {
            return new DatosPlato(nombre, descripcion, precio, categoria, unidadesDisponibles);
        }
    }

    public record DisponibilidadRequest(
            @NotNull(message = "Indique si el plato queda habilitado") Boolean habilitado,
            @PositiveOrZero(message = "Las unidades disponibles no pueden ser negativas")
            Integer unidadesDisponibles) {
    }

    public record ExtraRequest(
            @NotBlank(message = "El nombre del extra es obligatorio") String nombre,
            @NotNull(message = "El precio adicional es obligatorio")
            @PositiveOrZero(message = "El precio adicional no puede ser negativo") BigDecimal precioAdicional) {
    }

    public record ExtraResponse(Long id, String nombre, BigDecimal precioAdicional) {
    }

    /**
     * "disponible" es lo que deben mirar los clientes: resume si el plato está habilitado
     * y si le quedan unidades.
     */
    public record PlatoResponse(Long id, Long restauranteId, String nombre, String descripcion, BigDecimal precio,
                                CategoriaPlato categoria, boolean disponible, boolean habilitado,
                                Integer unidadesDisponibles, List<ExtraResponse> extras) {

        public static PlatoResponse de(Plato plato) {
            List<ExtraResponse> extras = plato.getExtras().stream()
                    .map(extra -> new ExtraResponse(extra.id(), extra.nombre(), extra.precioAdicional()))
                    .toList();
            return new PlatoResponse(plato.getId(), plato.getRestauranteId(), plato.getNombre(),
                    plato.getDescripcion(), plato.getPrecio(), plato.getCategoria(), plato.estaDisponible(),
                    plato.isHabilitado(), plato.getUnidadesDisponibles(), extras);
        }
    }
}
