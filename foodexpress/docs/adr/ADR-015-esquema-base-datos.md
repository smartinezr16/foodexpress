# ADR-015: Esquema generado por Hibernate en lugar de migraciones versionadas

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

Las entidades cambiaron varias veces durante la construcción y el equipo no tenía experiencia previa con herramientas de migración. La prioridad era que docker compose up levantara el sistema sin pasos manuales.

## Decisión

Dejaremos que Hibernate cree y actualice las tablas con ddl-auto: update en esta entrega, y registraremos la migración a Flyway como deuda técnica.

## Opciones evaluadas

- **A. Flyway.** Migraciones versionadas y repetibles; requiere escribir y mantener cada script SQL.
- **B. Liquibase.** Similar a Flyway, con formatos XML o YAML; más curva de aprendizaje.
- **C. ddl-auto: update (elegida para esta entrega).** Cero scripts; adecuado para un proyecto académico.

## Consecuencias

- (+) El esquema siempre coincide con las entidades sin trabajo adicional.
- (−) No hay historial del esquema ni forma segura de cambiar o borrar columnas en producción.
- (−) Deuda técnica: pasar a Flyway antes de cualquier despliegue real.

## Relacionados

ADR-010
