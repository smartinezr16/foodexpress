package com.foodexpress.pedidos.application.usecase;

import com.foodexpress.pedidos.application.exception.PedidoNoEncontradoException;
import com.foodexpress.pedidos.application.port.out.PedidoEventPublisher;
import com.foodexpress.pedidos.application.port.out.PedidoRepository;
import com.foodexpress.pedidos.domain.model.Pedido;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedidoTipo;

/**
 * Mueve el pedido al siguiente estado de su flujo
 * (recibido, en preparación, listo, entregado) y publica el cambio.
 */
public class AvanzarEstadoPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PedidoEventPublisher eventPublisher;

    public AvanzarEstadoPedidoUseCase(PedidoRepository pedidoRepository, PedidoEventPublisher eventPublisher) {
        this.pedidoRepository = pedidoRepository;
        this.eventPublisher = eventPublisher;
    }

    public Pedido ejecutar(Long pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));

        EstadoPedidoTipo estadoAnterior = pedido.avanzar();

        Pedido actualizado = pedidoRepository.guardar(pedido);
        eventPublisher.estadoCambiado(actualizado, estadoAnterior);
        return actualizado;
    }
}
