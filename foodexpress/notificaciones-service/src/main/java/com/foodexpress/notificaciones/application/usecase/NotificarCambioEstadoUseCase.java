package com.foodexpress.notificaciones.application.usecase;

import com.foodexpress.notificaciones.application.dto.EventoPedido;
import com.foodexpress.notificaciones.application.port.out.EventoProcesadoRepository;
import com.foodexpress.notificaciones.domain.model.Canal;
import com.foodexpress.notificaciones.domain.model.Destinatario;
import com.foodexpress.notificaciones.domain.model.Mensaje;

import java.util.EnumSet;
import java.util.Set;

/**
 * Reacciona a pedido.estado.cambiado: informa al cliente en tiempo real. Cuando el
 * pedido queda listo, además le envía un correo.
 */
public class NotificarCambioEstadoUseCase {

    private static final String ESTADO_LISTO = "LISTO";

    private final EventoProcesadoRepository eventosProcesados;
    private final DespachadorNotificaciones despachador;

    public NotificarCambioEstadoUseCase(EventoProcesadoRepository eventosProcesados,
                                        DespachadorNotificaciones despachador) {
        this.eventosProcesados = eventosProcesados;
        this.despachador = despachador;
    }

    public void ejecutar(EventoPedido evento) {
        if (!eventosProcesados.registrarSiEsNuevo(evento.eventId())) {
            return;
        }

        Mensaje mensaje = new Mensaje(
                evento.pedidoId(), evento.restauranteId(),
                Destinatario.cliente(evento.clienteNombre(), evento.clienteEmail()),
                evento.tipo(), evento.estadoNuevo(),
                asunto(evento), cuerpo(evento));

        Set<Canal> canales = EnumSet.of(Canal.WEBSOCKET, Canal.PUSH);
        if (ESTADO_LISTO.equals(evento.estadoNuevo())) {
            canales.add(Canal.EMAIL);
        }
        despachador.despachar(evento.eventId(), mensaje, canales);
    }

    private String asunto(EventoPedido evento) {
        String pedido = "Tu pedido #" + evento.pedidoId();
        if (evento.estadoNuevo() == null) {
            return pedido + " cambió de estado";
        }
        return switch (evento.estadoNuevo()) {
            case "EN_PREPARACION" -> pedido + " está en preparación";
            case ESTADO_LISTO -> evento.esDomicilio() ? pedido + " está listo y va en camino" : pedido + " está listo para recoger";
            case "ENTREGADO" -> pedido + " fue entregado";
            default -> pedido + " cambió de estado";
        };
    }

    private String cuerpo(EventoPedido evento) {
        String restaurante = evento.restauranteNombre();
        if (evento.estadoNuevo() == null) {
            return "El pedido cambió de estado.";
        }
        return switch (evento.estadoNuevo()) {
            case "EN_PREPARACION" -> restaurante + " empezó a preparar tu pedido.";
            case ESTADO_LISTO -> evento.esDomicilio()
                    ? "Tu pedido de " + restaurante + " ya salió hacia tu dirección."
                    : "Ya puedes pasar por tu pedido a " + restaurante + ".";
            case "ENTREGADO" -> "Gracias por pedir en " + restaurante + ". ¡Buen provecho!";
            default -> "El pedido pasó de " + evento.estadoAnterior() + " a " + evento.estadoNuevo() + ".";
        };
    }
}
