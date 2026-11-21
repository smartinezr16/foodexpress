package com.foodexpress.notificaciones.application.port.out;

import com.foodexpress.notificaciones.domain.model.Canal;
import com.foodexpress.notificaciones.domain.model.Mensaje;

/**
 * PATRÓN ADAPTER: interfaz común ("target") para todos los medios de envío.
 *
 * El correo (JavaMailSender), el tiempo real (WebSocket/STOMP) y el push tienen
 * APIs distintas entre sí. Cada uno se envuelve en un adaptador que implementa
 * esta interfaz, y los casos de uso solo conocen esta abstracción.
 *
 * OCP: agregar un canal nuevo (SMS, WhatsApp) es crear otro adaptador; ni los
 * casos de uso ni los canales existentes se modifican.
 */
public interface CanalNotificacion {

    Canal canal();

    /** @throws com.foodexpress.notificaciones.domain.exception.EnvioFallidoException si no se pudo entregar */
    void enviar(Mensaje mensaje);
}
