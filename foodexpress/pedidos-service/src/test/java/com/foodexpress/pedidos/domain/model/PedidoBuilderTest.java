package com.foodexpress.pedidos.domain.model;

import com.foodexpress.pedidos.domain.exception.PedidoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas del patrón Builder y del cálculo de totales. */
class PedidoBuilderTest {

    private static final BigDecimal IMPUESTO_8 = new BigDecimal("0.08");

    private PedidoBuilder pedidoBase() {
        return Pedido.builder()
                .cliente("Ana", "ana@correo.com")
                .restaurante(1L, "La Brasa")
                .tasaImpuesto(IMPUESTO_8);
    }

    @Test
    void calculaSubtotalImpuestosYTotalConExtras() {
        Pedido pedido = pedidoBase()
                .agregarItem(item -> item
                        .plato(10L, "Hamburguesa", new BigDecimal("25000"))
                        .cantidad(2)
                        .conExtra(3L, "Queso", new BigDecimal("3000"))
                        .conExtra(4L, "Tocineta", new BigDecimal("4000"))
                        .notas("Sin cebolla"))
                .agregarItem(item -> item.plato(20L, "Limonada", new BigDecimal("6000")))
                .paraRecoger()
                .construir();

        // (25000 + 3000 + 4000) * 2 = 64000 ; + 6000 = 70000
        assertEquals(new BigDecimal("70000.00"), pedido.subtotal());
        assertEquals(new BigDecimal("5600.00"), pedido.impuestos());
        assertEquals(new BigDecimal("75600.00"), pedido.total());
        assertEquals(new BigDecimal("64000"), pedido.getItems().get(0).subtotal());
        assertEquals("Sin cebolla", pedido.getItems().get(0).getNotasEspeciales());
    }

    @Test
    void redondeaLosImpuestosADosDecimales() {
        Pedido pedido = pedidoBase()
                .agregarItem(item -> item.plato(10L, "Empanada", new BigDecimal("3333")))
                .construir();

        // 3333 * 0.08 = 266.64
        assertEquals(new BigDecimal("266.64"), pedido.impuestos());
        assertEquals(new BigDecimal("3599.64"), pedido.total());
    }

    @Test
    void unPedidoParaRecogerNoCobraEnvio() {
        Pedido pedido = pedidoBase()
                .agregarItem(item -> item.plato(10L, "Hamburguesa", new BigDecimal("25000")))
                .paraRecoger()
                .construir();

        assertInstanceOf(PedidoLocal.class, pedido);
        assertEquals(TipoPedido.LOCAL, pedido.getTipo());
        assertEquals(0, pedido.costoEnvio().signum());
        assertTrue(pedido.direccionEntrega().isEmpty());
    }

    @Test
    void unPedidoADomicilioSumaElEnvioSinGravarlo() {
        Pedido pedido = pedidoBase()
                .agregarItem(item -> item.plato(10L, "Hamburguesa", new BigDecimal("25000")))
                .conDomicilio("Calle 10 # 5-20", new BigDecimal("5000"))
                .construir();

        assertInstanceOf(PedidoDelivery.class, pedido);
        assertEquals(TipoPedido.DELIVERY, pedido.getTipo());
        assertEquals("Calle 10 # 5-20", pedido.direccionEntrega().orElseThrow());
        assertEquals(new BigDecimal("2000.00"), pedido.impuestos());
        // 25000 + 2000 + 5000
        assertEquals(new BigDecimal("32000.00"), pedido.total());
    }

    @Test
    void lasNotasVaciasSeGuardanComoNulas() {
        Pedido pedido = pedidoBase()
                .agregarItem(item -> item.plato(10L, "Hamburguesa", new BigDecimal("25000")).notas("   "))
                .construir();

        assertNull(pedido.getItems().get(0).getNotasEspeciales());
    }

    @Test
    void rechazaUnPedidoSinPlatos() {
        assertThrows(PedidoInvalidoException.class, () -> pedidoBase().construir());
    }

    @Test
    void rechazaUnPedidoSinCliente() {
        PedidoBuilder builder = Pedido.builder()
                .restaurante(1L, "La Brasa")
                .agregarItem(item -> item.plato(10L, "Hamburguesa", new BigDecimal("25000")));

        assertThrows(PedidoInvalidoException.class, builder::construir);
    }

    @Test
    void rechazaCantidadesMenoresAUno() {
        assertThrows(PedidoInvalidoException.class, () -> pedidoBase()
                .agregarItem(item -> item.plato(10L, "Hamburguesa", new BigDecimal("25000")).cantidad(0)));
    }

    @Test
    void rechazaUnDomicilioSinDireccion() {
        PedidoBuilder builder = pedidoBase()
                .agregarItem(item -> item.plato(10L, "Hamburguesa", new BigDecimal("25000")))
                .conDomicilio("  ", new BigDecimal("5000"));

        assertThrows(PedidoInvalidoException.class, builder::construir);
    }

    @Test
    void losItemsDelPedidoNoSePuedenModificarDesdeAfuera() {
        Pedido pedido = pedidoBase()
                .agregarItem(item -> item.plato(10L, "Hamburguesa", new BigDecimal("25000")))
                .construir();

        assertThrows(UnsupportedOperationException.class, () -> pedido.getItems().clear());
    }
}
