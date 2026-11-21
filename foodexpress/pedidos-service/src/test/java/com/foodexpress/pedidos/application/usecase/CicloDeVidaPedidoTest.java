package com.foodexpress.pedidos.application.usecase;

import com.foodexpress.pedidos.application.exception.PedidoNoEncontradoException;
import com.foodexpress.pedidos.application.usecase.Fakes.EventosRegistrados;
import com.foodexpress.pedidos.application.usecase.Fakes.PedidosEnMemoria;
import com.foodexpress.pedidos.domain.exception.TransicionInvalidaException;
import com.foodexpress.pedidos.domain.model.Pedido;
import com.foodexpress.pedidos.domain.model.estado.ActorCancelacion;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedidoTipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Avance de estado y cancelación a través de los casos de uso. */
class CicloDeVidaPedidoTest {

    private PedidosEnMemoria pedidos;
    private EventosRegistrados eventos;
    private AvanzarEstadoPedidoUseCase avanzar;
    private CancelarPedidoUseCase cancelar;
    private Long pedidoId;

    @BeforeEach
    void preparar() {
        pedidos = new PedidosEnMemoria();
        eventos = new EventosRegistrados();
        avanzar = new AvanzarEstadoPedidoUseCase(pedidos, eventos);
        cancelar = new CancelarPedidoUseCase(pedidos, eventos);
        pedidoId = pedidos.guardar(Pedido.builder()
                .cliente("Ana", "ana@correo.com")
                .restaurante(1L, "La Brasa")
                .agregarItem(item -> item.plato(10L, "Hamburguesa", new BigDecimal("25000")))
                .construir()).getId();
    }

    @Test
    void cadaAvanceGuardaElNuevoEstadoYPublicaElCambio() {
        avanzar.ejecutar(pedidoId);
        avanzar.ejecutar(pedidoId);
        Pedido entregado = avanzar.ejecutar(pedidoId);

        assertEquals(EstadoPedidoTipo.ENTREGADO, entregado.getEstado());
        assertEquals(EstadoPedidoTipo.ENTREGADO, pedidos.buscarPorId(pedidoId).orElseThrow().getEstado());
        assertEquals(List.of(
                "pedido.estado.cambiado:" + pedidoId + ":RECIBIDO->EN_PREPARACION",
                "pedido.estado.cambiado:" + pedidoId + ":EN_PREPARACION->LISTO",
                "pedido.estado.cambiado:" + pedidoId + ":LISTO->ENTREGADO"), eventos.eventos);
    }

    @Test
    void noSePuedeAvanzarUnPedidoEntregado() {
        avanzar.ejecutar(pedidoId);
        avanzar.ejecutar(pedidoId);
        avanzar.ejecutar(pedidoId);
        eventos.eventos.clear();

        assertThrows(TransicionInvalidaException.class, () -> avanzar.ejecutar(pedidoId));
        assertTrue(eventos.eventos.isEmpty());
    }

    @Test
    void laCancelacionDelClientePublicaPedidoCancelado() {
        Pedido cancelado = cancelar.ejecutar(pedidoId, ActorCancelacion.CLIENTE, "Cambié de opinión");

        assertEquals(EstadoPedidoTipo.CANCELADO, cancelado.getEstado());
        assertEquals(ActorCancelacion.CLIENTE, cancelado.canceladoPor().orElseThrow());
        assertEquals("Cambié de opinión", cancelado.motivoCancelacion().orElseThrow());
        assertEquals(List.of("pedido.cancelado:" + pedidoId + ":RECIBIDO"), eventos.eventos);
    }

    @Test
    void siElEstadoNoPermiteCancelarNoSeGuardaNiSePublica() {
        avanzar.ejecutar(pedidoId);
        eventos.eventos.clear();

        assertThrows(TransicionInvalidaException.class,
                () -> cancelar.ejecutar(pedidoId, ActorCancelacion.CLIENTE, "tarde"));

        assertEquals(EstadoPedidoTipo.EN_PREPARACION, pedidos.buscarPorId(pedidoId).orElseThrow().getEstado());
        assertTrue(eventos.eventos.isEmpty());
    }

    @Test
    void operarSobreUnPedidoInexistenteFalla() {
        assertThrows(PedidoNoEncontradoException.class, () -> avanzar.ejecutar(999L));
        assertThrows(PedidoNoEncontradoException.class,
                () -> cancelar.ejecutar(999L, ActorCancelacion.RESTAURANTE, null));
    }
}
