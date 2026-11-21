package com.foodexpress.notificaciones.application.usecase;

import com.foodexpress.notificaciones.application.dto.EventoPedido;
import com.foodexpress.notificaciones.application.port.out.CanalNotificacion;
import com.foodexpress.notificaciones.application.port.out.EventoProcesadoRepository;
import com.foodexpress.notificaciones.application.port.out.NotificacionRepository;
import com.foodexpress.notificaciones.domain.exception.EnvioFallidoException;
import com.foodexpress.notificaciones.domain.model.Canal;
import com.foodexpress.notificaciones.domain.model.EstadoEnvio;
import com.foodexpress.notificaciones.domain.model.Mensaje;
import com.foodexpress.notificaciones.domain.model.Notificacion;
import com.foodexpress.notificaciones.domain.model.TipoDestinatario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Canales y repositorios en memoria para probar los casos de uso sin correo, WebSocket ni base de datos. */
final class Fakes {

    private Fakes() {
    }

    /** Canal que guarda lo que se le pide enviar; puede configurarse para fallar. */
    static final class CanalEspia implements CanalNotificacion {

        private final Canal canal;
        final List<Mensaje> enviados = new ArrayList<>();
        boolean fallar;

        CanalEspia(Canal canal) {
            this.canal = canal;
        }

        @Override
        public Canal canal() {
            return canal;
        }

        @Override
        public void enviar(Mensaje mensaje) {
            if (fallar) {
                throw new EnvioFallidoException("Servidor de " + canal + " no disponible");
            }
            enviados.add(mensaje);
        }

        List<Mensaje> para(TipoDestinatario tipo) {
            return enviados.stream().filter(m -> m.destinatario().tipo() == tipo).toList();
        }
    }

    static final class NotificacionesEnMemoria implements NotificacionRepository {

        final List<Notificacion> guardadas = new ArrayList<>();

        @Override
        public Notificacion guardar(Notificacion notificacion) {
            guardadas.add(notificacion);
            return notificacion;
        }

        @Override
        public List<Notificacion> buscarPorPedido(Long pedidoId) {
            return guardadas.stream().filter(n -> n.pedidoId().equals(pedidoId)).toList();
        }

        long cantidad(Canal canal, EstadoEnvio estado) {
            return guardadas.stream().filter(n -> n.canal() == canal && n.estado() == estado).count();
        }
    }

    static final class EventosEnMemoria implements EventoProcesadoRepository {

        private final Set<String> procesados = new HashSet<>();

        @Override
        public boolean registrarSiEsNuevo(String eventId) {
            return procesados.add(eventId);
        }
    }

    static EventoPedido evento(String eventId, String tipo, String tipoPedido, String estadoAnterior,
                               String estadoNuevo, String canceladoPor, String motivo) {
        return new EventoPedido(eventId, tipo, 15L, 1L, "La Brasa Roja", "Ana", "ana@correo.com", tipoPedido,
                estadoAnterior, estadoNuevo, new BigDecimal("66960.00"),
                List.of(new EventoPedido.Item("Hamburguesa clásica", 2), new EventoPedido.Item("Limonada natural", 1)),
                canceladoPor, motivo);
    }
}
