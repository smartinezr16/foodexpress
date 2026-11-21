# ADR-010: Spring Data JPA con entidades separadas del dominio

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

Por ADR-002 el dominio no puede depender de JPA. A la vez queríamos el menor código posible para consultas sencillas.

## Decisión

Usaremos Spring Data JPA en los adaptadores de persistencia, con entidades JPA propias y mappers que convierten entre entidad y dominio.

## Opciones evaluadas

- **A. Anotar las clases de dominio con JPA.** Menos código, pero el dominio dependería del framework y de sus restricciones (constructores vacíos, setters).
- **B. JdbcTemplate con SQL a mano.** Control total, pero mucho código repetido para un CRUD con relaciones.
- **C. Spring Data JPA con mappers (elegida).** Consultas derivadas sin SQL y un dominio limpio.

## Consecuencias

- (+) Consultas como findByRestauranteIdOrderByFechaCreacionDesc sin escribir SQL.
- (+) default_batch_fetch_size evita el problema N+1 al cargar ítems y extras.
- (−) Hay que mantener dos modelos y sus mappers sincronizados.

## Relacionados

ADR-002
