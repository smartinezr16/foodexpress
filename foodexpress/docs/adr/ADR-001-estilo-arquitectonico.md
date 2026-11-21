# ADR-001: Microservicios con comunicación dirigida por eventos

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

El catálogo del curso exige descomponer FoodExpress en tres microservicios (Restaurantes y Menú, Pedidos y Notificaciones) y manejar tres flujos asíncronos con RabbitMQ. Además prioriza la disponibilidad: los restaurantes deben poder recibir pedidos aunque Notificaciones falle. El equipo tiene tres integrantes y una sola entrega para construir y documentar.

## Decisión

Adoptaremos tres microservicios desplegables por separado, cada uno con su propia base de datos, que se comunican por eventos de RabbitMQ; la única llamada síncrona será la validación del menú al crear un pedido (ADR-006).

## Opciones evaluadas

- **A. Monolito en capas.** Despliegue y depuración simples, una sola base de datos. No cumple el requisito de tres microservicios ni aísla la caída de Notificaciones.
- **B. Monolito modular.** Módulos separados en un solo despliegue. Mejor organización, pero una falla del proceso tumba todo y tampoco cumple el requisito.
- **C. Microservicios con llamadas REST entre ellos.** Cumple la descomposición, pero Pedidos quedaría esperando a Notificaciones: si esta cae, crear pedidos falla.
- **D. Microservicios dirigidos por eventos (elegida).** Cumple el catálogo y desacopla en el tiempo: Pedidos publica y sigue; los consumidores procesan cuando pueden.

## Consecuencias

- (+) Una caída de Notificaciones no afecta la creación de pedidos.
- (+) Cada servicio evoluciona, se prueba y se despliega por separado.
- (+) Agregar un consumidor nuevo (por ejemplo, facturación) no exige tocar Pedidos.
- (−) Consistencia eventual: el inventario se ajusta un instante después del pedido (ver ADR-012).
- (−) Más piezas que operar: 3 servicios, 3 bases, un broker; se mitiga con Docker Compose (ADR-018).
- (−) Depurar un flujo exige seguirlo en varios registros y en la consola de RabbitMQ.

## Relacionados

ADR-002, ADR-005, ADR-006, ADR-018
