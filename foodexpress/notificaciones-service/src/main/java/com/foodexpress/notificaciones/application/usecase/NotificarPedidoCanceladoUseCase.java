package com.foodexpress.notificaciones.application.usecase;

import com.foodexpress.notificaciones.application.dto.EventoPedido;
import com.foodexpress.notificaciones.application.port.out.EventoProcesadoRepository;
import com.foodexpress.notificaciones.domain.model.Canal;
import com.foodexpress.notificaciones.domain.model.Destinatario;
import com.foodexpress.notificaciones.domain.model.Mensaje;

import java.util.EnumSet;

/**
 * Reacciona a pedido.cancelado: alerta a la contraparte. Si canceló el cliente se
 * avisa al restaurante, y si canceló el restaurante se avisa al cliente.
 */
public class NotificarPedidoCanceladoUseCase {

    private final EventoProcesadoRepository eventosProcesados;
    private final DespachadorNotificaciones despachador;

    public NotificarPedidoCanceladoUseCase(EventoProcesadoRepository eventosProcesados,
                                           DespachadorNotificaciones despachador) {
        this.eventosProcesados = eventosProcesados;
        this.despachador = despachador;
    }

    public void ejecutar(EventoPedido evento) {
        if (!eventosProcesados.registrarSiEsNuevo(evento.eventId())) {
            return;
        }

        String motivo = (evento.motivo() == null || evento.motivo().isBlank())
                ? ""
                : " Motivo: " + evento.motivo() + ".";

        if (evento.canceladoPorCliente()) {
            Mensaje alRestaurante = new Mensaje(
                    evento.pedidoId(), evento.restauranteId(),
                    Destinatario.restaurante(evento.restauranteNombre()),
                    evento.tipo(), evento.estadoNuevo(),
                    "Pedido #" + evento.pedidoId() + " cancelado por el cliente",
                    evento.clienteNombre() + " canceló su pedido. No es necesario prepararlo." + motivo);
            despachador.despachar(evento.eventId(), alRestaurante, EnumSet.of(Canal.WEBSOCKET));
        } else {
            Mensaje alCliente = new Mensaje(
                    evento.pedidoId(), evento.restauranteId(),
                    Destinatario.cliente(evento.clienteNombre(), evento.clienteEmail()),
                    evento.tipo(), evento.estadoNuevo(),
                    "Tu pedido #" + evento.pedidoId() + " fue cancelado",
                    "Lo sentimos, " + evento.restauranteNombre() + " canceló tu pedido." + motivo);
            despachador.despachar(evento.eventId(), alCliente, EnumSet.of(Canal.EMAIL, Canal.WEBSOCKET, Canal.PUSH));
        }
    }
}
