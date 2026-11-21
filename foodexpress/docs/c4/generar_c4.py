"""
Genera los diagramas C4 de FoodExpress como SVG (contexto, contenedores y componentes).

    python3 docs/c4/generar_c4.py

Los diagramas siguen la notación C4 de Simon Brown: personas, sistemas, contenedores y
componentes, cada uno con nombre, tecnología y responsabilidad, y relaciones etiquetadas.
La misma arquitectura está descrita como código en workspace.dsl (Structurizr).
"""
from html import escape
from pathlib import Path

SALIDA = Path(__file__).parent

# Colores clásicos de C4
PERSONA = '#08427b'
SISTEMA = '#1168bd'
CONTENEDOR = '#438dd5'
COMPONENTE = '#85bbf0'
EXTERNO = '#8a8a8a'
LINEA = '#555555'
FUENTE = "Inter, 'Helvetica Neue', Arial, sans-serif"


def envolver(texto, ancho_px, tam):
    """Parte el texto en líneas que quepan en ancho_px (estimación por ancho medio de carácter)."""
    max_car = max(8, int(ancho_px / (tam * 0.54)))
    lineas, actual = [], ''
    for palabra in texto.split():
        prueba = f'{actual} {palabra}'.strip()
        if len(prueba) <= max_car:
            actual = prueba
        else:
            lineas.append(actual)
            actual = palabra
    if actual:
        lineas.append(actual)
    return lineas


