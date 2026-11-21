package com.foodexpress.notificaciones.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/** Evento de RabbitMQ que este servicio ya atendió (consumo idempotente). */
@Entity
@Table(name = "eventos_procesados")
public class EventoProcesadoEntity {

    @Id
    @Column(name = "event_id", length = 64)
    private String eventId;

    @Column(name = "procesado_en", nullable = false)
    private Instant procesadoEn;

    protected EventoProcesadoEntity() {
    }

    public EventoProcesadoEntity(String eventId, Instant procesadoEn) {
        this.eventId = eventId;
        this.procesadoEn = procesadoEn;
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getProcesadoEn() {
        return procesadoEn;
    }
}
