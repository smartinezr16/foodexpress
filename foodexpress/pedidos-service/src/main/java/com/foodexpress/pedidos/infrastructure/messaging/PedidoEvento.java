package com.foodexpress.pedidos.infrastructure.messaging;

import com.foodexpress.pedidos.domain.model.Pedido;
import com.foodexpress.pedidos.domain.model.estado.ActorCancelacion;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedidoTipo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Contrato de los eventos que publica Pedidos (se serializa como JSON).
 *
 * Los tres eventos comparten estructura; los campos que no aplican viajan en null.
 * El eventId permite a los consumidores descartar mensajes repetidos.
 */
public record PedidoEvento(
        String eventId,
        String tipo,
        Instant ocurridoEn,
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

    public record Item(Long platoId, String nombre, int cantidad) {
    }

    static PedidoEvento de(String tipo, Pedido pedido, EstadoPedidoTipo estadoAnterior) {
        List<Item> items = pedido.getItems().stream()
                .map(item -> new Item(item.getPlatoId(), item.getNombrePlato(), item.getCantidad()))
                .toList();
        return new PedidoEvento(
                UUID.randomUUID().toString(),
                tipo,
                Instant.now(),
                pedido.getId(),
                pedido.getRestauranteId(),
                pedido.getRestauranteNombre(),
                pedido.getCliente().nombre(),
                pedido.getCliente().email(),
                pedido.getTipo().name(),
                estadoAnterior == null ? null : estadoAnterior.name(),
                pedido.getEstado().name(),
                pedido.total(),
                items,
                pedido.canceladoPor().map(ActorCancelacion::name).orElse(null),
                pedido.motivoCancelacion().orElse(null));
    }
}
