package com.foodexpress.restaurantes.domain.model;

import com.foodexpress.restaurantes.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestauranteTest {

    @Test
    void unRestauranteNuevoEmpiezaCerrado() {
        Restaurante restaurante = Restaurante.registrar("La Brasa", "Calle 45", null,
                LocalTime.of(11, 0), LocalTime.of(22, 0));

        assertEquals(EstadoRestaurante.CERRADO, restaurante.getEstado());
        assertFalse(restaurante.estaAbierto());
    }

    @Test
    void sePuedeAbrirYCerrar() {
        Restaurante restaurante = Restaurante.registrar("La Brasa", "Calle 45", null,
                LocalTime.of(11, 0), LocalTime.of(22, 0));

        restaurante.abrir();
        assertTrue(restaurante.estaAbierto());

        restaurante.cerrar();
        assertFalse(restaurante.estaAbierto());
    }

    @Test
    void exigeNombreDireccionYHorario() {
        assertThrows(ReglaNegocioException.class,
                () -> Restaurante.registrar(" ", "Calle 45", null, LocalTime.of(11, 0), LocalTime.of(22, 0)));
        assertThrows(ReglaNegocioException.class,
                () -> Restaurante.registrar("La Brasa", null, null, LocalTime.of(11, 0), LocalTime.of(22, 0)));
        assertThrows(ReglaNegocioException.class,
                () -> Restaurante.registrar("La Brasa", "Calle 45", null, LocalTime.of(11, 0), LocalTime.of(11, 0)));
    }
}
