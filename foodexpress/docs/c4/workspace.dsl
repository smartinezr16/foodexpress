/*
 * FoodExpress: modelo C4 como código (Structurizr DSL).
 *
 * Para verlo de forma interactiva:
 *   docker run -it --rm -p 8080:8080 -v ${PWD}/docs/c4:/usr/local/structurizr structurizr/lite
 *   y abrir http://localhost:8080
 *
 * Las imágenes del repositorio (c4-*.svg) se generan con generar_c4.py y muestran las mismas vistas.
 */
workspace "FoodExpress" "Sistema de pedidos de comida que conecta restaurantes con clientes." {

    model {
        cliente = person "Cliente" "Consulta menús, hace pedidos con extras y notas, y sigue su estado."
        personal = person "Personal del restaurante" "Administra el menú y la disponibilidad, y gestiona los pedidos en cocina."

        correo = softwareSystem "Servicio de correo" "Entrega al cliente los correos de confirmación y de pedido listo. En local: servicio mailpit del docker-compose." "Externo"
        push = softwareSystem "Servicio de avisos al celular" "Muestra avisos del pedido en el celular del cliente (simulado)." "Externo"

        foodexpress = softwareSystem "FoodExpress" "Conecta restaurantes con clientes: menús, pedidos con flujo de estados y notificaciones en tiempo real." {

            web = container "Aplicación web" "Interfaz de clientes y cocina. Nginx sirve la SPA y enruta /api y /ws a cada servicio. Servicio frontend del docker-compose." "React 19, Nginx 1.27" "Web"

            restaurantes = container "Restaurantes y Menú" "Restaurantes, menú con categorías y extras, disponibilidad e inventario. Caché de menús." "Spring Boot, Caffeine"
            restaurantesDb = container "Base de Restaurantes" "Restaurantes, platos, extras, eventos procesados. Servicio postgres-restaurantes." "PostgreSQL 16" "Base de datos"

            pedidos = container "Pedidos" "Crea pedidos validando el menú, máquina de estados y cálculo de totales." "Spring Boot" {
                controller = component "PedidoController" "API REST /api/pedidos." "Spring MVC"
                manejador = component "GlobalExceptionHandler" "Traduce errores de negocio a problem detail." "Spring MVC"
                casosUso = component "Casos de uso" "CrearPedido, AvanzarEstado, CancelarPedido, ConsultarPedidos." "Java"
                dominio = component "Pedido + PedidoBuilder" "Agregado raíz; PedidoLocal y PedidoDelivery. Patrón Builder." "Java"
                estados = component "EstadoPedido" "Recibido, EnPreparacion, Listo, Entregado, Cancelado. Patrón State." "Java"
                puertos = component "Puertos de salida" "PedidoRepository, MenuRepository, PedidoEventPublisher." "Interfaces Java"
                repoJpa = component "PedidoRepositoryAdapter" "Mapea el dominio a entidades JPA; control optimista con @Version." "Spring Data JPA"
                menuRest = component "MenuRepositoryRestAdapter" "Consulta el menú vigente." "RestClient"
                publicador = component "PedidoEventPublisherRabbit" "Publica los eventos en JSON." "Spring AMQP"
            }
            pedidosDb = container "Base de Pedidos" "Pedidos, ítems y extras. Servicio postgres-pedidos." "PostgreSQL 16" "Base de datos"

            notificaciones = container "Notificaciones" "Consume eventos y notifica por correo, WebSocket y push mediante adaptadores." "Spring Boot, WebSocket STOMP"
            notificacionesDb = container "Base de Notificaciones" "Historial de notificaciones, eventos procesados. Servicio postgres-notificaciones." "PostgreSQL 16" "Base de datos"

            rabbit = container "Bus de eventos" "Servicio rabbitmq. Exchange topic foodexpress.pedidos con colas durables y colas de mensajes muertos." "RabbitMQ 3.13" "Cola"
        }

        # Contexto
        cliente -> foodexpress "Consulta menús, hace y sigue sus pedidos"
        personal -> foodexpress "Gestiona el menú y los pedidos"
        foodexpress -> correo "Envía correos"
        foodexpress -> push "Envía avisos"
        correo -> cliente "Entrega el correo"

        # Contenedores
        cliente -> web "Usa" "HTTPS"
        personal -> web "Usa" "HTTPS"
        web -> restaurantes "Consulta y administra el menú" "JSON/HTTP"
        web -> pedidos "Crea, avanza y cancela pedidos" "JSON/HTTP"
        web -> notificaciones "Historial y avisos en vivo" "JSON/HTTP, WebSocket STOMP"
        pedidos -> restaurantes "Valida platos y precios" "JSON/HTTP síncrono"
        pedidos -> rabbit "Publica pedido.creado, pedido.estado.cambiado, pedido.cancelado" "AMQP"
        rabbit -> restaurantes "Entrega pedido.creado y pedido.cancelado (inventario)" "AMQP"
        rabbit -> notificaciones "Entrega los tres eventos" "AMQP"
        restaurantes -> restaurantesDb "Lee y escribe" "JDBC"
        pedidos -> pedidosDb "Lee y escribe" "JDBC"
        notificaciones -> notificacionesDb "Lee y escribe" "JDBC"
        notificaciones -> correo "Envía correos" "SMTP"
        notificaciones -> push "Envía avisos push"

        # Componentes de Pedidos
        web -> controller "JSON/HTTP"
        controller -> casosUso "Invoca"
        casosUso -> dominio "Crea y modifica"
        dominio -> estados "Delega su estado"
        casosUso -> puertos "Usa"
        repoJpa -> puertos "Implementa"
        menuRest -> puertos "Implementa"
        publicador -> puertos "Implementa"
        repoJpa -> pedidosDb "SQL"
        menuRest -> restaurantes "HTTP"
        publicador -> rabbit "AMQP"
    }

    views {
        systemContext foodexpress "Contexto" {
            include *
            autolayout lr
        }
        container foodexpress "Contenedores" {
            include *
            autolayout tb
        }
        component pedidos "ComponentesPedidos" {
            include *
            autolayout tb
        }

        styles {
            element "Person" {
                shape Person
                background #08427b
                color #ffffff
            }
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "Container" {
                background #438dd5
                color #ffffff
            }
            element "Component" {
                background #85bbf0
                color #10243e
            }
            element "Externo" {
                background #8a8a8a
                color #ffffff
            }
            element "Base de datos" {
                shape Cylinder
            }
            element "Cola" {
                shape Pipe
            }
            element "Web" {
                shape WebBrowser
            }
        }
    }
}
