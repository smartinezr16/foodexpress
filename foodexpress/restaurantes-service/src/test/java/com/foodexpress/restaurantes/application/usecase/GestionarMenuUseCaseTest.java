package com.foodexpress.restaurantes.application.usecase;

import com.foodexpress.restaurantes.application.dto.DatosPlato;
import com.foodexpress.restaurantes.application.dto.DatosRestaurante;
import com.foodexpress.restaurantes.application.usecase.Fakes.CacheEspia;
import com.foodexpress.restaurantes.application.usecase.Fakes.PlatosEnMemoria;
import com.foodexpress.restaurantes.application.usecase.Fakes.RestaurantesEnMemoria;
import com.foodexpress.restaurantes.domain.exception.RecursoNoEncontradoException;
import com.foodexpress.restaurantes.domain.model.CategoriaPlato;
import com.foodexpress.restaurantes.domain.model.Plato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Administración y consulta del menú. */
class GestionarMenuUseCaseTest {

    private CacheEspia cache;
    private GestionarMenuUseCase menuAdmin;
    private ConsultarMenuUseCase menuQuery;
    private Long restauranteId;

    @BeforeEach
    void preparar() {
        RestaurantesEnMemoria restaurantes = new RestaurantesEnMemoria();
        PlatosEnMemoria platos = new PlatosEnMemoria();
        cache = new CacheEspia();
        menuAdmin = new GestionarMenuUseCase(restaurantes, platos, cache);
        menuQuery = new ConsultarMenuUseCase(restaurantes, platos);
        restauranteId = new GestionarRestaurantesUseCase(restaurantes)
                .registrar(new DatosRestaurante("La Brasa", "Calle 45", null, LocalTime.of(11, 0), LocalTime.of(22, 0)))
                .getId();
    }

    private DatosPlato datos(String nombre, String precio, CategoriaPlato categoria) {
        return new DatosPlato(nombre, null, new BigDecimal(precio), categoria, null);
    }

    @Test
    void agregaUnPlatoDisponibleEInvalidaLaCacheDelRestaurante() {
        Plato plato = menuAdmin.agregarPlato(restauranteId, datos("Hamburguesa", "25000", CategoriaPlato.PLATO_FUERTE));

        assertNotNull(plato.getId());
        assertTrue(plato.estaDisponible());
        assertEquals(List.of(restauranteId), cache.invalidados);
    }

    @Test
    void noAgregaPlatosAUnRestauranteInexistente() {
        assertThrows(RecursoNoEncontradoException.class,
                () -> menuAdmin.agregarPlato(99L, datos("Hamburguesa", "25000", CategoriaPlato.PLATO_FUERTE)));
        assertTrue(cache.invalidados.isEmpty());
    }

    @Test
    void elMenuSeOrdenaComoUnaCartaPorCategoriaYNombre() {
        menuAdmin.agregarPlato(restauranteId, datos("Brownie", "12000", CategoriaPlato.POSTRE));
        menuAdmin.agregarPlato(restauranteId, datos("Limonada", "6000", CategoriaPlato.BEBIDA));
        menuAdmin.agregarPlato(restauranteId, datos("Hamburguesa", "25000", CategoriaPlato.PLATO_FUERTE));
        menuAdmin.agregarPlato(restauranteId, datos("Costillas", "42000", CategoriaPlato.PLATO_FUERTE));
        menuAdmin.agregarPlato(restauranteId, datos("Empanadas", "9000", CategoriaPlato.ENTRADA));

        List<String> nombres = menuQuery.consultarMenu(restauranteId).stream().map(Plato::getNombre).toList();

        assertEquals(List.of("Empanadas", "Costillas", "Hamburguesa", "Limonada", "Brownie"), nombres);
    }

    @Test
    void retirarUnPlatoDeLaCartaLoDejaNoDisponible() {
        Plato plato = menuAdmin.agregarPlato(restauranteId, datos("Hamburguesa", "25000", CategoriaPlato.PLATO_FUERTE));

        Plato retirado = menuAdmin.cambiarDisponibilidad(plato.getId(), false, null);

        assertFalse(retirado.estaDisponible());
        assertFalse(menuQuery.obtenerPlato(plato.getId()).estaDisponible());
    }

    @Test
    void losExtrasRecibenIdAlGuardarseYSePuedenQuitar() {
        Plato plato = menuAdmin.agregarPlato(restauranteId, datos("Hamburguesa", "25000", CategoriaPlato.PLATO_FUERTE));

        Plato conExtra = menuAdmin.agregarExtra(plato.getId(), "Queso", new BigDecimal("3000"));
        Long extraId = conExtra.getExtras().get(0).id();
        assertNotNull(extraId);

        Plato sinExtra = menuAdmin.quitarExtra(plato.getId(), extraId);
        assertTrue(sinExtra.getExtras().isEmpty());
    }

    @Test
    void eliminarUnPlatoLoSacaDelMenuEInvalidaLaCache() {
        Plato plato = menuAdmin.agregarPlato(restauranteId, datos("Hamburguesa", "25000", CategoriaPlato.PLATO_FUERTE));
        cache.invalidados.clear();

        menuAdmin.eliminarPlato(plato.getId());

        assertTrue(menuQuery.consultarMenu(restauranteId).isEmpty());
        assertEquals(List.of(restauranteId), cache.invalidados);
    }

    @Test
    void consultarElMenuDeUnRestauranteInexistenteFalla() {
        assertThrows(RecursoNoEncontradoException.class, () -> menuQuery.consultarMenu(99L));
    }

    @Test
    void obtenerPlatosSoloDevuelveLosDelRestauranteIndicado() {
        Plato plato = menuAdmin.agregarPlato(restauranteId, datos("Hamburguesa", "25000", CategoriaPlato.PLATO_FUERTE));

        List<Plato> encontrados = menuQuery.obtenerPlatos(restauranteId, List.of(plato.getId(), 999L));

        assertEquals(1, encontrados.size());
        assertEquals("Hamburguesa", encontrados.get(0).getNombre());
    }
}
