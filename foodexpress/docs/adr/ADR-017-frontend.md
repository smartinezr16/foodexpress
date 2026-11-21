# ADR-017: Frontend React con Vite, servido por Nginx como proxy inverso

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-19 |
| Estado | Aceptado |

## Contexto

La presentación final exige mostrar un frontend y la tecnología con que se hizo. Debía consumir tres API y un WebSocket sin obligar a configurar CORS en cada microservicio.

## Decisión

Construiremos una aplicación de una sola página con React 19, Vite y TypeScript, servida en Docker por Nginx 1.27, que además enruta /api y /ws a cada servicio para que el navegador use un solo origen.

## Opciones evaluadas

- **A. Angular.** Más estructura de fábrica, pero más pesado y lento de montar para cinco pantallas.
- **B. HTML y JavaScript sin framework.** Sin dependencias, pero con más código para el estado y las rutas.
- **C. React con Vite y TypeScript (elegida).** Rápido de construir, tipos para los contratos de las API y amplio soporte.

## Consecuencias

- (+) Sin CORS: Nginx en Docker y Vite en desarrollo reenvían cada ruta al servicio que corresponde.
- (+) Los tipos de TypeScript documentan los contratos de las tres API.
- (−) Nginx es un punto de entrada único; si cae, la interfaz no responde aunque las API sí.
- (−) Sin autenticación (ADR-019), la pantalla de cocina es accesible para cualquiera.

## Relacionados

ADR-019
