# ADR-013: BigDecimal en el dominio y control de concurrencia optimista

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

El catálogo exige que el cálculo de totales sea exacto. Además, dos operaciones pueden modificar el mismo pedido o plato a la vez (por ejemplo, el restaurante avanza el pedido mientras el cliente lo cancela).

## Decisión

Calcularemos los totales en el dominio con BigDecimal redondeado a 2 decimales (HALF_UP), y las entidades de pedido y plato llevarán @Version para rechazar escrituras concurrentes con 409.

## Opciones evaluadas

- **A. double y bloqueo pesimista.** double introduce errores de redondeo en dinero; el bloqueo pesimista frena la concurrencia.
- **B. Enteros en centavos y sin control de concurrencia.** Exacto, pero la última escritura gana y puede pisar un cambio de estado.
- **C. BigDecimal y control optimista (elegida).** Exactitud y detección de conflictos sin bloquear.

## Consecuencias

- (+) El impuesto del 8 % y el domicilio se calculan igual en todas partes; lo cubren pruebas con casos de redondeo.
- (+) Un conflicto se devuelve como 409 y el cliente puede reintentar.
- (−) Con mucha contención habría más reintentos; para este volumen no es un problema.

## Relacionados

ADR-006
