package com.foodexpress.restaurantes.domain.model;

import com.foodexpress.restaurantes.domain.exception.RecursoNoEncontradoException;
import com.foodexpress.restaurantes.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlatoTest {

    private Plato costillas(Integer unidades) {
        return Plato.nuevo(1L, "Costillas BBQ", "400 g", new BigDecimal("42000"), CategoriaPlato.PLATO_FUERTE, unidades);
    }

    @Test
    void unPlatoSinControlDeInventarioSiempreEstaDisponible() {
        Plato plato = costillas(null);

        plato.descontarUnidades(50);

        assertTrue(plato.estaDisponible());
        assertNull(plato.getUnidadesDisponibles());
    }

    @Test
    void alAgotarseLasUnidadesElPlatoDejaDeEstarDisponible() {
        Plato plato = costillas(3);

        plato.descontarUnidades(2);
        assertTrue(plato.estaDisponible());

        plato.descontarUnidades(1);
        assertEquals(0, plato.getUnidadesDisponibles());
        assertFalse(plato.estaDisponible());
    }

    @Test
    void elInventarioNuncaQuedaNegativo() {
        Plato plato = costillas(2);

        plato.descontarUnidades(5);

        assertEquals(0, plato.getUnidadesDisponibles());
    }

    @Test
    void reponerUnidadesVuelveADejarElPlatoDisponible() {
        Plato plato = costillas(1);
        plato.descontarUnidades(1);

        plato.reponerUnidades(1);

        assertEquals(1, plato.getUnidadesDisponibles());
        assertTrue(plato.estaDisponible());
    }

    @Test
    void unPlatoDeshabilitadoNoEstaDisponibleAunqueTengaUnidades() {
        Plato plato = costillas(10);

        plato.cambiarDisponibilidad(false, 10);
        plato.reponerUnidades(5);

        assertFalse(plato.estaDisponible());
        assertEquals(15, plato.getUnidadesDisponibles());
    }

    @Test
    void rechazaPreciosQueNoSeanPositivos() {
        assertThrows(ReglaNegocioException.class,
                () -> Plato.nuevo(1L, "Gratis", null, BigDecimal.ZERO, CategoriaPlato.POSTRE, null));
    }

    @Test
    void noPermiteDosExtrasConElMismoNombre() {
        Plato plato = costillas(null);
        plato.agregarExtra("Papas", new BigDecimal("6000"));

        assertThrows(ReglaNegocioException.class, () -> plato.agregarExtra("papas", new BigDecimal("5000")));
        assertEquals(1, plato.getExtras().size());
    }

    @Test
    void quitaUnExtraPorSuId() {
        Plato plato = new Plato(7L, 0L, 1L, "Hamburguesa", null, new BigDecimal("25000"),
                CategoriaPlato.PLATO_FUERTE, true, null,
                List.of(new Extra(3L, "Queso", new BigDecimal("3000")), new Extra(4L, "Tocineta", new BigDecimal("4000"))));

        plato.quitarExtra(3L);

        assertEquals(1, plato.getExtras().size());
        assertEquals("Tocineta", plato.getExtras().get(0).nombre());
        assertThrows(RecursoNoEncontradoException.class, () -> plato.quitarExtra(99L));
    }
}
