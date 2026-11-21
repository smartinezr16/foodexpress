package com.foodexpress.notificaciones.infrastructure.web;

import com.foodexpress.notificaciones.application.usecase.ConsultarNotificacionesUseCase;
import com.foodexpress.notificaciones.domain.model.Notificacion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/** Consulta del historial de notificaciones; útil para verificar qué se envió por cada pedido. */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final ConsultarNotificacionesUseCase consultarNotificaciones;

    public NotificacionController(ConsultarNotificacionesUseCase consultarNotificaciones) {
        this.consultarNotificaciones = consultarNotificaciones;
    }

    @GetMapping
    public List<NotificacionResponse> porPedido(@RequestParam("pedidoId") Long pedidoId) {
        return consultarNotificaciones.porPedido(pedidoId).stream().map(NotificacionResponse::de).toList();
    }

    public record NotificacionResponse(Long id, Long pedidoId, String canal, String tipoDestinatario,
                                       String destinatario, String asunto, String cuerpo, String estado,
                                       String detalleError, Instant fecha) {

        static NotificacionResponse de(Notificacion n) {
            return new NotificacionResponse(n.id(), n.pedidoId(), n.canal().name(), n.tipoDestinatario().name(),
                    n.destinatario(), n.asunto(), n.cuerpo(), n.estado().name(), n.detalleError(), n.fecha());
        }
    }
}
