package com.foodexpress.pedidos.domain.model;

import com.foodexpress.pedidos.domain.exception.TransicionInvalidaException;
import com.foodexpress.pedidos.domain.model.estado.ActorCancelacion;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedidoTipo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas de la máquina de estados del pedido (patrón State). */
class EstadoPedidoTest {

    private Pedido pedidoEn(EstadoPedidoTipo estado) {
        return Pedido.builder()
                .cliente("Ana", "ana@correo.com")
                .restaurante(1L, "La Brasa")
                .agregarItem(item -> item.plato(10L, "Hamburguesa", new BigDecimal("25000")))
                .enEstado(estado)
                .construir();
    }

    @Test
    void unPedidoNuevoEmpiezaRecibido() {
        Pedido pedido = pedidoEn(EstadoPedidoTipo.RECIBIDO);

        assertEquals(EstadoPedidoTipo.RECIBIDO, pedido.getEstado());
        assertFalse(pedido.estaFinalizado());
    }

    @Test
    void recorreElFlujoCompletoHastaEntregado() {
        Pedido pedido = pedidoEn(EstadoPedidoTipo.RECIBIDO);

        assertEquals(EstadoPedidoTipo.RECIBIDO, pedido.avanzar());
        assertEquals(EstadoPedidoTipo.EN_PREPARACION, pedido.getEstado());

        assertEquals(EstadoPedidoTipo.EN_PREPARACION, pedido.avanzar());
        assertEquals(EstadoPedidoTipo.LISTO, pedido.getEstado());

        assertEquals(EstadoPedidoTipo.LISTO, pedido.avanzar());
        assertEquals(EstadoPedidoTipo.ENTREGADO, pedido.getEstado());
        assertTrue(pedido.estaFinalizado());
    }

    @Test
    void unPedidoEntregadoNoAvanzaNiSeCancela() {
        Pedido pedido = pedidoEn(EstadoPedidoTipo.ENTREGADO);

        assertThrows(TransicionInvalidaException.class, pedido::avanzar);
        assertThrows(TransicionInvalidaException.class,
                () -> pedido.cancelar(ActorCancelacion.RESTAURANTE, "tarde"));
        assertEquals(EstadoPedidoTipo.ENTREGADO, pedido.getEstado());
    }

    @Test
    void elClientePuedeCancelarMientrasEstaRecibido() {
        Pedido pedido = pedidoEn(EstadoPedidoTipo.RECIBIDO);

        EstadoPedidoTipo anterior = pedido.cancelar(ActorCancelacion.CLIENTE, "  Me equivoqué de plato ");

        assertEquals(EstadoPedidoTipo.RECIBIDO, anterior);
        assertEquals(EstadoPedidoTipo.CANCELADO, pedido.getEstado());
        assertEquals(ActorCancelacion.CLIENTE, pedido.canceladoPor().orElseThrow());
        assertEquals("Me equivoqué de plato", pedido.motivoCancelacion().orElseThrow());
        assertTrue(pedido.estaFinalizado());
    }

    @Test
    void enPreparacionElClienteYaNoPuedeCancelar() {
        Pedido pedido = pedidoEn(EstadoPedidoTipo.EN_PREPARACION);

        assertThrows(TransicionInvalidaException.class,
                () -> pedido.cancelar(ActorCancelacion.CLIENTE, "ya no lo quiero"));

        assertEquals(EstadoPedidoTipo.EN_PREPARACION, pedido.getEstado());
        assertTrue(pedido.canceladoPor().isEmpty());
    }

    @Test
    void enPreparacionElRestauranteSiPuedeCancelar() {
        Pedido pedido = pedidoEn(EstadoPedidoTipo.EN_PREPARACION);

        pedido.cancelar(ActorCancelacion.RESTAURANTE, "Se acabó un ingrediente");

        assertEquals(EstadoPedidoTipo.CANCELADO, pedido.getEstado());
    }

    @Test
    void unPedidoListoNoSePuedeCancelar() {
        Pedido pedido = pedidoEn(EstadoPedidoTipo.LISTO);

        assertThrows(TransicionInvalidaException.class,
                () -> pedido.cancelar(ActorCancelacion.RESTAURANTE, "error"));
        assertThrows(TransicionInvalidaException.class,
                () -> pedido.cancelar(ActorCancelacion.CLIENTE, "error"));
    }

    @Test
    void unPedidoCanceladoNoAvanzaNiSeCancelaDeNuevo() {
        Pedido pedido = pedidoEn(EstadoPedidoTipo.CANCELADO);

        assertThrows(TransicionInvalidaException.class, pedido::avanzar);
        assertThrows(TransicionInvalidaException.class,
                () -> pedido.cancelar(ActorCancelacion.CLIENTE, "otra vez"));
    }

    @Test
    void localYDeliveryFluyenPorLaMismaMaquinaDeEstados() {
        Pedido delivery = Pedido.builder()
                .cliente("Ana", "ana@correo.com")
                .restaurante(1L, "La Brasa")
                .agregarItem(item -> item.plato(10L, "Hamburguesa", new BigDecimal("25000")))
                .conDomicilio("Calle 10 # 5-20", new BigDecimal("5000"))
                .construir();

        delivery.avanzar();
        delivery.avanzar();
        delivery.avanzar();

        assertEquals(EstadoPedidoTipo.ENTREGADO, delivery.getEstado());
    }
}
