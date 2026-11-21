# ADR-008: Patrón Builder para construir pedidos

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

Un pedido tiene muchos datos y varios son opcionales (extras, notas, dirección de domicilio), y además puede ser para recoger o a domicilio. Un constructor con diez parámetros es propenso a errores y permite objetos a medio construir.

## Decisión

Construiremos los pedidos e ítems con PedidoBuilder e ItemPedido.Builder, que validan todo en construir() y deciden si crear PedidoLocal o PedidoDelivery.

## Opciones evaluadas

- **A. Constructor con todos los parámetros.** Simple, pero ilegible y no impide combinaciones inválidas.
- **B. Setters sobre un objeto mutable.** Flexible, pero el pedido podría existir sin cliente o sin platos.
- **C. Builder (elegida).** Fluido, legible y con un único punto de validación.

## Consecuencias

- (+) Nunca existe un pedido inválido: falta de cliente, platos o dirección lanza PedidoInvalidoException.
- (+) El mismo builder se usa al crear y al reconstruir desde la base de datos.
- (−) Más código que un constructor; los campos del builder son de paquete para que Pedido los lea.

## Relacionados

ADR-007
