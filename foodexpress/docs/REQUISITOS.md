# Requisitos de FoodExpress

Diez requerimientos funcionales (el máximo que pide la guía), todos implementados, y las reglas de negocio que los rigen. Al final, lo que quedó fuera del alcance.

## Actores

| Actor | Descripción |
|---|---|
| Cliente | Consulta menús, hace pedidos y sigue su estado. Se identifica con su correo. |
| Personal del restaurante | Administra el restaurante y su menú, y opera la cocina. |
| Sistema | Procesos automáticos disparados por eventos (inventario y notificaciones). |

## Requerimientos funcionales

| ID | Requerimiento | Actor | Criterio de aceptación | Reglas | Estado | Dónde se implementa |
|---|---|---|---|---|---|---|
| RF-01 | Registrar y operar restaurantes | Restaurante | Un restaurante nuevo queda cerrado; se puede abrir y cerrar; uno cerrado no recibe pedidos. | RN-01, RN-02 | Implementado | `POST /api/restaurantes`, `PATCH /api/restaurantes/{id}/estado` · Cocina |
| RF-02 | Gestionar el menú con categorías y extras | Restaurante | Cada plato tiene nombre, precio, categoría y extras opcionales. | RN-03, RN-04 | Implementado | `POST /api/restaurantes/{id}/platos`, `POST /api/platos/{id}/extras` |
| RF-03 | Controlar la disponibilidad de los platos | Restaurante | Se puede retirar un plato de la carta o fijar sus unidades; sin unidades aparece Agotado. | RN-05 | Implementado | `PATCH /api/platos/{id}/disponibilidad` · Cocina, pestaña Menú |
| RF-04 | Consultar el menú | Cliente | El menú se muestra por categorías y se puede filtrar; responde desde caché. | — | Implementado | `GET /api/restaurantes/{id}/menu` · pantalla del restaurante |
| RF-05 | Hacer un pedido con extras y notas | Cliente | El pedido es para recoger o a domicilio; se rechazan platos agotados o de otro restaurante; el total lo calcula el sistema. | RN-02, RN-06 a RN-10 | Implementado | `POST /api/pedidos` · carrito |
| RF-06 | Avanzar el estado del pedido | Restaurante | Recibido → En preparación → Listo → Entregado; un pedido final no avanza. | RN-11 | Implementado | `PATCH /api/pedidos/{id}/avanzar` · tablero de Cocina |
| RF-07 | Cancelar un pedido | Cliente, Restaurante | Se cancela solo si el estado lo permite al actor; se registra quién canceló y el motivo. | RN-12 | Implementado | `POST /api/pedidos/{id}/cancelar` · Seguimiento y Cocina |
| RF-08 | Notificar los cambios del pedido | Sistema | El restaurante ve en vivo los pedidos nuevos y las cancelaciones; el cliente recibe confirmación, cada cambio de estado y el aviso de pedido listo. | RN-13 | Implementado | Eventos de RabbitMQ → correo, WebSocket y push |
| RF-09 | Ajustar el inventario con los eventos | Sistema | Un pedido creado descuenta unidades y uno cancelado las devuelve; un evento repetido no se aplica dos veces. | RN-05, RN-14 | Implementado | Consumidores de Restaurantes, `AjustarInventarioUseCase` |
| RF-10 | Consultar pedidos e historial de avisos | Cliente, Restaurante | El cliente ve sus pedidos por correo; el restaurante ve los suyos; cada pedido muestra sus notificaciones y el canal. | — | Implementado | `GET /api/pedidos?clienteEmail=`, `?restauranteId=`, `GET /api/notificaciones?pedidoId=` · Mis pedidos |

## Reglas de negocio

| ID | Regla | Dónde se hace cumplir |
|---|---|---|
| RN-01 | Un restaurante nuevo empieza cerrado; se abre cuando su menú está listo. | `Restaurante.registrar` |
| RN-02 | Solo un restaurante abierto recibe pedidos. | `CrearPedidoUseCase` |
| RN-03 | El precio de un plato es mayor que cero y su categoría es entrada, plato fuerte, bebida o postre. | `Plato.actualizarDatos` |
| RN-04 | Un plato no puede tener dos extras con el mismo nombre. | `Plato.agregarExtra` |
| RN-05 | Un plato está disponible si está habilitado y le quedan unidades (o no lleva control de unidades); las unidades nunca son negativas. | `Plato.estaDisponible`, `Plato.descontarUnidades` |
| RN-06 | Los precios del pedido se toman del menú vigente en el momento de la orden; el cliente nunca los envía. | `CrearPedidoUseCase` |
| RN-07 | Cada plato y extra del pedido debe pertenecer al restaurante del pedido. | `CrearPedidoUseCase` |
| RN-08 | Un pedido tiene al menos un plato y cada ítem al menos una unidad. | `PedidoBuilder`, `ItemPedido.Builder` |
| RN-09 | Un pedido a domicilio exige dirección de entrega y suma un costo fijo de $5.000. | `PedidoBuilder`, `PedidoDelivery` |
| RN-10 | Total = platos + extras + 8 % de impuesto sobre platos y extras + domicilio (no gravado), redondeado a 2 decimales. | `Pedido.total` |
| RN-11 | El pedido sigue el orden Recibido, En preparación, Listo, Entregado; Entregado y Cancelado son finales. | Clases de `domain/model/estado` |
| RN-12 | El cliente puede cancelar solo en Recibido; el restaurante en Recibido o En preparación; nadie cuando está Listo. | Clases de `domain/model/estado` |
| RN-13 | Si cancela el cliente se avisa al restaurante; si cancela el restaurante se avisa al cliente. | `NotificarPedidoCanceladoUseCase` |
| RN-14 | Un mismo evento se procesa una sola vez aunque RabbitMQ lo entregue de nuevo. | `EventoProcesadoRepository` en cada consumidor |

## Fuera del alcance

| Elemento | Origen | Motivo |
|---|---|---|
| Emitir facturas | Descripción del problema en el catálogo | Se entrega el desglose de totales del pedido; la factura formal sería un consumidor más de `pedido.creado`. |
| Reordenar un pedido anterior | Bonificación | Existe el historial (RF-10), falta el botón de reordenar. |
| Reportes de ventas | Bonificación | No se implementó. |
| Autenticación y roles | No exigido | Ver ADR-019. |

## Requisitos no funcionales (atributos de calidad)

| Prioridad | Atributo | Requisito | Cómo se cumple |
|---|---|---|---|
| 1 | Consistencia | Los totales son exactos y los platos no disponibles no se pueden pedir. | Validación síncrona del menú (ADR-006), `BigDecimal` en el dominio y `@Version` (ADR-013), consumo idempotente (ADR-012). |
| 2 | Disponibilidad | Los pedidos se reciben aunque Notificaciones falle. | Eventos en colas durables, reintentos y cola de mensajes muertos (ADR-005, ADR-012). |
| 3 | Rendimiento | El menú responde rápido en horas pico. | Caché Caffeine del menú, invalidada al cambiar (ADR-014). |
| 4 | Mantenibilidad | Cambiar base de datos, broker o canal no toca la lógica de negocio. | Clean Architecture con puertos y adaptadores (ADR-002). |
| 5 | Portabilidad | Todo el sistema arranca con un comando. | Docker Compose con health checks (ADR-018). |
