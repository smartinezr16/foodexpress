# ADR-004: PostgreSQL con una base por servicio en un solo contenedor

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Reemplazado por ADR-018 |

## Contexto

Cada microservicio debe ser dueño de sus datos (patrón database per service). En la primera versión queríamos además que el entorno local consumiera pocos recursos.

## Decisión

Usaremos PostgreSQL 16 con una base de datos por servicio (restaurantes_db, pedidos_db, notificaciones_db) creadas por un script init.sql dentro de un único contenedor.

## Opciones evaluadas

- **A. PostgreSQL (elegida).** Relacional, transacciones, tipos numéricos exactos para dinero y soporte directo en Spring Data JPA.
- **B. MySQL.** Equivalente para este caso; se prefirió PostgreSQL por su tipo NUMERIC exacto y su buen soporte en Hibernate.
- **C. MongoDB.** Esquema flexible, pero pedidos, ítems y extras son relaciones naturales y el cálculo de totales pide transacciones.

## Consecuencias

- (+) Un solo contenedor de base de datos para levantar.
- (−) Las tres bases compartían usuario y red: cualquier servicio podía conectarse a la base de otro, así que la separación era solo de convención.
- (−) Al revisar la guía de entrega final, el diagrama C4 de contenedores (tres bases) no coincidía con el Compose (un contenedor). Por eso se reemplazó en ADR-018.

## Relacionados

ADR-018
