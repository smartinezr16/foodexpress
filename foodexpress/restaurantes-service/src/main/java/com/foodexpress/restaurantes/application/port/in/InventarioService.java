package com.foodexpress.restaurantes.application.port.in;

import com.foodexpress.restaurantes.application.dto.MovimientoInventario;

import java.util.List;

/**
 * Puerto de entrada para ajustar el inventario de platos como reacción a los
 * eventos de pedidos. El eventId hace que procesar dos veces el mismo evento
 * no altere dos veces el inventario.
 */
public interface InventarioService {

    void descontarPorPedidoCreado(String eventId, List<MovimientoInventario> movimientos);

    void reponerPorPedidoCancelado(String eventId, List<MovimientoInventario> movimientos);
}