class Diagrama:
    def __init__(self, ancho, alto, titulo, subtitulo):
        self.ancho, self.alto = ancho, alto
        self.fondo, self.cajas, self.flechas = [], [], []
        self.titulo, self.subtitulo = titulo, subtitulo

    # ---------- texto ----------
    def _texto(self, cx, y, lineas, tam, color, peso=400, estilo='normal'):
        partes = []
        for i, linea in enumerate(lineas):
            partes.append(
                f'<text x="{cx}" y="{y + i * tam * 1.3:.1f}" font-size="{tam}" font-weight="{peso}" '
                f'font-style="{estilo}" fill="{color}" text-anchor="middle">{escape(linea)}</text>')
        return ''.join(partes)

    def _contenido(self, x, y, w, h, nombre, tipo, desc, color_texto, dy=0):
        nom = envolver(nombre, w - 24, 17)
        tip = [f'[{tipo}]']
        des = envolver(desc, w - 28, 13) if desc else []
        alto_total = len(nom) * 22 + 20 + len(des) * 17
        y0 = y + (h - alto_total) / 2 + 16 + dy
        cx = x + w / 2
        out = self._texto(cx, y0, nom, 17, color_texto, 700)
        y1 = y0 + len(nom) * 22
        out += self._texto(cx, y1, tip, 12, color_texto, 400)
        out += self._texto(cx, y1 + 22, des, 13, color_texto, 400)
        return out

    # ---------- elementos ----------
    def caja(self, x, y, w, h, nombre, tipo, desc='', color=CONTENEDOR, texto='#fff'):
        borde = '#0b3d6e' if color != EXTERNO else '#6b6b6b'
        self.cajas.append(
            f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="10" fill="{color}" stroke="{borde}" stroke-width="1.2"/>'
            + self._contenido(x, y, w, h, nombre, tipo, desc, texto))
        return (x, y, w, h)

    def persona(self, x, y, w, h, nombre, desc, color=PERSONA):
        cx = x + w / 2
        r = 28
        cuerpo_y = y + r * 1.6
        self.cajas.append(
            f'<circle cx="{cx}" cy="{y + r}" r="{r}" fill="{color}" stroke="#062f57" stroke-width="1.2"/>'
            f'<rect x="{x}" y="{cuerpo_y}" width="{w}" height="{h - r * 1.6}" rx="26" fill="{color}" stroke="#062f57" stroke-width="1.2"/>'
            + self._contenido(x, cuerpo_y, w, h - r * 1.6, nombre, 'Persona', desc, '#fff', dy=4))
        return (x, y, w, h)

    def base_datos(self, x, y, w, h, nombre, tipo, desc='', color=CONTENEDOR):
        e = 14  # alto de la elipse
        self.cajas.append(
            f'<path d="M{x},{y + e} L{x},{y + h - e} A{w / 2},{e} 0 0 0 {x + w},{y + h - e} L{x + w},{y + e}" '
            f'fill="{color}" stroke="#0b3d6e" stroke-width="1.2"/>'
            f'<ellipse cx="{x + w / 2}" cy="{y + e}" rx="{w / 2}" ry="{e}" fill="{color}" stroke="#0b3d6e" stroke-width="1.2"/>'
            + self._contenido(x, y + e, w, h - e, nombre, tipo, desc, '#fff'))
        return (x, y, w, h)

    def cola(self, x, y, w, h, nombre, tipo, desc='', color=CONTENEDOR):
        e = 14  # cilindro horizontal
        self.cajas.append(
            f'<path d="M{x + e},{y} L{x + w - e},{y} A{e},{h / 2} 0 0 1 {x + w - e},{y + h} L{x + e},{y + h} A{e},{h / 2} 0 0 1 {x + e},{y}" '
            f'fill="{color}" stroke="#0b3d6e" stroke-width="1.2"/>'
            f'<ellipse cx="{x + w - e}" cy="{y + h / 2}" rx="{e}" ry="{h / 2}" fill="none" stroke="#0b3d6e" stroke-width="1.2"/>'
            + self._contenido(x + 6, y, w - 2 * e, h, nombre, tipo, desc, '#fff'))
        return (x, y, w, h)

    def limite(self, x, y, w, h, etiqueta, color='#444'):
        self.fondo.append(
            f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="14" fill="none" stroke="{color}" '
            f'stroke-width="1.6" stroke-dasharray="9 6"/>'
            f'<text x="{x + 16}" y="{y + h - 14}" font-size="14" font-weight="700" fill="{color}">{escape(etiqueta)}</text>')

    def grupo(self, x, y, w, h, etiqueta):
        """Agrupación ligera (capas de Clean Architecture dentro de un contenedor)."""
        self.fondo.append(
            f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="10" fill="#f3f7fc" stroke="#b9cde6" stroke-width="1"/>'
            f'<text x="{x + 12}" y="{y + 20}" font-size="12" font-weight="700" fill="#4a6a8f" '
            f'letter-spacing="0.08em">{escape(etiqueta.upper())}</text>')

    def flecha(self, puntos, etiqueta='', tecnologia='', pos=0.5, dx=0, dy=0, discontinua=False):
        """puntos: lista de (x, y). La etiqueta se ubica sobre el tramo indicado por pos."""
        d = 'M' + ' L'.join(f'{px},{py}' for px, py in puntos)
        guion = ' stroke-dasharray="6 5"' if discontinua else ''
        svg = f'<path d="{d}" fill="none" stroke="{LINEA}" stroke-width="1.6" marker-end="url(#punta)"{guion}/>'
        if etiqueta:
            # punto medio a lo largo del recorrido
            tramos = list(zip(puntos, puntos[1:]))
            largos = [abs(b[0] - a[0]) + abs(b[1] - a[1]) for a, b in tramos]
            objetivo, acum = sum(largos) * pos, 0
            for (a, b), largo in zip(tramos, largos):
                if acum + largo >= objetivo:
                    t = (objetivo - acum) / largo if largo else 0
                    mx, my = a[0] + (b[0] - a[0]) * t + dx, a[1] + (b[1] - a[1]) * t + dy
                    break
                acum += largo
            lineas = envolver(etiqueta, 190, 13)
            if tecnologia:
                lineas_t = [f'[{tecnologia}]']
            else:
                lineas_t = []
            alto = len(lineas) * 16 + len(lineas_t) * 15 + 8
            ancho = max(len(l) for l in lineas + lineas_t) * 13 * 0.55 + 16
            svg += (f'<rect x="{mx - ancho / 2}" y="{my - alto / 2}" width="{ancho}" height="{alto}" rx="4" '
                    f'fill="#ffffff" fill-opacity="0.94"/>')
            y0 = my - alto / 2 + 15
            for i, l in enumerate(lineas):
                svg += f'<text x="{mx}" y="{y0 + i * 16}" font-size="13" fill="#333" text-anchor="middle">{escape(l)}</text>'
            for i, l in enumerate(lineas_t):
                svg += (f'<text x="{mx}" y="{y0 + len(lineas) * 16 + i * 15}" font-size="11.5" fill="#666" '
                        f'text-anchor="middle" font-style="italic">{escape(l)}</text>')
        self.flechas.append(svg)

    def leyenda(self, elementos, x, y):
        out = f'<text x="{x}" y="{y}" font-size="12" font-weight="700" fill="#555">LEYENDA</text>'
        for i, (color, nombre) in enumerate(elementos):
            yy = y + 14 + i * 24
            out += (f'<rect x="{x}" y="{yy}" width="18" height="14" rx="3" fill="{color}"/>'
                    f'<text x="{x + 26}" y="{yy + 12}" font-size="12.5" fill="#444">{escape(nombre)}</text>')
        self.cajas.append(out)

    def svg(self):
        return (
            f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {self.ancho} {self.alto}" '
            f'width="{self.ancho}" height="{self.alto}" font-family="{FUENTE}">'
            '<defs><marker id="punta" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="9" markerHeight="9" '
            f'orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z" fill="{LINEA}"/></marker></defs>'
            f'<rect width="100%" height="100%" fill="#ffffff"/>'
            f'<text x="40" y="52" font-size="26" font-weight="700" fill="#1c1c1c">{escape(self.titulo)}</text>'
            f'<text x="40" y="80" font-size="15" fill="#666">{escape(self.subtitulo)}</text>'
            + ''.join(self.fondo) + ''.join(self.flechas) + ''.join(self.cajas) + '</svg>')

    def guardar(self, nombre):
        (SALIDA / nombre).write_text(self.svg(), encoding='utf-8')
        print('generado', SALIDA / nombre)


