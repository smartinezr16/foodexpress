package com.foodexpress.notificaciones.application.dto;

import java.math.BigDecimal;
import java.util.List;

/** Evento de pedido tal como lo necesitan los casos de uso de notificación. */
public record EventoPedido(
        String eventId,
        String tipo,
        Long pedidoId,
        Long restauranteId,
        String restauranteNombre,
        String clienteNombre,
        String clienteEmail,
        String tipoPedido,
        String estadoAnterior,
        String estadoNuevo,
        BigDecimal total,
        List<Item> items,
        String canceladoPor,
        String motivo) {

    public EventoPedido {
        items = items == null ? List.of() : List.copyOf(items);
    }

    public record Item(String nombre, int cantidad) {
    }

    public boolean esDomicilio() {
        return "DELIVERY".equals(tipoPedido);
    }

    public boolean canceladoPorCliente() {
        return "CLIENTE".equals(canceladoPor);
    }
}
