package com.foodexpress.notificaciones.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foodexpress.notificaciones.application.dto.EventoPedido;

import java.math.BigDecimal;
import java.util.List;

/**
 * Forma del JSON que publica el servicio de Pedidos. Se traduce a EventoPedido
 * para que los casos de uso no dependan del formato del mensaje.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PedidoEventoMensaje(
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(Long platoId, String nombre, int cantidad) {
    }

    EventoPedido aEvento() {
        List<EventoPedido.Item> itemsEvento = items == null
                ? List.of()
                : items.stream().map(item -> new EventoPedido.Item(item.nombre(), item.cantidad())).toList();
        return new EventoPedido(eventId, tipo, pedidoId, restauranteId, restauranteNombre, clienteNombre,
                clienteEmail, tipoPedido, estadoAnterior, estadoNuevo, total, itemsEvento, canceladoPor, motivo);
    }
}
