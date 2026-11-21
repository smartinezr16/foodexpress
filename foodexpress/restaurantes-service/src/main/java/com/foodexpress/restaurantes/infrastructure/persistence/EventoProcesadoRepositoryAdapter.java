package com.foodexpress.restaurantes.infrastructure.persistence;

import com.foodexpress.restaurantes.application.port.out.EventoProcesadoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
public class EventoProcesadoRepositoryAdapter implements EventoProcesadoRepository {

    private final EventoProcesadoJpaRepository jpaRepository;

    public EventoProcesadoRepositoryAdapter(EventoProcesadoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Si dos entregas del mismo evento llegaran a la vez, la clave primaria rechaza la
     * segunda inserción: su transacción se revierte y, al reintentar, ya lo encuentra registrado.
     */
    @Override
    @Transactional
    public boolean registrarSiEsNuevo(String eventId) {
        if (jpaRepository.existsById(eventId)) {
            return false;
        }
        jpaRepository.saveAndFlush(new EventoProcesadoEntity(eventId, Instant.now()));
        return true;
    }
}
