package com.foodexpress.pedidos.infrastructure.web.dto;

import com.foodexpress.pedidos.domain.model.ItemPedido;
import com.foodexpress.pedidos.domain.model.Pedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PedidoResponse(
        Long id,
        String estado,
        String tipo,
        ClienteResponse cliente,
        Long restauranteId,
        String restauranteNombre,
        String direccionEntrega,
        List<ItemResponse> items,
        BigDecimal subtotal,
        BigDecimal impuestos,
        BigDecimal costoEnvio,
        BigDecimal total,
        Instant fechaCreacion,
        String canceladoPor,
        String motivoCancelacion) {

    public record ClienteResponse(String nombre, String email) {
    }

    public record ItemResponse(
            Long platoId,
            String nombrePlato,
            BigDecimal precioUnitario,
            int cantidad,
            List<ExtraResponse> extras,
            String notasEspeciales,
            BigDecimal subtotal) {
    }

    public record ExtraResponse(Long extraId, String nombre, BigDecimal precioAdicional) {
    }

    public static PedidoResponse de(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getEstado().name(),
                pedido.getTipo().name(),
                new ClienteResponse(pedido.getCliente().nombre(), pedido.getCliente().email()),
                pedido.getRestauranteId(),
                pedido.getRestauranteNombre(),
                pedido.direccionEntrega().orElse(null),
                pedido.getItems().stream().map(PedidoResponse::item).toList(),
                pedido.subtotal(),
                pedido.impuestos(),
                pedido.costoEnvio(),
                pedido.total(),
                pedido.getFechaCreacion(),
                pedido.canceladoPor().map(Enum::name).orElse(null),
                pedido.motivoCancelacion().orElse(null));
    }

    private static ItemResponse item(ItemPedido item) {
        List<ExtraResponse> extras = item.getExtras().stream()
                .map(extra -> new ExtraResponse(extra.extraId(), extra.nombre(), extra.precioAdicional()))
                .toList();
        return new ItemResponse(item.getPlatoId(), item.getNombrePlato(), item.getPrecioUnitario(),
                item.getCantidad(), extras, item.getNotasEspeciales(), item.subtotal());
    }
}
