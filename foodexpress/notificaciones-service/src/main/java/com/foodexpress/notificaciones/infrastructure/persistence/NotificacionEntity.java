package com.foodexpress.notificaciones.infrastructure.persistence;

import com.foodexpress.notificaciones.domain.model.Canal;
import com.foodexpress.notificaciones.domain.model.EstadoEnvio;
import com.foodexpress.notificaciones.domain.model.Notificacion;
import com.foodexpress.notificaciones.domain.model.TipoDestinatario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "notificaciones", indexes = @Index(name = "idx_notificaciones_pedido", columnList = "pedido_id"))
public class NotificacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, length = 64)
    private String eventId;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Canal canal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_destinatario", nullable = false, length = 20)
    private TipoDestinatario tipoDestinatario;

    @Column(nullable = false)
    private String destinatario;

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false, length = 1000)
    private String cuerpo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEnvio estado;

    @Column(name = "detalle_error", length = 500)
    private String detalleError;

    @Column(nullable = false)
    private Instant fecha;

    protected NotificacionEntity() {
    }

    static NotificacionEntity de(Notificacion notificacion) {
        NotificacionEntity entidad = new NotificacionEntity();
        entidad.eventId = notificacion.eventId();
        entidad.pedidoId = notificacion.pedidoId();
        entidad.canal = notificacion.canal();
        entidad.tipoDestinatario = notificacion.tipoDestinatario();
        entidad.destinatario = notificacion.destinatario();
        entidad.asunto = notificacion.asunto();
        entidad.cuerpo = recortar(notificacion.cuerpo(), 1000);
        entidad.estado = notificacion.estado();
        entidad.detalleError = recortar(notificacion.detalleError(), 500);
        entidad.fecha = notificacion.fecha();
        return entidad;
    }

    Notificacion aDominio() {
        return new Notificacion(id, eventId, pedidoId, canal, tipoDestinatario, destinatario, asunto, cuerpo,
                estado, detalleError, fecha);
    }

    private static String recortar(String texto, int maximo) {
        return (texto == null || texto.length() <= maximo) ? texto : texto.substring(0, maximo);
    }
}
