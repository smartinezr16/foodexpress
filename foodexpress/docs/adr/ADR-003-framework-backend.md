# ADR-003: Java 17 y Spring Boot 3.5 como base de los servicios

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

El catálogo usa términos de Java (JPA, casos de uso, repositorios) y el curso evalúa fragmentos de código Java. Necesitábamos soporte maduro para REST, JPA, AMQP, WebSocket, validación, caché y health checks sin integrar librerías sueltas.

## Decisión

Construiremos los tres servicios con Java 17 (imagen de ejecución Java 21) y Spring Boot 3.5, usando sus starters de web, validación, Data JPA, AMQP, WebSocket, mail, caché y actuator.

## Opciones evaluadas

- **A. Spring Boot (elegida).** Un starter para cada necesidad, ampliamente documentado y alineado con el curso.
- **B. Quarkus o Micronaut.** Arranque más rápido y menos memoria, pero menos material de apoyo y menos familiaridad del equipo.
- **C. Node.js con NestJS.** Bueno para APIs, pero el curso evalúa patrones y fragmentos en Java.

## Consecuencias

- (+) Toda la integración (RabbitMQ, WebSocket, caché, health checks) se resuelve con configuración y anotaciones.
- (+) Mismo lenguaje en los tres servicios: un integrante puede revisar el código de otro.
- (−) Cada servicio consume más memoria y tarda más en arrancar que con alternativas nativas; con 3 servicios en Compose se nota en equipos de 8 GB.
- (−) La versión 3.5.6 se fijó sin poder compilarla en el entorno de la IA; la confirmó el equipo al ejecutar el proyecto.

## Relacionados

ADR-002