# =====================================================================
# Nivel 1: Contexto del sistema
# =====================================================================
def contexto():
    d = Diagrama(1600, 900, 'Nivel 1 · Diagrama de contexto',
                 'FoodExpress y las personas y sistemas externos con los que interactúa')

    cliente = d.persona(160, 140, 280, 210, 'Cliente',
                        'Consulta menús, hace pedidos con extras y notas, y sigue su estado.')
    rest = d.persona(1160, 140, 280, 210, 'Personal del restaurante',
                     'Administra el menú y la disponibilidad, y gestiona los pedidos en cocina.')
    d.caja(560, 420, 480, 190, 'FoodExpress', 'Sistema de software',
           'Conecta restaurantes con clientes: publica menús, recibe pedidos, sigue su estado y avisa a cada parte.',
           color=SISTEMA)
    d.caja(160, 680, 300, 150, 'Servicio de correo', 'Sistema externo',
           'Entrega al cliente los correos de confirmación y de pedido listo.', color=EXTERNO)
    d.caja(1140, 680, 300, 150, 'Servicio de avisos al celular', 'Sistema externo',
           'Muestra avisos del pedido en el celular del cliente.', color=EXTERNO)

    d.flecha([(300, 350), (300, 470), (560, 470)], 'Consulta menús, hace y sigue sus pedidos', pos=0.35)
    d.flecha([(1300, 350), (1300, 470), (1040, 470)], 'Gestiona el menú y los pedidos', pos=0.35)
    d.flecha([(700, 610), (700, 755), (460, 755)], 'Envía correos', pos=0.62)
    d.flecha([(900, 610), (900, 755), (1140, 755)], 'Envía avisos', pos=0.62)
    d.flecha([(160, 755), (110, 755), (110, 245), (160, 245)], 'Entrega el correo', pos=0.5, dx=0)

    d.leyenda([(PERSONA, 'Persona'), (SISTEMA, 'Sistema en alcance'), (EXTERNO, 'Sistema externo')], 1180, 30)
    d.guardar('c4-1-contexto.svg')


