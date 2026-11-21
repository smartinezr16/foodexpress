# Bitácora de uso de inteligencia artificial

**Proyecto:** FoodExpress · Diseño y Arquitectura de Software
**Equipo:** [Integrante 1], [Integrante 2], [Integrante 3]

> Las secciones marcadas con **[Equipo: completar]** deben llenarlas los integrantes con su
> propia experiencia. El resto describe lo que se hizo con la herramienta, en orden.

## Herramienta utilizada

| Herramienta | Uso | Modelo |
|---|---|---|
| Claude (claude.ai, Anthropic), en modo agente con acceso a un espacio de trabajo con terminal | Análisis del catálogo, generación de código, pruebas, diagramas, documentación y presentación | Claude Fable 5.2 (15 de noviembre) y Claude Opus 5.5 (19 de noviembre) |

## Registro de sesiones

| # | Fecha | Objetivo (lo que se pidió) | Qué produjo la IA | Cómo se verificó | Decisión o ajuste humano |
|---|---|---|---|---|---|
| 1 | 2026-11-15 | Elegir proyecto entre los libres: Clínica veterinaria, GymTrack, FoodExpress y EventHub, adjuntando el catálogo en PDF. | Comparación de los cuatro proyectos por patrones, retos técnicos y dificultad; recomendó FoodExpress. | Se contrastó con el catálogo del curso. | **El equipo eligió FoodExpress.** |
| 2 | 2026-11-15 | Definir cómo construirlo. | Preguntas de alcance antes de empezar. | — | El equipo escogió Java + Spring Boot, código completo y dejar la bonificación para después. |
| 3 | 2026-11-15 | Construir el backend. | 3 microservicios con Clean Architecture; patrones Builder, State y Adapter; mensajería RabbitMQ con reintentos, colas de mensajes muertos e idempotencia; caché; Docker Compose; README; 66 pruebas unitarias. | La IA compiló y ejecutó las pruebas de dominio y casos de uso (66/66), verificó los contratos JSON entre servicios con Jackson y que las capas internas no importaran el framework. **No pudo ejecutar Spring ni Docker** en su entorno (sin acceso a Maven Central ni a Docker Hub) y lo advirtió. | El equipo ejecutó `docker compose up --build` en su máquina y **funcionó**. |
| 4 | 2026-11-19 | Construir el frontend. | Aplicación React + Vite con TypeScript: restaurantes, menú, carrito, seguimiento en vivo, Mis pedidos y panel de Cocina; 6 pruebas unitarias del carrito. | La IA la probó en un navegador automatizado contra un backend simulado: 9 comprobaciones del flujo completo, incluido WebSocket, sin errores de consola. | La IA corrigió dos defectos que vio en las capturas: notificaciones duplicadas y menú desbordado en celular. [Equipo: completar revisión] |
| 5 | 2026-11-19 | Dockerizar el frontend. | Dockerfile multietapa (Node → Nginx), Nginx como proxy inverso hacia los 3 servicios y el WebSocket; servicio en Docker Compose. | Se probó la configuración con Nginx real: encontró y corrigió una directiva duplicada que impedía arrancar. Se repitió el recorrido completo a través de Nginx. | [Equipo: confirmar `docker compose up --build` con el frontend] |
| 6 | 2026-11-19 | Diagrama C4. | Diagramas de contexto, contenedores y componentes (servicio de Pedidos) en SVG, con su modelo en Structurizr DSL. | Revisión visual de cada diagrama; se reubicaron flechas que se cruzaban. | [Equipo: completar revisión] |
| 7 | 2026-11-19 | Preparar la presentación. | Requisitos funcionales, presentación y esta bitácora. | — | [Equipo: completar revisión] |
| 8 | 2026-11-19 | Alinear el proyecto con la guía de entrega final, adjuntando la guía en PDF. | Comparación sección por sección; Docker Compose con `.env`, health checks, redes y una base por servicio; C4 alineado con Compose; 4 diagramas de clases UML; 20 ADRs; 10 RF y 14 reglas de negocio; lecciones aprendidas; GitFlow con plan de trabajo; documento final. | `docker compose config` validado con y sin `.env`; renders de C4 y UML revisados; pruebas repetidas (66 + 6). **No se pudo ejecutar `docker compose up` en el entorno de la IA** (sin acceso a Docker Hub). | La IA advirtió que no fabricaría el historial de Git ni las métricas de participación. [Equipo: ejecutar `docker compose up --build` con el Compose nuevo] |

## Qué hizo la IA y qué hizo el equipo

**La IA:** propuso la elección de proyecto, generó la mayor parte del código del backend y del frontend, las pruebas, la configuración de Docker, los diagramas C4 y la documentación; detectó y corrigió errores al probar en su entorno.

**El equipo:** eligió el proyecto, la tecnología y el alcance; ejecutó el sistema en su máquina; [Equipo: completar con lo que cada integrante revisó, modificó, probó o explicó].

## Limitaciones y errores detectados

- La IA no pudo ejecutar el backend real (Spring, PostgreSQL, RabbitMQ) en su entorno; esa verificación la hizo el equipo.
- El frontend se probó contra un backend simulado que imita las APIs; la integración real queda confirmada al ejecutar Docker Compose.
- Errores de la IA corregidos por ella misma durante las pruebas: directiva duplicada en Nginx, notificaciones repetidas en el seguimiento, navegación móvil desbordada y flechas cruzadas en los diagramas.
- El Docker Compose reescrito el 19 de noviembre (credenciales en `.env`, health checks, tres PostgreSQL) solo se validó en su sintaxis; debe probarse con `docker compose up --build`.
- La IA no generó commits, pull requests ni métricas de Git: la sección B.4 depende del trabajo real del equipo.
- [Equipo: agregar cualquier error encontrado al ejecutar o revisar.]

## Reflexión del equipo

[Equipo: completar. Por ejemplo: qué aprendieron al revisar el código generado, qué partes entienden y pueden defender en la sustentación, qué harían distinto y cómo validaron que la arquitectura cumple lo pedido.]
