package com.foodexpress.restaurantes.infrastructure.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foodexpress.restaurantes.application.dto.MovimientoInventario;

import java.util.List;

/**
 * Parte del evento de pedidos que le interesa a este servicio. El JSON trae más
 * campos; los que no se declaran aquí simplemente se ignoran.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PedidoEvento(String eventId, Long pedidoId, Long restauranteId, List<Item> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(Long platoId, String nombre, int cantidad) {
    }

    List<MovimientoInventario> aMovimientos() {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> new MovimientoInventario(item.platoId(), item.cantidad()))
                .toList();
    }
}