# =====================================================================
# Nivel 2: Contenedores
# =====================================================================
def contenedores():
    d = Diagrama(1780, 1230, 'Nivel 2 · Diagrama de contenedores',
                 'Cada caja es un servicio del docker-compose.yml; el nombre del servicio va entre corchetes')

    d.persona(120, 110, 250, 180, 'Cliente', 'Pide comida y sigue su pedido.')
    d.persona(1390, 110, 250, 180, 'Personal del restaurante', 'Opera la cocina y el menú.')

    d.limite(60, 320, 1630, 730, 'FoodExpress  [Sistema de software]')

    d.caja(620, 350, 520, 140, 'Aplicación web', 'React 19 · Nginx 1.27 · [frontend]',
           'Interfaz de clientes y cocina. Nginx sirve la SPA y enruta /api y /ws a cada servicio.')

    d.caja(110, 580, 380, 170, 'Restaurantes y Menú', 'Spring Boot 3.5 · [restaurantes-service]',
           'Restaurantes, menú con categorías y extras, disponibilidad e inventario. Caché de menús (Caffeine).')
    d.caja(690, 580, 380, 170, 'Pedidos', 'Spring Boot 3.5 · [pedidos-service]',
           'Crea pedidos validando el menú, máquina de estados (State), totales con Builder.')
    d.caja(1270, 580, 380, 170, 'Notificaciones', 'Spring Boot 3.5 · [notificaciones-service]',
           'Consume eventos y notifica por correo, WebSocket (STOMP) y push mediante adaptadores.')

    d.base_datos(110, 830, 240, 140, 'Base de Restaurantes', 'PostgreSQL 16', '[postgres-restaurantes]')
    d.base_datos(660, 830, 200, 140, 'Base de Pedidos', 'PostgreSQL 16', '[postgres-pedidos]')
    d.cola(900, 840, 330, 100, 'Bus de eventos', 'RabbitMQ 3.13 · [rabbitmq]', 'Exchange topic foodexpress.pedidos')
    d.base_datos(1430, 830, 240, 140, 'Base de Notificaciones', 'PostgreSQL 16', '[postgres-notificaciones]')

    d.caja(1180, 1100, 280, 100, 'Servicio de correo', 'Externo · en local [mailpit]', color=EXTERNO)
    d.caja(1480, 1100, 280, 100, 'Avisos al celular', 'Externo · simulado', color=EXTERNO)

    # Personas -> aplicación web
    d.flecha([(245, 290), (245, 420), (620, 420)], 'Usa', 'HTTPS', pos=0.62)
    d.flecha([(1515, 290), (1515, 420), (1140, 420)], 'Usa', 'HTTPS', pos=0.62)
    # Aplicación web -> servicios
    d.flecha([(700, 490), (700, 535), (300, 535), (300, 580)], 'Consulta y administra el menú', 'JSON/HTTP', pos=0.5)
    d.flecha([(880, 490), (880, 580)], 'Crea, avanza y cancela pedidos', 'JSON/HTTP', pos=0.5)
    d.flecha([(1060, 490), (1060, 535), (1460, 535), (1460, 580)], 'Historial y avisos en vivo',
             'JSON/HTTP · WebSocket STOMP', pos=0.5)
    # Pedidos -> Restaurantes (síncrono)
    d.flecha([(690, 665), (490, 665)], 'Valida platos y precios', 'JSON/HTTP síncrono', pos=0.5)
    # Bases de datos
    d.flecha([(230, 750), (230, 830)], 'Lee y escribe', 'JDBC', pos=0.5, dx=-62)
    d.flecha([(760, 750), (760, 830)], 'Lee y escribe', 'JDBC', pos=0.5, dx=-62)
    d.flecha([(1550, 750), (1550, 830)], 'Lee y escribe', 'JDBC', pos=0.5, dx=66)
    # Eventos
    d.flecha([(1000, 750), (1000, 840)], 'Publica los 3 eventos', 'AMQP', pos=0.5, dx=86)
    d.flecha([(1230, 890), (1330, 890), (1330, 750)], 'Consume los 3', 'AMQP', pos=0.25, dy=-24)
    d.flecha([(960, 940), (960, 1010), (420, 1010), (420, 750)], 'Consume creado y cancelado (ajusta inventario)',
             'AMQP', pos=0.45)
    # Externos
    d.flecha([(1390, 750), (1390, 1100)], 'Envía correos', 'SMTP', pos=0.72, dx=0)
    d.flecha([(1650, 690), (1720, 690), (1720, 1100)], 'Envía avisos', 'API del proveedor', pos=0.93)

    d.leyenda([(PERSONA, 'Persona'), (CONTENEDOR, 'Contenedor'), (EXTERNO, 'Sistema externo')], 1500, 30)
    d.guardar('c4-2-contenedores.svg')


