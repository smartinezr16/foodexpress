package com.foodexpress.notificaciones.application.usecase;

import com.foodexpress.notificaciones.application.port.out.NotificacionRepository;
import com.foodexpress.notificaciones.domain.model.Notificacion;

import java.util.List;

/** Historial de notificaciones de un pedido. */
public class ConsultarNotificacionesUseCase {

    private final NotificacionRepository notificacionRepository;

    public ConsultarNotificacionesUseCase(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public List<Notificacion> porPedido(Long pedidoId) {
        return notificacionRepository.buscarPorPedido(pedidoId);
    }
}
