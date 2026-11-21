# ADR-006: Validar el menú de forma síncrona al crear el pedido

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

El atributo de consistencia exige que los platos no disponibles no puedan pedirse y que la validación ocurra en el momento de la orden. Si el cliente enviara precios, podría manipular el total.

## Decisión

Pedidos consultará por HTTP al servicio de Restaurantes los platos del pedido justo antes de crearlo, tomará de allí nombres, precios y extras, y rechazará platos agotados o de otro restaurante; el cliente nunca envía precios.

## Opciones evaluadas

- **A. Copia local del menú en Pedidos, actualizada por eventos.** Pedidos no dependería de Restaurantes al crear, pero la copia podría estar desactualizada justo en la orden.
- **B. Confiar en los precios que envía el frontend.** Sin dependencia, pero inseguro: el total quedaría en manos del cliente.
- **C. Consulta síncrona con tiempo de espera (elegida).** La validación es exacta en el momento de la orden.

## Consecuencias

- (+) Precio y disponibilidad siempre son los vigentes.
- (+) El frontend solo estima el total; el definitivo lo calcula el dominio.
- (−) Si Restaurantes no responde, no se pueden crear pedidos: se devuelve 503 con un mensaje claro tras 2 s de conexión y 3 s de lectura.
- (−) Dos pedidos casi simultáneos por la última unidad pueden aceptarse ambos, porque el descuento llega después por evento; el inventario nunca queda negativo.

## Relacionados

ADR-001, ADR-013
