package com.foodexpress.restaurantes.application.usecase;

import com.foodexpress.restaurantes.application.dto.MovimientoInventario;
import com.foodexpress.restaurantes.application.usecase.Fakes.CacheEspia;
import com.foodexpress.restaurantes.application.usecase.Fakes.EventosEnMemoria;
import com.foodexpress.restaurantes.application.usecase.Fakes.PlatosEnMemoria;
import com.foodexpress.restaurantes.domain.model.CategoriaPlato;
import com.foodexpress.restaurantes.domain.model.Plato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Reacción del inventario a los eventos pedido.creado y pedido.cancelado. */
class AjustarInventarioUseCaseTest {

    private static final Long RESTAURANTE = 1L;

    private PlatosEnMemoria platos;
    private CacheEspia cache;
    private AjustarInventarioUseCase inventario;
    private Long costillas;
    private Long limonada;

    @BeforeEach
    void preparar() {
        platos = new PlatosEnMemoria();
        cache = new CacheEspia();
        inventario = new AjustarInventarioUseCase(platos, new EventosEnMemoria(), cache);
        costillas = platos.guardar(Plato.nuevo(RESTAURANTE, "Costillas", null, new BigDecimal("42000"),
                CategoriaPlato.PLATO_FUERTE, 5)).getId();
        limonada = platos.guardar(Plato.nuevo(RESTAURANTE, "Limonada", null, new BigDecimal("6000"),
                CategoriaPlato.BEBIDA, null)).getId();
    }

    private Plato plato(Long id) {
        return platos.buscarPorId(id).orElseThrow();
    }

    @Test
    void unPedidoCreadoDescuentaLasUnidadesEInvalidaLaCache() {
        inventario.descontarPorPedidoCreado("evt-1", List.of(
                new MovimientoInventario(costillas, 2),
                new MovimientoInventario(limonada, 3)));

        assertEquals(3, plato(costillas).getUnidadesDisponibles());
        assertNull(plato(limonada).getUnidadesDisponibles());
        assertEquals(List.of(RESTAURANTE), cache.invalidados);
    }

    @Test
    void elMismoEventoEntregadoDosVecesSoloSeAplicaUna() {
        List<MovimientoInventario> movimientos = List.of(new MovimientoInventario(costillas, 2));

        inventario.descontarPorPedidoCreado("evt-1", movimientos);
        inventario.descontarPorPedidoCreado("evt-1", movimientos);

        assertEquals(3, plato(costillas).getUnidadesDisponibles());
    }

    @Test
    void sumaLasCantidadesCuandoElPlatoVieneEnVariosItems() {
        inventario.descontarPorPedidoCreado("evt-1", List.of(
                new MovimientoInventario(costillas, 2),
                new MovimientoInventario(costillas, 3)));

        assertEquals(0, plato(costillas).getUnidadesDisponibles());
        assertFalse(plato(costillas).estaDisponible());
    }

    @Test
    void unPedidoCanceladoDevuelveLasUnidades() {
        inventario.descontarPorPedidoCreado("evt-1", List.of(new MovimientoInventario(costillas, 5)));
        assertFalse(plato(costillas).estaDisponible());

        inventario.reponerPorPedidoCancelado("evt-2", List.of(new MovimientoInventario(costillas, 5)));

        assertEquals(5, plato(costillas).getUnidadesDisponibles());
        assertTrue(plato(costillas).estaDisponible());
    }

    @Test
    void ignoraPlatosQueYaNoExistenEnElMenu() {
        inventario.descontarPorPedidoCreado("evt-1", List.of(
                new MovimientoInventario(999L, 1),
                new MovimientoInventario(costillas, 1)));

        assertEquals(4, plato(costillas).getUnidadesDisponibles());
    }
}
