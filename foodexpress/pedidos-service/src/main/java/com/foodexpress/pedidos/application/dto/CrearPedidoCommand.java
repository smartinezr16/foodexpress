package com.foodexpress.pedidos.application.dto;

import com.foodexpress.pedidos.domain.model.TipoPedido;

import java.util.List;

/**
 * Datos que el cliente envía para crear un pedido. No incluye precios: estos
 * siempre se toman del menú del restaurante en el momento de la orden.
 */
public record CrearPedidoCommand(
        Long restauranteId,
        String clienteNombre,
        String clienteEmail,
        TipoPedido tipo,
        String direccionEntrega,
        List<ItemSolicitado> items) {

    public record ItemSolicitado(Long platoId, int cantidad, List<Long> extraIds, String notasEspeciales) {

        public ItemSolicitado {
            extraIds = extraIds == null ? List.of() : List.copyOf(extraIds);
        }
    }
}
