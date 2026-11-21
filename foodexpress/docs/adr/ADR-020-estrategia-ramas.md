# ADR-020: GitFlow con Conventional Commits y revisión por pull request

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-19 |
| Estado | Aceptado |

## Contexto

La guía exige documentar una estrategia de ramas con al menos main, develop y ramas de funcionalidad, y evidenciar revisión entre pares. El equipo tiene tres integrantes y una fecha de entrega fija.

## Decisión

Usaremos GitFlow: main solo recibe versiones, develop integra, cada cambio va en una rama feature/, fix/ o docs/ desde develop y entra por pull request aprobado por otro integrante; los commits siguen Conventional Commits.

## Opciones evaluadas

- **A. Trunk-based development.** Integración continua sobre main con ramas muy cortas; exige integración continua y pruebas automáticas maduras.
- **B. GitHub Flow.** Una sola rama principal más ramas de funcionalidad; simple, pero sin rama de integración separada de las versiones.
- **C. GitFlow (elegida).** Separa lo integrado (develop) de lo entregado (main) y la guía lo pide explícitamente.

## Consecuencias

- (+) main siempre contiene una versión que levanta con docker compose up.
- (+) Cada cambio queda revisado por otra persona antes de integrarse.
- (−) Más ramas y más pasos de merge que GitHub Flow para un equipo de tres.

## Relacionados

Ver docs/GITFLOW.md
