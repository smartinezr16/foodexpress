package com.foodexpress.restaurantes.application.usecase;

import com.foodexpress.restaurantes.application.dto.MovimientoInventario;
import com.foodexpress.restaurantes.application.port.in.InventarioService;
import com.foodexpress.restaurantes.application.port.out.EventoProcesadoRepository;
import com.foodexpress.restaurantes.application.port.out.MenuCache;
import com.foodexpress.restaurantes.application.port.out.PlatoRepository;
import com.foodexpress.restaurantes.domain.model.Plato;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Mantiene la disponibilidad de los platos al día a partir de los eventos de pedidos:
 * un pedido creado descuenta unidades y un pedido cancelado las devuelve.
 */
public class AjustarInventarioUseCase implements InventarioService {

    private final PlatoRepository platoRepository;
    private final EventoProcesadoRepository eventosProcesados;
    private final MenuCache menuCache;

    public AjustarInventarioUseCase(PlatoRepository platoRepository,
                                    EventoProcesadoRepository eventosProcesados,
                                    MenuCache menuCache) {
        this.platoRepository = platoRepository;
        this.eventosProcesados = eventosProcesados;
        this.menuCache = menuCache;
    }

    @Override
    public void descontarPorPedidoCreado(String eventId, List<MovimientoInventario> movimientos) {
        aplicar(eventId, movimientos, Plato::descontarUnidades);
    }

    @Override
    public void reponerPorPedidoCancelado(String eventId, List<MovimientoInventario> movimientos) {
        aplicar(eventId, movimientos, Plato::reponerUnidades);
    }

    private void aplicar(String eventId, List<MovimientoInventario> movimientos, BiConsumer<Plato, Integer> ajuste) {
        if (!eventosProcesados.registrarSiEsNuevo(eventId)) {
            return; // Entrega repetida del mismo evento: ya se aplicó.
        }

        Set<Long> restaurantesAfectados = new HashSet<>();
        for (Map.Entry<Long, Integer> movimiento : agruparPorPlato(movimientos).entrySet()) {
            Optional<Plato> encontrado = platoRepository.buscarPorId(movimiento.getKey());
            if (encontrado.isEmpty()) {
                continue; // El plato fue retirado del menú después del pedido.
            }
            Plato plato = encontrado.get();
            ajuste.accept(plato, movimiento.getValue());
            platoRepository.guardar(plato);
            restaurantesAfectados.add(plato.getRestauranteId());
        }
        restaurantesAfectados.forEach(menuCache::invalidar);
    }

    /** Un mismo plato puede venir en varios ítems del pedido: se suma su cantidad. */
    private Map<Long, Integer> agruparPorPlato(List<MovimientoInventario> movimientos) {
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        if (movimientos != null) {
            for (MovimientoInventario movimiento : movimientos) {
                cantidades.merge(movimiento.platoId(), movimiento.cantidad(), Integer::sum);
            }
        }
        return cantidades;
    }
}
