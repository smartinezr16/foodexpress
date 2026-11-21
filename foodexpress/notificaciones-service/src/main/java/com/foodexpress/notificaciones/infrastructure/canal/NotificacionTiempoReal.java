package com.foodexpress.notificaciones.infrastructure.canal;

/** Lo que recibe el navegador por WebSocket (se envía como JSON). */
public record NotificacionTiempoReal(
        Long pedidoId,
        String tipoEvento,
        String estado,
        String titulo,
        String mensaje,
        String fecha) {
}
