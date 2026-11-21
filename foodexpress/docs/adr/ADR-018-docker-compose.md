# ADR-018: Docker Compose con una base por servicio, redes segmentadas y credenciales en .env

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-19 |
| Estado | Aceptado |

## Contexto

Al recibir la guía de entrega final, el Compose existente incumplía cuatro criterios: credenciales escritas en el archivo, servicios sin health check, uso de la red por defecto y un solo contenedor de PostgreSQL que no coincidía con el diagrama C4. La guía advierte que el Compose se ejecutará en la sustentación.

## Decisión

Levantaremos tres contenedores PostgreSQL (uno por servicio) con volúmenes propios, siete redes con nombre que solo conectan lo necesario, credenciales leídas de un archivo .env no versionado (con .env.example documentado), health checks en todos los servicios y depends_on con condition: service_healthy.

## Opciones evaluadas

- **A. Mantener el Compose anterior.** Funcionaba, pero incumplía criterios que la guía declara no aceptables.
- **B. Instalar PostgreSQL y RabbitMQ directamente en cada equipo.** Sin Docker, pero cada integrante tendría versiones y configuraciones distintas.
- **C. Compose endurecido (elegida).** Reproducible con un comando y alineado con el diagrama C4.

## Consecuencias

- (+) Database per service se cumple también en la red: Pedidos no puede alcanzar la base de Restaurantes.
- (+) Pedidos y el frontend solo arrancan cuando sus dependencias responden en /actuator/health.
- (+) Si falta una variable, Compose se detiene antes de arrancar y dice cuál.
- (−) Hay que copiar .env.example a .env antes del primer arranque.
- (−) Tres instancias de PostgreSQL consumen más memoria que una.
- (−) Las imágenes de ejecución instalan curl para el health check, unos pocos MB más.

## Relacionados

Reemplaza a ADR-004. Relacionado con ADR-001
