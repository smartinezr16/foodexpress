# Errores, dificultades y lecciones aprendidas

> Las secciones marcadas **[Equipo: completar]** deben llenarlas los integrantes con lo que vivieron. Lo demás registra lo que ocurrió durante la construcción, con fechas.

## Errores técnicos encontrados

### E-01 · Nginx no arrancaba con la configuración del WebSocket

| Campo | Descripción |
|---|---|
| Error | Nginx rechazaba la configuración con el error "directive is duplicate" y el contenedor del frontend no arrancaba. |
| Contexto | Proxy inverso del frontend (`frontend/nginx/default.conf.template`), ruta `/ws`. 19 de noviembre. |
| Causa raíz | La ruta `/ws` incluía el archivo de cabeceras comunes (que ya define `proxy_read_timeout 30s`) y además declaraba `proxy_read_timeout 1h`. Nginx no permite la misma directiva dos veces en un bloque. |
| Solución aplicada | La ruta `/ws` dejó de incluir el archivo común y declara sus propias cabeceras, con el tiempo de espera largo que necesita el WebSocket. Se probó con Nginx real y un recorrido completo del frontend a través del proxy. |
| Lección aprendida | Probar la configuración real (`nginx -t`) antes de darla por buena; un archivo compartido no sirve para bloques que necesitan valores distintos. |

### E-02 · La misma notificación aparecía repetida en el seguimiento del pedido

| Campo | Descripción |
|---|---|
| Error | En la pantalla de seguimiento, cada aviso aparecía dos o tres veces. |
| Contexto | Frontend, `PedidoPage`. 19 de noviembre. |
| Causa raíz | El backend guarda un registro por cada canal (correo, WebSocket, push) y la pantalla además agregaba el mensaje recibido en vivo. Un mismo aviso llegaba por tres caminos. |
| Solución aplicada | Agrupar los registros por asunto y cuerpo, mostrar los canales por los que salió y agregar el aviso en vivo solo si aún no está en el historial. |
| Lección aprendida | Revisar las capturas de pantalla con datos reales, no solo que la prueba pase; el modelo del backend (un registro por canal) no es el que el usuario quiere ver. |

### E-03 · La barra de navegación se salía de la pantalla en el celular

| Campo | Descripción |
|---|---|
| Error | En pantallas de 390 px el menú superior quedaba cortado. |
| Contexto | Frontend, estilos de la barra. 19 de noviembre. |
| Causa raíz | Marca y enlaces estaban en una sola fila sin permitir salto de línea. |
| Solución aplicada | En pantallas angostas la navegación pasa a una segunda fila con desplazamiento horizontal. |
| Lección aprendida | Incluir una captura en tamaño de celular en cada revisión del frontend. |

### E-04 · El Docker Compose incumplía la guía de entrega

| Campo | Descripción |
|---|---|
| Error | El Compose tenía las credenciales escritas en el archivo, no tenía health checks en los microservicios, Mailpit ni el frontend, y usaba la red por defecto. |
| Contexto | `docker-compose.yml`. Detectado el 19 de noviembre al leer la guía de entrega final. |
| Causa raíz | El sistema se construyó antes de conocer la guía. Funcionaba, pero se diseñó para "que arranque", no para los criterios de calidad que se evaluarían. |
| Solución aplicada | Credenciales en `.env` (con `.env.example`), health checks con Spring Boot Actuator, `depends_on` con `condition: service_healthy`, siete redes con nombre y un PostgreSQL por servicio (ADR-018). |
| Lección aprendida | Leer la guía y la rúbrica completas antes de escribir código. |

### E-05 · El diagrama C4 de contenedores no coincidía con el Compose

| Campo | Descripción |
|---|---|
| Error | El diagrama mostraba tres bases de datos, pero el Compose tenía un solo contenedor PostgreSQL con tres bases. El nivel 1 además usaba siglas técnicas (SMTP, HTTPS). |
| Contexto | `docs/c4`. Detectado el 19 de noviembre con el criterio "cada contenedor trazable a un servicio del docker-compose.yml". |
| Causa raíz | Los diagramas se dibujaron desde la intención (una base por servicio) y no desde lo que realmente se desplegaba. |
| Solución aplicada | Se pasó a tres contenedores PostgreSQL (ADR-018, que reemplaza ADR-004), cada caja del nivel 2 muestra el nombre de su servicio de Compose y el nivel 1 quedó sin siglas. |
| Lección aprendida | Generar o revisar el diagrama a partir del `docker-compose.yml`, no de memoria. |

