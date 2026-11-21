package com.foodexpress.notificaciones.application.usecase;

import com.foodexpress.notificaciones.application.dto.EventoPedido;
import com.foodexpress.notificaciones.application.port.out.EventoProcesadoRepository;
import com.foodexpress.notificaciones.domain.model.Canal;
import com.foodexpress.notificaciones.domain.model.Destinatario;
import com.foodexpress.notificaciones.domain.model.Mensaje;

import java.util.EnumSet;

/**
 * Reacciona a pedido.creado: alerta al restaurante del nuevo pedido y envía la
 * confirmación al cliente.
 */
public class NotificarPedidoCreadoUseCase {

    private final EventoProcesadoRepository eventosProcesados;
    private final DespachadorNotificaciones despachador;

    public NotificarPedidoCreadoUseCase(EventoProcesadoRepository eventosProcesados,
                                        DespachadorNotificaciones despachador) {
        this.eventosProcesados = eventosProcesados;
        this.despachador = despachador;
    }

    public void ejecutar(EventoPedido evento) {
        if (!eventosProcesados.registrarSiEsNuevo(evento.eventId())) {
            return;
        }

        String modalidad = evento.esDomicilio() ? "a domicilio" : "para recoger";

        Mensaje alRestaurante = new Mensaje(
                evento.pedidoId(), evento.restauranteId(),
                Destinatario.restaurante(evento.restauranteNombre()),
                evento.tipo(), evento.estadoNuevo(),
                "Nuevo pedido #" + evento.pedidoId(),
                "Pedido " + modalidad + " de " + evento.clienteNombre() + ": " + Textos.resumenItems(evento)
                        + ". Total " + Textos.pesos(evento.total()) + ".");
        despachador.despachar(evento.eventId(), alRestaurante, EnumSet.of(Canal.WEBSOCKET));

        Mensaje alCliente = new Mensaje(
                evento.pedidoId(), evento.restauranteId(),
                Destinatario.cliente(evento.clienteNombre(), evento.clienteEmail()),
                evento.tipo(), evento.estadoNuevo(),
                "Recibimos tu pedido #" + evento.pedidoId(),
                "Hola " + evento.clienteNombre() + ", " + evento.restauranteNombre() + " recibió tu pedido "
                        + modalidad + ": " + Textos.resumenItems(evento)
                        + ". Total " + Textos.pesos(evento.total()) + ". Te avisaremos cuando cambie de estado.");
        despachador.despachar(evento.eventId(), alCliente, EnumSet.of(Canal.EMAIL, Canal.WEBSOCKET));
    }
}
