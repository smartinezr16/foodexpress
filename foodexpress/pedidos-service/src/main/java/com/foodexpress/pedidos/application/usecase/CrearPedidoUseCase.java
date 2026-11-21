package com.foodexpress.pedidos.application.usecase;

import com.foodexpress.pedidos.application.dto.CrearPedidoCommand;
import com.foodexpress.pedidos.application.dto.CrearPedidoCommand.ItemSolicitado;
import com.foodexpress.pedidos.application.dto.ParametrosPedido;
import com.foodexpress.pedidos.application.dto.PlatoMenu;
import com.foodexpress.pedidos.application.dto.PlatoMenu.ExtraMenu;
import com.foodexpress.pedidos.application.dto.RestauranteInfo;
import com.foodexpress.pedidos.application.exception.PlatoNoDisponibleException;
import com.foodexpress.pedidos.application.exception.RestauranteNoDisponibleException;
import com.foodexpress.pedidos.application.port.out.MenuRepository;
import com.foodexpress.pedidos.application.port.out.PedidoEventPublisher;
import com.foodexpress.pedidos.application.port.out.PedidoRepository;
import com.foodexpress.pedidos.domain.exception.PedidoInvalidoException;
import com.foodexpress.pedidos.domain.model.ItemPedido;
import com.foodexpress.pedidos.domain.model.Pedido;
import com.foodexpress.pedidos.domain.model.PedidoBuilder;
import com.foodexpress.pedidos.domain.model.TipoPedido;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Crea un pedido validando contra el menú vigente del restaurante.
 *
 * DIP: depende únicamente de interfaces (MenuRepository, PedidoRepository,
 * PedidoEventPublisher). No conoce JPA, HTTP ni RabbitMQ.
 */
public class CrearPedidoUseCase {

    private final MenuRepository menuRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoEventPublisher eventPublisher;
    private final ParametrosPedido parametros;

    public CrearPedidoUseCase(MenuRepository menuRepository,
                              PedidoRepository pedidoRepository,
                              PedidoEventPublisher eventPublisher,
                              ParametrosPedido parametros) {
        this.menuRepository = menuRepository;
        this.pedidoRepository = pedidoRepository;
        this.eventPublisher = eventPublisher;
        this.parametros = parametros;
    }

    public Pedido ejecutar(CrearPedidoCommand command) {
        if (command.items() == null || command.items().isEmpty()) {
            throw new PedidoInvalidoException("El pedido debe tener al menos un plato");
        }

        RestauranteInfo restaurante = menuRepository.obtenerRestaurante(command.restauranteId());
        if (!restaurante.abierto()) {
            throw new RestauranteNoDisponibleException(
                    "El restaurante '" + restaurante.nombre() + "' está cerrado y no recibe pedidos");
        }

        Map<Long, PlatoMenu> platos = consultarPlatos(command);

        PedidoBuilder builder = Pedido.builder()
                .cliente(command.clienteNombre(), command.clienteEmail())
                .restaurante(restaurante.id(), restaurante.nombre())
                .tasaImpuesto(parametros.tasaImpuesto());

        for (ItemSolicitado solicitado : command.items()) {
            builder.agregarItem(construirItem(solicitado, platos, restaurante));
        }

        if (command.tipo() == TipoPedido.DELIVERY) {
            builder.conDomicilio(command.direccionEntrega(), parametros.costoDomicilio());
        } else {
            builder.paraRecoger();
        }

        Pedido pedido = pedidoRepository.guardar(builder.construir());
        eventPublisher.pedidoCreado(pedido);
        return pedido;
    }

    private Map<Long, PlatoMenu> consultarPlatos(CrearPedidoCommand command) {
        List<Long> platoIds = command.items().stream()
                .map(ItemSolicitado::platoId)
                .distinct()
                .toList();
        return menuRepository.obtenerPlatos(command.restauranteId(), platoIds).stream()
                .collect(Collectors.toMap(PlatoMenu::id, Function.identity()));
    }

    /** Valida el ítem contra el menú y toma de allí nombres y precios. */
    private ItemPedido construirItem(ItemSolicitado solicitado,
                                     Map<Long, PlatoMenu> platos,
                                     RestauranteInfo restaurante) {
        PlatoMenu plato = platos.get(solicitado.platoId());
        if (plato == null) {
            throw new PlatoNoDisponibleException("El plato " + solicitado.platoId()
                    + " no pertenece al menú de '" + restaurante.nombre() + "'");
        }
        if (!plato.disponible()) {
            throw new PlatoNoDisponibleException("El plato '" + plato.nombre() + "' no está disponible en este momento");
        }

        ItemPedido.Builder item = ItemPedido.builder()
                .plato(plato.id(), plato.nombre(), plato.precio())
                .cantidad(solicitado.cantidad())
                .notas(solicitado.notasEspeciales());

        for (Long extraId : solicitado.extraIds()) {
            ExtraMenu extra = plato.buscarExtra(extraId).orElseThrow(() -> new PedidoInvalidoException(
                    "El extra " + extraId + " no existe para el plato '" + plato.nombre() + "'"));
            item.conExtra(extra.id(), extra.nombre(), extra.precioAdicional());
        }
        return item.construir();
    }
}
