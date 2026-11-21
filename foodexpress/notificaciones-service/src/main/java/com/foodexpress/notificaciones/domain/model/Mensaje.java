package com.foodexpress.notificaciones.domain.model;

/**
 * Notificación lista para enviarse, independiente del canal. Cada canal decide
 * cómo presentarla (correo, mensaje en tiempo real, push).
 */
public record Mensaje(
        Long pedidoId,
        Long restauranteId,
        Destinatario destinatario,
        String tipoEvento,
        String estadoPedido,
        String asunto,
        String cuerpo) {
}
