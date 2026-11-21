package com.foodexpress.pedidos.domain.model.estado;

import java.util.function.Supplier;

/**
 * Identificador de cada estado. Es lo que se persiste y lo que viaja en los eventos;
 * a partir de él se reconstruye el objeto de estado correspondiente.
 */
public enum EstadoPedidoTipo {
    RECIBIDO(EstadoRecibido::new),
    EN_PREPARACION(EstadoEnPreparacion::new),
    LISTO(EstadoListo::new),
    ENTREGADO(EstadoEntregado::new),
    CANCELADO(EstadoCancelado::new);

    private final Supplier<EstadoPedido> fabrica;

    EstadoPedidoTipo(Supplier<EstadoPedido> fabrica) {
        this.fabrica = fabrica;
    }

    public EstadoPedido crearEstado() {
        return fabrica.get();
    }
}
