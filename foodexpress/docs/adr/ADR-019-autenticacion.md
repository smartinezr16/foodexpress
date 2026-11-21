# ADR-019: Sin autenticación ni autorización en esta entrega

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

El catálogo no exige autenticación para FoodExpress y el alcance obligatorio (tres servicios, eventos, patrones) ya llenaba el tiempo disponible.

## Decisión

No implementaremos autenticación en esta entrega: el cliente se identifica con su correo y cualquier persona puede usar los endpoints de administración y de cocina.

## Opciones evaluadas

- **A. JWT emitido por un servicio de identidad.** Estándar para microservicios, pero exige un cuarto servicio y validar el token en cada uno.
- **B. Sesiones con cookie en el frontend.** Simple para una aplicación web, pero no encaja con API independientes.
- **C. OAuth2 con un proveedor externo (Keycloak).** Robusto, pero agrega otro contenedor y bastante configuración.
- **D. Sin autenticación por ahora (elegida).** Permite cumplir el alcance obligatorio a tiempo.

## Consecuencias

- (+) Menos piezas y pruebas manuales más rápidas.
- (−) Riesgo conocido: cualquiera puede cambiar el menú o el estado de un pedido.
- (−) Trabajo futuro: JWT con roles CLIENTE y RESTAURANTE validado en un gateway o en cada servicio.

## Relacionados

ADR-017
