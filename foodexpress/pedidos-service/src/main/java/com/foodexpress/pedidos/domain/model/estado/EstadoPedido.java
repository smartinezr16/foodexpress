package com.foodexpress.pedidos.domain.model.estado;

/**
 * PATRÓN STATE.
 *
 * Cada estado del pedido es una clase que conoce sus propias transiciones válidas
 * y sus propias reglas de cancelación. El pedido delega en su estado actual en
 * lugar de tener condicionales del tipo "if (estado == LISTO) ...".
 *
 * OCP: agregar un estado nuevo (por ejemplo EnCamino para domicilios) consiste en
 * crear una clase nueva y ajustar el estado que le precede; los demás no se tocan.
 */
public interface EstadoPedido {

    EstadoPedidoTipo tipo();

    /** Devuelve el estado que sigue en el flujo normal del pedido. */
    EstadoPedido siguiente();

    /** Devuelve el estado cancelado si el actor puede cancelar en este punto. */
    EstadoPedido cancelar(ActorCancelacion actor);

    /** Un estado final no admite más transiciones. */
    default boolean esFinal() {
        return false;
    }
}
