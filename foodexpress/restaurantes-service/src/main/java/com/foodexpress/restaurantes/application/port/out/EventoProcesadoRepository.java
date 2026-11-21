package com.foodexpress.restaurantes.application.port.out;

/** Registro de eventos ya atendidos, para que el consumo sea idempotente. */
public interface EventoProcesadoRepository {

    /**
     * Anota el evento como procesado.
     *
     * @return true si es la primera vez que se ve este evento; false si ya se había procesado
     */
    boolean registrarSiEsNuevo(String eventId);
}
