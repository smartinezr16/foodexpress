package com.foodexpress.pedidos.application.usecase;

import com.foodexpress.pedidos.application.exception.PedidoNoEncontradoException;
import com.foodexpress.pedidos.application.port.out.PedidoRepository;
import com.foodexpress.pedidos.domain.model.Pedido;

import java.util.List;

/** Consultas de pedidos: por id, por restaurante o por cliente. */
public class ConsultarPedidosUseCase {

    private final PedidoRepository pedidoRepository;

    public ConsultarPedidosUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido porId(Long pedidoId) {
        return pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));
    }

    public List<Pedido> porRestaurante(Long restauranteId) {
        return pedidoRepository.buscarPorRestaurante(restauranteId);
    }

    public List<Pedido> porCliente(String clienteEmail) {
        return pedidoRepository.buscarPorCliente(clienteEmail);
    }
}
