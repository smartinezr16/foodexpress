package com.foodexpress.notificaciones.infrastructure.canal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Simula el SDK de un proveedor externo de notificaciones push (como Firebase
 * Cloud Messaging). Su interfaz es propia del proveedor y no coincide con la de
 * CanalNotificacion; por eso se usa a través de {@link PushCanalAdapter}.
 */
@Component
public class ProveedorPushSimulado {

    private static final Logger log = LoggerFactory.getLogger(ProveedorPushSimulado.class);

    /** Publica en un tema y devuelve el identificador que asigna el proveedor. */
    public String publicarEnTema(String tema, Map<String, String> carga) {
        String idMensaje = UUID.randomUUID().toString();
        log.info("[PUSH simulado] tema={} id={} carga={}", tema, idMensaje, carga);
        return idMensaje;
    }
}
