# ADR-007: Patrón State para el ciclo de vida del pedido

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

Un pedido pasa por Recibido, En preparación, Listo y Entregado, o Cancelado, y quién puede cancelar depende del estado. Repartir esas reglas en condicionales dentro de los casos de uso las hace difíciles de leer y de extender.

## Decisión

Modelaremos cada estado como una clase que implementa EstadoPedido con sus propias transiciones y reglas de cancelación, y Pedido delegará en su estado actual.

## Opciones evaluadas

- **A. Enum con un switch en cada caso de uso.** Menos clases, pero las reglas quedan repartidas y agregar un estado obliga a tocar cada switch.
- **B. Librería de máquina de estados (Spring Statemachine).** Potente, pero excesiva para cinco estados y oculta las reglas en configuración.
- **C. Patrón State (elegida).** Cada regla vive en la clase de su estado; agregar uno es una clase nueva.

## Consecuencias

- (+) OCP: un estado nuevo, como En camino para domicilios, no modifica los existentes.
- (+) Una transición no permitida lanza TransicionInvalidaException y la API responde 409.
- (+) Las reglas se prueban directamente en el dominio (9 pruebas).
- (−) Cinco clases pequeñas en lugar de un enum; se persiste el tipo de estado y se reconstruye con EstadoPedidoTipo.

## Relacionados

ADR-008
