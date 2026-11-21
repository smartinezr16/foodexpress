package com.foodexpress.pedidos.application.port.out;

import com.foodexpress.pedidos.domain.model.Pedido;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de pedidos.
 *
 * DIP: los casos de uso dependen de esta interfaz; la implementación con JPA vive
 * en infraestructura y puede cambiarse sin tocar la lógica de negocio.
 * ISP: expone solo las consultas que los casos de uso necesitan, no un CRUD genérico.
 */
public interface PedidoRepository {

    /** Guarda el pedido y devuelve la versión persistida (con id asignado). */
    Pedido guardar(Pedido pedido);

    Optional<Pedido> buscarPorId(Long pedidoId);

    List<Pedido> buscarPorRestaurante(Long restauranteId);

    List<Pedido> buscarPorCliente(String clienteEmail);
}
