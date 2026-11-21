# ADR-011: Excepciones de dominio traducidas a Problem Details en un manejador global

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

Las reglas de negocio fallan de formas distintas (plato agotado, transición no permitida, restaurante cerrado) y el frontend necesita mostrarle al usuario un mensaje claro y un código HTTP coherente.

## Decisión

Las reglas lanzarán excepciones propias del dominio y de la aplicación, y un @RestControllerAdvice por servicio las traducirá a respuestas Problem Details (RFC 9457) con el mensaje en el campo detail.

## Opciones evaluadas

- **A. Devolver códigos de error en el cuerpo con HTTP 200.** Rompe la semántica HTTP y obliga a revisar cada respuesta.
- **B. Tipos Result en lugar de excepciones.** Explícito, pero poco idiomático en Spring y más verboso en cada capa.
- **C. Excepciones de dominio y manejador global (elegida).** Los casos de uso quedan limpios y el formato de error es uno solo.

## Consecuencias

- (+) 400 datos inválidos, 404 no existe, 409 transición o conflicto, 422 no disponible, 503 menú no disponible.
- (+) El frontend muestra directamente el campo detail.
- (−) Hay que mantener la tabla de excepción a código en cada servicio.

## Relacionados

ADR-007
