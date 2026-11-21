# ADR-005: RabbitMQ con exchange topic para los eventos de pedidos

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

Al crear, avanzar o cancelar un pedido hay que avisar al cliente y al restaurante, y ajustar el inventario. El catálogo pide los eventos pedido.creado, pedido.estado.cambiado y pedido.cancelado por RabbitMQ.

## Decisión

Pedidos publicará los tres eventos como JSON en un exchange topic durable foodexpress.pedidos, y cada servicio consumidor declarará sus propias colas durables con la clave de enrutamiento que le interesa.

## Opciones evaluadas

- **A. Patrón Observer en memoria.** Simple, pero solo funciona dentro de un proceso: no sirve entre microservicios y se pierde todo si el proceso cae.
- **B. Llamadas HTTP directas de Pedidos a Notificaciones.** Acopla en el tiempo: si Notificaciones cae, la creación del pedido falla o se demora.
- **C. Kafka.** Muy capaz para volúmenes grandes y reprocesamiento, pero más pesado de operar y el catálogo pide RabbitMQ.
- **D. RabbitMQ con exchange topic (elegida).** Enrutamiento por clave, colas durables, reintentos y colas de mensajes muertos con poca configuración.

## Consecuencias

- (+) Pedidos solo conoce el exchange; no sabe quién consume.
- (+) Agregar un consumidor es declarar una cola nueva en ese consumidor.
- (+) Si un consumidor está caído, los mensajes esperan en su cola.
- (−) El contrato del evento (campos del JSON) se vuelve una interfaz pública que hay que cuidar; los consumidores ignoran campos desconocidos para tolerar cambios.
- (−) Hay un broker más que operar y vigilar.

## Relacionados

ADR-001, ADR-012
