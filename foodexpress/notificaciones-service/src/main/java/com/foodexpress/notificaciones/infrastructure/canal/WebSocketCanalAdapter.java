package com.foodexpress.notificaciones.infrastructure.canal;

import com.foodexpress.notificaciones.application.port.out.CanalNotificacion;
import com.foodexpress.notificaciones.domain.exception.EnvioFallidoException;
import com.foodexpress.notificaciones.domain.model.Canal;
import com.foodexpress.notificaciones.domain.model.Mensaje;
import com.foodexpress.notificaciones.domain.model.TipoDestinatario;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * PATRÓN ADAPTER: adapta SimpMessagingTemplate (WebSocket/STOMP) a CanalNotificacion.
 *
 * Los clientes se suscriben a /topic/pedidos/{pedidoId} para seguir su pedido, y
 * los restaurantes a /topic/restaurantes/{restauranteId} para ver los pedidos nuevos.
 */
@Component
public class WebSocketCanalAdapter implements CanalNotificacion {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketCanalAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Canal canal() {
        return Canal.WEBSOCKET;
    }

    @Override
    public void enviar(Mensaje mensaje) {
        String destino = mensaje.destinatario().tipo() == TipoDestinatario.RESTAURANTE
                ? "/topic/restaurantes/" + mensaje.restauranteId()
                : "/topic/pedidos/" + mensaje.pedidoId();

        NotificacionTiempoReal contenido = new NotificacionTiempoReal(mensaje.pedidoId(), mensaje.tipoEvento(),
                mensaje.estadoPedido(), mensaje.asunto(), mensaje.cuerpo(), Instant.now().toString());
        try {
            messagingTemplate.convertAndSend(destino, contenido);
        } catch (MessagingException e) {
            throw new EnvioFallidoException("No se pudo publicar en " + destino, e);
        }
    }
}
