package com.foodexpress.notificaciones.domain.model;

import java.time.Instant;

/** Registro de un intento de notificación por un canal, haya salido bien o no. */
public record Notificacion(
        Long id,
        String eventId,
        Long pedidoId,
        Canal canal,
        TipoDestinatario tipoDestinatario,
        String destinatario,
        String asunto,
        String cuerpo,
        EstadoEnvio estado,
        String detalleError,
        Instant fecha) {

    public static Notificacion enviada(String eventId, Mensaje mensaje, Canal canal) {
        return de(eventId, mensaje, canal, EstadoEnvio.ENVIADA, null);
    }

    public static Notificacion fallida(String eventId, Mensaje mensaje, Canal canal, String detalleError) {
        return de(eventId, mensaje, canal, EstadoEnvio.FALLIDA, detalleError);
    }

    private static Notificacion de(String eventId, Mensaje mensaje, Canal canal, EstadoEnvio estado, String error) {
        Destinatario destinatario = mensaje.destinatario();
        String referencia = destinatario.tieneEmail() ? destinatario.email() : destinatario.nombre();
        if (referencia == null || referencia.isBlank()) {
            referencia = "(sin identificar)";
        }
        return new Notificacion(null, eventId, mensaje.pedidoId(), canal, destinatario.tipo(), referencia,
                mensaje.asunto(), mensaje.cuerpo(), estado, error, Instant.now());
    }
}
