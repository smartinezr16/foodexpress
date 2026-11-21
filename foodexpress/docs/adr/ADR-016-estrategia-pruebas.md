# ADR-016: Pruebas unitarias de dominio y casos de uso con implementaciones en memoria

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

Las reglas con más riesgo son el cálculo de totales, la máquina de estados, las reglas de cancelación y el ajuste de inventario. El entorno donde se generó el código no podía ejecutar Spring ni Docker, así que las pruebas debían correr sin infraestructura.

## Decisión

Probaremos dominio y casos de uso con JUnit 5 y fakes en memoria de los puertos, y el carrito del frontend con Vitest; la integración con Spring, PostgreSQL y RabbitMQ se verificará ejecutando el sistema con Docker Compose.

## Opciones evaluadas

- **A. Solo pruebas manuales.** Rápido al principio, pero no detecta regresiones.
- **B. Pruebas de integración con Testcontainers.** Prueban la infraestructura real, pero requieren Docker al ejecutar las pruebas.
- **C. Unitarias con fakes, más verificación con Compose (elegida).** Cubren la lógica de negocio y corren en segundos en cualquier equipo.

## Consecuencias

- (+) 66 pruebas de backend y 6 de frontend que corren en segundos.
- (+) Los fakes demuestran en la práctica que los casos de uso no dependen de la infraestructura.
- (−) No hay pruebas automáticas de controladores, mapeo JPA ni mensajería; un error en esa capa solo aparece al ejecutar.
- (−) Deuda técnica: agregar pruebas de integración con Testcontainers y un flujo de extremo a extremo.

## Relacionados

ADR-002
