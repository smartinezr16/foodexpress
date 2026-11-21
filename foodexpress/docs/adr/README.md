# Architecture Decision Records

Cada archivo registra una decisión de arquitectura de FoodExpress: el contexto, las opciones evaluadas, la decisión y sus consecuencias.

Las decisiones se tomaron en dos momentos: la construcción del sistema (15 de noviembre de 2026) y la preparación de la entrega final con base en la guía del curso (19 de noviembre de 2026). ADR-004 muestra una decisión que se reemplazó al revisar la guía.

| ADR | Decisión | Fecha | Estado |
| --- | --- | --- | --- |
| [ADR-001](ADR-001-estilo-arquitectonico.md) | Microservicios con comunicación dirigida por eventos | 2026-11-15 | Aceptado |
| [ADR-002](ADR-002-clean-architecture.md) | Clean Architecture con puertos y adaptadores en cada servicio | 2026-11-15 | Aceptado |
| [ADR-003](ADR-003-framework-backend.md) | Java 17 y Spring Boot 3.5 como base de los servicios | 2026-11-15 | Aceptado |
| [ADR-004](ADR-004-postgresql-compartido.md) | PostgreSQL con una base por servicio en un solo contenedor | 2026-11-15 | Reemplazado por ADR-018 |
| [ADR-005](ADR-005-mensajeria-rabbitmq.md) | RabbitMQ con exchange topic para los eventos de pedidos | 2026-11-15 | Aceptado |
| [ADR-006](ADR-006-validacion-sincrona-menu.md) | Validar el menú de forma síncrona al crear el pedido | 2026-11-15 | Aceptado |
| [ADR-007](ADR-007-patron-state.md) | Patrón State para el ciclo de vida del pedido | 2026-11-15 | Aceptado |
| [ADR-008](ADR-008-patron-builder.md) | Patrón Builder para construir pedidos | 2026-11-15 | Aceptado |
| [ADR-009](ADR-009-patron-adapter.md) | Patrón Adapter para los canales de notificación | 2026-11-15 | Aceptado |
| [ADR-010](ADR-010-acceso-datos.md) | Spring Data JPA con entidades separadas del dominio | 2026-11-15 | Aceptado |
| [ADR-011](ADR-011-manejo-errores.md) | Excepciones de dominio traducidas a Problem Details en un manejador global | 2026-11-15 | Aceptado |
| [ADR-012](ADR-012-entrega-eventos.md) | Reintentos, cola de mensajes muertos y consumo idempotente | 2026-11-15 | Aceptado |
| [ADR-013](ADR-013-consistencia-datos.md) | BigDecimal en el dominio y control de concurrencia optimista | 2026-11-15 | Aceptado |
| [ADR-014](ADR-014-cache-menu.md) | Caché local del menú con Caffeine | 2026-11-15 | Aceptado |
| [ADR-015](ADR-015-esquema-base-datos.md) | Esquema generado por Hibernate en lugar de migraciones versionadas | 2026-11-15 | Aceptado |
| [ADR-016](ADR-016-estrategia-pruebas.md) | Pruebas unitarias de dominio y casos de uso con implementaciones en memoria | 2026-11-15 | Aceptado |
| [ADR-017](ADR-017-frontend.md) | Frontend React con Vite, servido por Nginx como proxy inverso | 2026-11-19 | Aceptado |
| [ADR-018](ADR-018-docker-compose.md) | Docker Compose con una base por servicio, redes segmentadas y credenciales en .env | 2026-11-19 | Aceptado |
| [ADR-019](ADR-019-autenticacion.md) | Sin autenticación ni autorización en esta entrega | 2026-11-15 | Aceptado |
| [ADR-020](ADR-020-estrategia-ramas.md) | GitFlow con Conventional Commits y revisión por pull request | 2026-11-19 | Aceptado |

## Cómo agregar un ADR

1. Copiar la estructura de cualquier ADR con el siguiente número libre: `ADR-NNN-titulo-corto.md`.
2. Usar el estado **Propuesto** mientras se discute en el pull request.
3. Al aprobarse, cambiarlo a **Aceptado**. Si reemplaza a otro, marcar el anterior como **Reemplazado por ADR-NNN**.
