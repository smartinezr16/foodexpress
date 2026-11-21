package com.foodexpress.pedidos.application.usecase;

import com.foodexpress.pedidos.application.dto.CrearPedidoCommand;
import com.foodexpress.pedidos.application.dto.CrearPedidoCommand.ItemSolicitado;
import com.foodexpress.pedidos.application.dto.ParametrosPedido;
import com.foodexpress.pedidos.application.dto.PlatoMenu;
import com.foodexpress.pedidos.application.dto.PlatoMenu.ExtraMenu;
import com.foodexpress.pedidos.application.dto.RestauranteInfo;
import com.foodexpress.pedidos.application.exception.PlatoNoDisponibleException;
import com.foodexpress.pedidos.application.exception.RestauranteNoDisponibleException;
import com.foodexpress.pedidos.application.usecase.Fakes.EventosRegistrados;
import com.foodexpress.pedidos.application.usecase.Fakes.MenuEnMemoria;
import com.foodexpress.pedidos.application.usecase.Fakes.PedidosEnMemoria;
import com.foodexpress.pedidos.domain.exception.PedidoInvalidoException;
import com.foodexpress.pedidos.domain.model.Pedido;
import com.foodexpress.pedidos.domain.model.TipoPedido;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedidoTipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrearPedidoUseCaseTest {

    private static final Long RESTAURANTE = 1L;
    private static final Long HAMBURGUESA = 10L;
    private static final Long LIMONADA = 20L;
    private static final Long AGOTADO = 30L;
    private static final Long QUESO = 3L;

    private MenuEnMemoria menu;
    private PedidosEnMemoria pedidos;
    private EventosRegistrados eventos;
    private CrearPedidoUseCase crearPedido;

    @BeforeEach
    void preparar() {
        menu = new MenuEnMemoria();
        menu.registrar(new RestauranteInfo(RESTAURANTE, "La Brasa", true),
                new PlatoMenu(HAMBURGUESA, "Hamburguesa", new BigDecimal("25000"), true,
                        List.of(new ExtraMenu(QUESO, "Queso", new BigDecimal("3000")))),
                new PlatoMenu(LIMONADA, "Limonada", new BigDecimal("6000"), true, List.of()),
                new PlatoMenu(AGOTADO, "Costillas", new BigDecimal("40000"), false, List.of()));
        pedidos = new PedidosEnMemoria();
        eventos = new EventosRegistrados();
        crearPedido = new CrearPedidoUseCase(menu, pedidos, eventos,
                new ParametrosPedido(new BigDecimal("0.08"), new BigDecimal("5000")));
    }

    private CrearPedidoCommand pedidoDe(TipoPedido tipo, String direccion, ItemSolicitado... items) {
        return new CrearPedidoCommand(RESTAURANTE, "Ana", "ana@correo.com", tipo, direccion, List.of(items));
    }

    @Test
    void creaElPedidoConPreciosDelMenuYPublicaElEvento() {
        Pedido pedido = crearPedido.ejecutar(pedidoDe(TipoPedido.LOCAL, null,
                new ItemSolicitado(HAMBURGUESA, 2, List.of(QUESO), "Sin cebolla"),
                new ItemSolicitado(LIMONADA, 1, null, null)));

        assertNotNull(pedido.getId());
        assertEquals(EstadoPedidoTipo.RECIBIDO, pedido.getEstado());
        assertEquals("La Brasa", pedido.getRestauranteNombre());
        // (25000 + 3000) * 2 + 6000 = 62000 ; impuesto 4960
        assertEquals(new BigDecimal("62000.00"), pedido.subtotal());
        assertEquals(new BigDecimal("4960.00"), pedido.impuestos());
        assertEquals(new BigDecimal("66960.00"), pedido.total());
        assertEquals("Queso", pedido.getItems().get(0).getExtras().get(0).nombre());

        assertEquals(1, pedidos.cantidad());
        assertEquals(List.of("pedido.creado:" + pedido.getId()), eventos.eventos);
    }

    @Test
    void unDomicilioCobraElCostoDeEnvioConfigurado() {
        Pedido pedido = crearPedido.ejecutar(pedidoDe(TipoPedido.DELIVERY, "Calle 10 # 5-20",
                new ItemSolicitado(LIMONADA, 1, null, null)));

        assertEquals(TipoPedido.DELIVERY, pedido.getTipo());
        assertEquals(new BigDecimal("5000"), pedido.costoEnvio());
        // 6000 + 480 + 5000
        assertEquals(new BigDecimal("11480.00"), pedido.total());
    }

    @Test
    void rechazaUnPlatoNoDisponibleYNoGuardaNiPublicaNada() {
        PlatoNoDisponibleException error = assertThrows(PlatoNoDisponibleException.class,
                () -> crearPedido.ejecutar(pedidoDe(TipoPedido.LOCAL, null,
                        new ItemSolicitado(LIMONADA, 1, null, null),
                        new ItemSolicitado(AGOTADO, 1, null, null))));

        assertTrue(error.getMessage().contains("Costillas"));
        assertEquals(0, pedidos.cantidad());
        assertTrue(eventos.eventos.isEmpty());
    }

    @Test
    void rechazaUnPlatoQueNoEsDelRestaurante() {
        assertThrows(PlatoNoDisponibleException.class,
                () -> crearPedido.ejecutar(pedidoDe(TipoPedido.LOCAL, null,
                        new ItemSolicitado(999L, 1, null, null))));
    }

    @Test
    void rechazaUnExtraQueNoPerteneceAlPlato() {
        assertThrows(PedidoInvalidoException.class,
                () -> crearPedido.ejecutar(pedidoDe(TipoPedido.LOCAL, null,
                        new ItemSolicitado(LIMONADA, 1, List.of(QUESO), null))));
        assertEquals(0, pedidos.cantidad());
    }

    @Test
    void rechazaPedidosParaUnRestauranteCerrado() {
        menu.registrar(new RestauranteInfo(RESTAURANTE, "La Brasa", false),
                new PlatoMenu(LIMONADA, "Limonada", new BigDecimal("6000"), true, List.of()));

        assertThrows(RestauranteNoDisponibleException.class,
                () -> crearPedido.ejecutar(pedidoDe(TipoPedido.LOCAL, null,
                        new ItemSolicitado(LIMONADA, 1, null, null))));
        assertTrue(eventos.eventos.isEmpty());
    }

    @Test
    void rechazaPedidosParaUnRestauranteQueNoExiste() {
        CrearPedidoCommand command = new CrearPedidoCommand(77L, "Ana", "ana@correo.com", TipoPedido.LOCAL, null,
                List.of(new ItemSolicitado(LIMONADA, 1, null, null)));

        assertThrows(RestauranteNoDisponibleException.class, () -> crearPedido.ejecutar(command));
    }

    @Test
    void rechazaUnDomicilioSinDireccion() {
        assertThrows(PedidoInvalidoException.class,
                () -> crearPedido.ejecutar(pedidoDe(TipoPedido.DELIVERY, null,
                        new ItemSolicitado(LIMONADA, 1, null, null))));
    }

    @Test
    void rechazaUnPedidoSinItems() {
        assertThrows(PedidoInvalidoException.class,
                () -> crearPedido.ejecutar(pedidoDe(TipoPedido.LOCAL, null)));
    }
}
