package com.foodexpress.notificaciones.application.usecase;

import com.foodexpress.notificaciones.application.port.out.CanalNotificacion;
import com.foodexpress.notificaciones.application.port.out.NotificacionRepository;
import com.foodexpress.notificaciones.domain.model.Canal;
import com.foodexpress.notificaciones.domain.model.Mensaje;
import com.foodexpress.notificaciones.domain.model.Notificacion;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Entrega un mensaje por los canales indicados y deja registro de cada intento.
 *
 * Trabaja contra la interfaz CanalNotificacion: no sabe si detrás hay un servidor
 * de correo, un WebSocket o un proveedor de push. Si un canal falla, se registra
 * el fallo y se continúa con los demás.
 */
public class DespachadorNotificaciones {

    private final Map<Canal, CanalNotificacion> canales = new EnumMap<>(Canal.class);
    private final NotificacionRepository notificacionRepository;

    public DespachadorNotificaciones(List<CanalNotificacion> canalesDisponibles,
                                     NotificacionRepository notificacionRepository) {
        for (CanalNotificacion canal : canalesDisponibles) {
            this.canales.put(canal.canal(), canal);
        }
        this.notificacionRepository = notificacionRepository;
    }

    public void despachar(String eventId, Mensaje mensaje, Set<Canal> canalesDestino) {
        for (Canal canalDestino : canalesDestino) {
            CanalNotificacion canal = canales.get(canalDestino);
            if (canal == null) {
                notificacionRepository.guardar(Notificacion.fallida(eventId, mensaje, canalDestino,
                        "El canal " + canalDestino + " no está configurado"));
                continue;
            }
            try {
                canal.enviar(mensaje);
                notificacionRepository.guardar(Notificacion.enviada(eventId, mensaje, canalDestino));
            } catch (RuntimeException e) {
                notificacionRepository.guardar(Notificacion.fallida(eventId, mensaje, canalDestino, e.getMessage()));
            }
        }
    }
}
