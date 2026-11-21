# ADR-002: Clean Architecture con puertos y adaptadores en cada servicio

| Campo | Valor |
| --- | --- |
| Fecha | 2026-11-15 |
| Estado | Aceptado |

## Contexto

El catálogo pide evidenciar SOLID con ejemplos concretos, entre ellos que CrearPedidoUseCase dependa de interfaces y no de JPA (DIP). También queríamos probar las reglas de negocio sin levantar base de datos ni broker, porque el tiempo de pruebas manuales era limitado.

## Decisión

Organizaremos cada servicio en tres capas, domain, application e infrastructure, donde domain y application son Java puro y todo lo que depende de Spring, JPA, HTTP o RabbitMQ vive en adaptadores de infrastructure que implementan puertos definidos en application.

## Opciones evaluadas

- **A. Paquetes por capa técnica (controller / service / repository).** Conocido y rápido, pero los servicios terminan dependiendo de entidades JPA y de Spring, y la lógica queda mezclada con la infraestructura.
- **B. Paquetes por funcionalidad (feature).** Agrupa por caso de uso; no impide por sí solo que el dominio dependa del framework.
- **C. Clean Architecture / hexagonal (elegida).** Separa reglas de detalles: el dominio no conoce el framework y la infraestructura es reemplazable.

## Consecuencias

- (+) 66 pruebas unitarias corren sin base de datos, broker ni Spring, usando implementaciones en memoria de los puertos.
- (+) DIP e ISP quedan visibles en la estructura de paquetes, no solo en el discurso.
- (+) Cambiar JPA, el cliente HTTP o RabbitMQ no toca los casos de uso.
- (−) Más clases: entidades JPA separadas del dominio y mappers entre ambas (ADR-010).
- (−) Los casos de uso se ensamblan a mano en una clase UseCaseConfig por servicio.

## Relacionados

ADR-010, ADR-016
