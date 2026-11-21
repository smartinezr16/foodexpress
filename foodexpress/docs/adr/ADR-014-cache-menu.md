# ADR-014: Caché local del menú con Caffeine

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

El atributo de rendimiento pide que la consulta de menús responda rápido en horas pico, con caché justificado. El menú se lee muchas más veces de las que cambia.

## Decisión

Guardaremos en una caché Caffeine local el menú completo de cada restaurante (hasta 500 menús, 60 s de vida) y la invalidaremos cuando cambien los platos o el inventario; la consulta puntual que usa Pedidos nunca pasa por la caché.

## Opciones evaluadas

- **A. Sin caché.** Siempre fresco, pero cada visita al menú consulta la base de datos.
- **B. Redis compartido.** Sirve con varias instancias, pero agrega otro contenedor y un punto de falla.
- **C. Caffeine local con invalidación (elegida).** Sin infraestructura adicional y suficiente para una instancia.

## Consecuencias

- (+) La caché se agregó envolviendo el caso de uso (MenuQueryServiceCacheado), sin modificarlo: ejemplo de OCP.
- (+) La validación del pedido siempre ve la disponibilidad real.
- (−) Con varias instancias de Restaurantes cada una tendría su copia; el límite de 60 s acota el dato desactualizado. Habría que pasar a Redis.

## Relacionados

ADR-006
