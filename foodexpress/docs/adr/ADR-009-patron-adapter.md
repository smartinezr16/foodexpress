# ADR-009: Patrón Adapter para los canales de notificación

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

Notificaciones debe enviar por correo, WebSocket y push, tres tecnologías con API distintas. Los casos de uso no deberían cambiar si se agrega SMS o se cambia de proveedor de correo.

## Decisión

Definiremos la interfaz CanalNotificacion y un adaptador por tecnología (EmailCanalAdapter, WebSocketCanalAdapter, PushCanalAdapter), y un despachador que entrega cada mensaje por los canales indicados.

## Opciones evaluadas

- **A. Llamar a cada API desde los casos de uso.** Directo, pero los casos de uso quedarían atados a JavaMailSender y a STOMP.
- **B. Una clase con un método por canal.** Centraliza, pero agregar un canal modifica esa clase.
- **C. Adapter con interfaz común (elegida).** Cada tecnología queda envuelta; los casos de uso solo conocen la interfaz.

## Consecuencias

- (+) Agregar un canal es crear un adaptador; Spring lo registra solo.
- (+) Si un canal falla se registra el fallo y los demás siguen.
- (−) El push está simulado (escribe en el registro), porque no se integró un proveedor real.

## Relacionados

ADR-005
