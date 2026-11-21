package com.foodexpress.pedidos.application.usecase;

import com.foodexpress.pedidos.application.dto.PlatoMenu;
import com.foodexpress.pedidos.application.dto.RestauranteInfo;
import com.foodexpress.pedidos.application.exception.RestauranteNoDisponibleException;
import com.foodexpress.pedidos.application.port.out.MenuRepository;
import com.foodexpress.pedidos.application.port.out.PedidoEventPublisher;
import com.foodexpress.pedidos.application.port.out.PedidoRepository;
import com.foodexpress.pedidos.domain.model.ItemPedido;
import com.foodexpress.pedidos.domain.model.Pedido;
import com.foodexpress.pedidos.domain.model.PedidoBuilder;
import com.foodexpress.pedidos.domain.model.TipoPedido;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedidoTipo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementaciones en memoria de los puertos de salida.
 *
 * Como los casos de uso dependen de interfaces (DIP), se pueden probar sin base
 * de datos, sin RabbitMQ y sin levantar Spring.
 */
final class Fakes {

    private Fakes() {
    }

    /** Menú en memoria: un restaurante con sus platos. */
    static final class MenuEnMemoria implements MenuRepository {

        private final Map<Long, RestauranteInfo> restaurantes = new HashMap<>();
        private final Map<Long, List<PlatoMenu>> platosPorRestaurante = new HashMap<>();

        void registrar(RestauranteInfo restaurante, PlatoMenu... platos) {
            restaurantes.put(restaurante.id(), restaurante);
            platosPorRestaurante.put(restaurante.id(), List.of(platos));
        }

        @Override
        public RestauranteInfo obtenerRestaurante(Long restauranteId) {
            RestauranteInfo restaurante = restaurantes.get(restauranteId);
            if (restaurante == null) {
                throw new RestauranteNoDisponibleException("No existe el restaurante " + restauranteId);
            }
            return restaurante;
        }

        @Override
        public List<PlatoMenu> obtenerPlatos(Long restauranteId, Collection<Long> platoIds) {
            return platosPorRestaurante.getOrDefault(restauranteId, List.of()).stream()
                    .filter(plato -> platoIds.contains(plato.id()))
                    .toList();
        }
    }

    /** Repositorio en memoria que asigna ids y versiones como lo haría la base de datos. */
    static final class PedidosEnMemoria implements PedidoRepository {

        private final Map<Long, Pedido> pedidos = new HashMap<>();
        private long secuencia = 0;

        @Override
        public Pedido guardar(Pedido pedido) {
            Long id = pedido.getId() == null ? ++secuencia : pedido.getId();
            long version = pedido.getVersion() == null ? 0 : pedido.getVersion() + 1;
            Pedido guardado = copiar(pedido, id, version);
            pedidos.put(id, guardado);
            return guardado;
        }

        @Override
        public Optional<Pedido> buscarPorId(Long pedidoId) {
            return Optional.ofNullable(pedidos.get(pedidoId));
        }

        @Override
        public List<Pedido> buscarPorRestaurante(Long restauranteId) {
            return pedidos.values().stream().filter(p -> p.getRestauranteId().equals(restauranteId)).toList();
        }

        @Override
        public List<Pedido> buscarPorCliente(String clienteEmail) {
            return pedidos.values().stream()
                    .filter(p -> p.getCliente().email().equalsIgnoreCase(clienteEmail))
                    .toList();
        }

        int cantidad() {
            return pedidos.size();
        }

        private Pedido copiar(Pedido pedido, Long id, long version) {
            PedidoBuilder builder = Pedido.builder()
                    .conId(id, version)
                    .cliente(pedido.getCliente().nombre(), pedido.getCliente().email())
                    .restaurante(pedido.getRestauranteId(), pedido.getRestauranteNombre())
                    .tasaImpuesto(pedido.getTasaImpuesto())
                    .enEstado(pedido.getEstado())
                    .creadoEn(pedido.getFechaCreacion())
                    .canceladoPor(pedido.canceladoPor().orElse(null), pedido.motivoCancelacion().orElse(null));
            for (ItemPedido item : pedido.getItems()) {
                builder.agregarItem(item);
            }
            if (pedido.getTipo() == TipoPedido.DELIVERY) {
                builder.conDomicilio(pedido.direccionEntrega().orElseThrow(), pedido.costoEnvio());
            }
            return builder.construir();
        }
    }

    /** Publicador que solo anota lo que se le pidió publicar. */
    static final class EventosRegistrados implements PedidoEventPublisher {

        final List<String> eventos = new ArrayList<>();

        @Override
        public void pedidoCreado(Pedido pedido) {
            eventos.add("pedido.creado:" + pedido.getId());
        }

        @Override
        public void estadoCambiado(Pedido pedido, EstadoPedidoTipo estadoAnterior) {
            eventos.add("pedido.estado.cambiado:" + pedido.getId() + ":" + estadoAnterior + "->" + pedido.getEstado());
        }

        @Override
        public void pedidoCancelado(Pedido pedido, EstadoPedidoTipo estadoAnterior) {
            eventos.add("pedido.cancelado:" + pedido.getId() + ":" + estadoAnterior);
        }
    }
}