### E-06 · Riesgo de romper a los consumidores al cambiar un evento

| Campo | Descripción |
|---|---|
| Error | Si Pedidos agregaba un campo al evento, Restaurantes y Notificaciones podían fallar al leerlo, porque cada uno declara solo los campos que usa. |
| Contexto | Contratos JSON entre servicios por RabbitMQ. 15 de noviembre. |
| Causa raíz | Por defecto, Jackson falla ante propiedades desconocidas cuando no se usa la configuración de Spring. |
| Solución aplicada | `@JsonIgnoreProperties(ignoreUnknown = true)` en los mensajes que leen los consumidores, y una prueba de contrato que serializa el evento real de Pedidos y lo lee con los modelos de los dos consumidores, incluso con un lector estricto. |
| Lección aprendida | Tratar el formato de un evento como una API pública y probarlo de extremo a extremo. |

### [Equipo: completar] · Errores que aparecieron al ejecutar el sistema en sus equipos

| Campo | Descripción |
|---|---|
| Error | |
| Contexto | |
| Causa raíz | |
| Solución aplicada | |
| Lección aprendida | |

## Dificultades del proceso

| Dificultad | Qué pasó | Cómo se resolvió |
|---|---|---|
| Elegir el proyecto | Había cuatro proyectos libres (Clínica veterinaria, GymTrack, FoodExpress, EventHub). | Se compararon por patrones, retos técnicos y dificultad; FoodExpress tenía flujos de eventos sin procesos programados ni control de aforo. |
| La guía llegó después de construir | El código, el Docker y la presentación se hicieron antes de conocer la guía; no hubo Entrega 1. | Se revisó cada sección de la guía contra lo construido y se corrigieron Docker, C4 y requisitos (E-04, E-05). |
| Verificación sin infraestructura | El entorno de la IA no podía descargar Spring ni ejecutar Docker. | Se probaron dominio y casos de uso con pruebas que no usan infraestructura, los contratos JSON con Jackson real y el frontend contra un backend simulado; la ejecución real la hizo el equipo. |
| Historial de Git concentrado | La primera versión se construyó en una sola sesión, antes de crear el repositorio. | El repositorio arranca con una importación inicial y el trabajo posterior se reparte en ramas por integrante (docs/GITFLOW.md). |
| [Equipo: completar] | | |

## Deuda técnica identificada

| Deuda | Riesgo | Cómo se abordaría |
|---|---|---|
| Sin patrón Outbox al publicar eventos | Si RabbitMQ no responde justo después de guardar, el evento se pierde (ADR-012). | Tabla outbox en la misma transacción y un proceso que reenvía lo pendiente. |
| Esquema con `ddl-auto: update` | No hay historial del esquema ni cambios seguros en producción (ADR-015). | Migraciones con Flyway. |
| Sin pruebas de integración | Un error en controladores, JPA o mensajería solo aparece al ejecutar (ADR-016). | Testcontainers con PostgreSQL y RabbitMQ, más una prueba de extremo a extremo. |
| Sin autenticación | Cualquiera puede cambiar el menú o el estado de un pedido (ADR-019). | JWT con roles CLIENTE y RESTAURANTE. |
| Caché local | Con varias instancias, cada una tendría su copia (ADR-014). | Redis compartido. |
| Inventario con consistencia eventual | Dos pedidos simultáneos pueden tomar la última unidad (ADR-006). | Reserva de unidades síncrona o un evento de compensación. |

## Sugerencias de mejora

1. **Técnica:** publicar eventos con el patrón Outbox y migrar el esquema a Flyway antes de cualquier despliegue real.
2. **Técnica:** agregar pruebas de integración con Testcontainers y un pipeline de integración continua que las corra en cada pull request.
3. **Proceso:** leer la guía y la rúbrica al empezar, crear el repositorio el primer día y repartir el trabajo por ramas desde el inicio.
4. **Proceso:** revisar cada pull request con capturas del frontend en escritorio y en celular.
5. **Trabajo futuro:** facturas como consumidor de `pedido.creado`, botón de reordenar, reportes de ventas y el estado En camino para domicilios (un estado nuevo, gracias al patrón State).
