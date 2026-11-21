package com.foodexpress.restaurantes.infrastructure.web.dto;

import com.foodexpress.restaurantes.application.dto.DatosRestaurante;
import com.foodexpress.restaurantes.domain.model.EstadoRestaurante;
import com.foodexpress.restaurantes.domain.model.Restaurante;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/** Cuerpos de petición y respuesta de la API de restaurantes. */
public final class RestauranteDtos {

    private RestauranteDtos() {
    }

    public record RestauranteRequest(
            @NotBlank(message = "El nombre es obligatorio") String nombre,
            @NotBlank(message = "La dirección es obligatoria") String direccion,
            String telefono,
            @NotNull(message = "La hora de apertura es obligatoria (HH:mm)") LocalTime horaApertura,
            @NotNull(message = "La hora de cierre es obligatoria (HH:mm)") LocalTime horaCierre) {

        public DatosRestaurante aDatos() {
            return new DatosRestaurante(nombre, direccion, telefono, horaApertura, horaCierre);
        }
    }

    public record CambiarEstadoRequest(
            @NotNull(message = "El estado es obligatorio (ABIERTO o CERRADO)") EstadoRestaurante estado) {
    }

    public record RestauranteResponse(Long id, String nombre, String direccion, String telefono,
                                      LocalTime horaApertura, LocalTime horaCierre, EstadoRestaurante estado) {

        public static RestauranteResponse de(Restaurante restaurante) {
            return new RestauranteResponse(restaurante.getId(), restaurante.getNombre(), restaurante.getDireccion(),
                    restaurante.getTelefono(), restaurante.getHoraApertura(), restaurante.getHoraCierre(),
                    restaurante.getEstado());
        }
    }
}
