# ADR-012: Reintentos, cola de mensajes muertos y consumo idempotente

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

RabbitMQ puede entregar un mensaje más de una vez, y un consumidor puede fallar temporalmente. Notificar dos veces o descontar dos veces el inventario sería un error visible.

## Decisión

Cada evento llevará un eventId; los consumidores lo registrarán en una tabla eventos_procesados en la misma transacción que su efecto, reintentarán 3 veces con espera creciente y, agotados los reintentos, enviarán el mensaje a una cola de mensajes muertos.

## Opciones evaluadas

- **A. Sin protección.** Simple, pero los duplicados y los mensajes envenenados bloquean o corrompen.
- **B. Patrón Transactional Outbox en el publicador.** Garantiza que no se pierda ningún evento, pero exige una tabla y un proceso de reenvío adicionales.
- **C. Idempotencia y cola de mensajes muertos en los consumidores (elegida).** Cubre duplicados y fallos del consumidor con poca configuración.

## Consecuencias

- (+) Un evento repetido no se aplica dos veces (lo cubren pruebas en Restaurantes y Notificaciones).
- (+) Un mensaje defectuoso no bloquea la cola: queda en restaurantes.dlq o notificaciones.dlq.
- (−) Deuda técnica: sin Outbox, si RabbitMQ no responde justo después de guardar el pedido, ese evento se pierde y solo queda en el registro.

## Relacionados

ADR-005
