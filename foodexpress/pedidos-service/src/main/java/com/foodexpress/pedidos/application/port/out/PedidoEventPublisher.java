package com.foodexpress.pedidos.application.port.out;

import com.foodexpress.pedidos.domain.model.Pedido;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedidoTipo;

/**
 * Puerto de salida para publicar los eventos del pedido.
 *
 * SRP: Pedidos solo anuncia lo que ocurrió. Quién reacciona (Notificaciones,
 * Restaurantes) y cómo lo hace no es asunto de este servicio.
 */
public interface PedidoEventPublisher {

    void pedidoCreado(Pedido pedido);

    void estadoCambiado(Pedido pedido, EstadoPedidoTipo estadoAnterior);

    void pedidoCancelado(Pedido pedido, EstadoPedidoTipo estadoAnterior);
}
