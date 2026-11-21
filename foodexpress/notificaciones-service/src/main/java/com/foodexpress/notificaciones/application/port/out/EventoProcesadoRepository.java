package com.foodexpress.notificaciones.application.port.out;

/** Registro de eventos ya atendidos, para no notificar dos veces lo mismo. */
public interface EventoProcesadoRepository {

    /**
     * Anota el evento como procesado.
     *
     * @return true si es la primera vez que se ve este evento; false si ya se había procesado
     */
    boolean registrarSiEsNuevo(String eventId);
}
