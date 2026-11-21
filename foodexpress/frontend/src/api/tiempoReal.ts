import { Client } from '@stomp/stompjs';
import { useEffect, useRef, useState } from 'react';
import type { NotificacionEnVivo } from './tipos';

/**
 * Se suscribe a un tema STOMP del servicio de Notificaciones mientras el componente
 * esté montado. Devuelve si la conexión está activa.
 *
 *   /topic/pedidos/{pedidoId}           seguimiento de un pedido
 *   /topic/restaurantes/{restauranteId} pedidos nuevos y cancelaciones
 */
export function useTemaEnVivo(tema: string | null, alRecibir: (n: NotificacionEnVivo) => void): boolean {
  const [conectado, setConectado] = useState(false);
  // Se guarda la última versión del callback para no reabrir la conexión cada vez que cambia.
  const callback = useRef(alRecibir);
  callback.current = alRecibir;

  useEffect(() => {
    if (!tema) return;
    const protocolo = window.location.protocol === 'https:' ? 'wss' : 'ws';
    const cliente = new Client({
      brokerURL: `${protocolo}://${window.location.host}/ws`,
      reconnectDelay: 3000,
    });
    cliente.onConnect = () => {
      setConectado(true);
      cliente.subscribe(tema, (mensaje) => callback.current(JSON.parse(mensaje.body)));
    };
    cliente.onWebSocketClose = () => setConectado(false);
    cliente.activate();
    return () => {
      setConectado(false);
      void cliente.deactivate();
    };
  }, [tema]);

  return conectado;
}