# =====================================================================
# Nivel 3: Componentes del servicio de Pedidos
# =====================================================================
def componentes():
    d = Diagrama(1760, 1120, 'Nivel 3 · Componentes del servicio de Pedidos',
                 'Clean Architecture: las dependencias apuntan hacia el dominio; la infraestructura implementa los puertos')

    d.caja(640, 110, 480, 100, 'Aplicación web', 'Contenedor · React + Nginx', color=CONTENEDOR)

    d.limite(40, 240, 1680, 760, 'Pedidos  [Contenedor · Spring Boot]')

    d.grupo(70, 270, 1620, 150, 'Infraestructura · adaptadores de entrada')
    d.caja(560, 305, 380, 100, 'PedidoController', 'Componente · Spring MVC',
           'API REST /api/pedidos.', color=COMPONENTE, texto='#10243e')
    d.caja(980, 305, 380, 100, 'GlobalExceptionHandler', 'Componente · Spring MVC',
           'Traduce errores de negocio a problem detail.', color=COMPONENTE, texto='#10243e')

    d.grupo(70, 440, 1620, 330, 'Aplicación y dominio · Java puro, sin Spring')
    d.caja(110, 480, 470, 120, 'Casos de uso', 'Componente · Java',
           'CrearPedido, AvanzarEstado, CancelarPedido, ConsultarPedidos.', color=COMPONENTE, texto='#10243e')
    d.caja(650, 480, 460, 120, 'Pedido + PedidoBuilder', 'Componente · dominio · patrón Builder',
           'Agregado raíz; PedidoLocal y PedidoDelivery (LSP). Calcula totales.', color=COMPONENTE, texto='#10243e')
    d.caja(1180, 480, 470, 120, 'EstadoPedido', 'Componente · dominio · patrón State',
           'Recibido, EnPreparacion, Listo, Entregado, Cancelado.', color=COMPONENTE, texto='#10243e')
    d.caja(110, 640, 1540, 100, 'Puertos de salida: PedidoRepository · MenuRepository · PedidoEventPublisher',
           'Interfaces · DIP', 'Lo que los casos de uso necesitan, sin saber cómo se implementa.',
           color='#dbe9f8', texto='#10243e')

    d.grupo(70, 790, 1620, 160, 'Infraestructura · adaptadores de salida')
    d.caja(110, 830, 470, 100, 'PedidoRepositoryAdapter', 'Componente · Spring Data JPA',
           'Mapea el dominio a entidades JPA; @Version.', color=COMPONENTE, texto='#10243e')
    d.caja(650, 830, 460, 100, 'MenuRepositoryRestAdapter', 'Componente · RestClient',
           'Consulta el menú vigente.', color=COMPONENTE, texto='#10243e')
    d.caja(1180, 830, 470, 100, 'PedidoEventPublisherRabbit', 'Componente · Spring AMQP',
           'Publica los eventos en JSON.', color=COMPONENTE, texto='#10243e')

    d.base_datos(305, 1030, 290, 80, 'pedidos_db', 'PostgreSQL', color=CONTENEDOR)
    d.caja(705, 1030, 350, 80, 'Restaurantes y Menú', 'Contenedor · Spring Boot', color=CONTENEDOR)
    d.cola(1240, 1030, 350, 80, 'RabbitMQ', 'Contenedor · broker', color=CONTENEDOR)

    d.flecha([(880, 210), (880, 240), (750, 240), (750, 305)], 'JSON/HTTP', pos=0.55)
    d.flecha([(560, 355), (480, 355), (480, 480)], 'Invoca', pos=0.75, dx=36)
    d.flecha([(580, 540), (650, 540)], 'Crea', pos=0.5, dy=-20)
    d.flecha([(1110, 540), (1180, 540)], 'Delega', pos=0.5, dy=-20)
    d.flecha([(250, 600), (250, 640)], 'Usa', pos=0.5, dx=30)
    # implementaciones (línea discontinua hacia el puerto)
    d.flecha([(450, 830), (450, 740)], 'Implementa', pos=0.5, dx=48, discontinua=True)
    d.flecha([(880, 830), (880, 740)], 'Implementa', pos=0.5, dx=48, discontinua=True)
    d.flecha([(1415, 830), (1415, 740)], 'Implementa', pos=0.5, dx=48, discontinua=True)
    d.flecha([(450, 930), (450, 1030)], 'SQL', pos=0.5, dx=26)
    d.flecha([(880, 930), (880, 1030)], 'HTTP', pos=0.5, dx=28)
    d.flecha([(1415, 930), (1415, 1030)], 'AMQP', pos=0.5, dx=30)

    d.leyenda([(CONTENEDOR, 'Contenedor'), (COMPONENTE, 'Componente'), ('#dbe9f8', 'Interfaces (puertos)')], 1480, 30)
    d.guardar('c4-3-componentes-pedidos.svg')


if __name__ == '__main__':
    contexto()
    contenedores()
    componentes()
