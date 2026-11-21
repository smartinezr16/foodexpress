package com.foodexpress.notificaciones.application.port.out;

import com.foodexpress.notificaciones.domain.model.Notificacion;

import java.util.List;

public interface NotificacionRepository {

    Notificacion guardar(Notificacion notificacion);

    List<Notificacion> buscarPorPedido(Long pedidoId);
}
