package com.foodexpress.notificaciones.application.usecase;

import com.foodexpress.notificaciones.application.usecase.Fakes.CanalEspia;
import com.foodexpress.notificaciones.application.usecase.Fakes.EventosEnMemoria;
import com.foodexpress.notificaciones.application.usecase.Fakes.NotificacionesEnMemoria;
import com.foodexpress.notificaciones.domain.model.Canal;
import com.foodexpress.notificaciones.domain.model.EstadoEnvio;
import com.foodexpress.notificaciones.domain.model.Mensaje;
import com.foodexpress.notificaciones.domain.model.TipoDestinatario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Qué se notifica, a quién y por qué canal ante cada evento de pedidos. */
class NotificacionesUseCaseTest {

    private CanalEspia email;
    private CanalEspia webSocket;
    private CanalEspia push;
    private NotificacionesEnMemoria registro;
    private NotificarPedidoCreadoUseCase pedidoCreado;
    private NotificarCambioEstadoUseCase cambioEstado;
    private NotificarPedidoCanceladoUseCase pedidoCancelado;

    @BeforeEach
    void preparar() {
        email = new CanalEspia(Canal.EMAIL);
        webSocket = new CanalEspia(Canal.WEBSOCKET);
        push = new CanalEspia(Canal.PUSH);
        registro = new NotificacionesEnMemoria();
        EventosEnMemoria eventos = new EventosEnMemoria();
        DespachadorNotificaciones despachador =
                new DespachadorNotificaciones(List.of(email, webSocket, push), registro);
        pedidoCreado = new NotificarPedidoCreadoUseCase(eventos, despachador);
        cambioEstado = new NotificarCambioEstadoUseCase(eventos, despachador);
        pedidoCancelado = new NotificarPedidoCanceladoUseCase(eventos, despachador);
    }

    @Test
    void pedidoCreadoAlertaAlRestauranteYConfirmaAlCliente() {
        pedidoCreado.ejecutar(Fakes.evento("evt-1", "pedido.creado", "LOCAL", null, "RECIBIDO", null, null));

        Mensaje alRestaurante = webSocket.para(TipoDestinatario.RESTAURANTE).get(0);
        assertEquals("Nuevo pedido #15", alRestaurante.asunto());
        assertTrue(alRestaurante.cuerpo().contains("2 x Hamburguesa clásica, 1 x Limonada natural"));
        assertTrue(alRestaurante.cuerpo().contains("$66.960"));

        assertEquals(1, email.enviados.size());
        assertEquals("ana@correo.com", email.enviados.get(0).destinatario().email());
        assertEquals("Recibimos tu pedido #15", email.enviados.get(0).asunto());
        assertEquals(1, webSocket.para(TipoDestinatario.CLIENTE).size());
        assertTrue(push.enviados.isEmpty());

        assertEquals(3, registro.guardadas.size());
    }

    @Test
    void elMismoEventoEntregadoDosVecesNotificaUnaSolaVez() {
        pedidoCreado.ejecutar(Fakes.evento("evt-1", "pedido.creado", "LOCAL", null, "RECIBIDO", null, null));
        pedidoCreado.ejecutar(Fakes.evento("evt-1", "pedido.creado", "LOCAL", null, "RECIBIDO", null, null));

        assertEquals(1, email.enviados.size());
        assertEquals(3, registro.guardadas.size());
    }

    @Test
    void elCambioDeEstadoLlegaAlClienteEnTiempoRealYPorPush() {
        cambioEstado.ejecutar(Fakes.evento("evt-2", "pedido.estado.cambiado", "LOCAL",
                "RECIBIDO", "EN_PREPARACION", null, null));

        assertEquals(1, webSocket.enviados.size());
        assertEquals("Tu pedido #15 está en preparación", webSocket.enviados.get(0).asunto());
        assertEquals("EN_PREPARACION", webSocket.enviados.get(0).estadoPedido());
        assertEquals(1, push.enviados.size());
        assertTrue(email.enviados.isEmpty());
    }

    @Test
    void cuandoElPedidoQuedaListoAdemasSeEnviaCorreo() {
        cambioEstado.ejecutar(Fakes.evento("evt-3", "pedido.estado.cambiado", "LOCAL",
                "EN_PREPARACION", "LISTO", null, null));

        assertEquals(1, email.enviados.size());
        assertEquals("Tu pedido #15 está listo para recoger", email.enviados.get(0).asunto());
    }

    @Test
    void elAvisoDeListoCambiaSiElPedidoEsADomicilio() {
        cambioEstado.ejecutar(Fakes.evento("evt-3", "pedido.estado.cambiado", "DELIVERY",
                "EN_PREPARACION", "LISTO", null, null));

        assertEquals("Tu pedido #15 está listo y va en camino", email.enviados.get(0).asunto());
    }

    @Test
    void siCancelaElClienteSeAvisaSoloAlRestaurante() {
        pedidoCancelado.ejecutar(Fakes.evento("evt-4", "pedido.cancelado", "LOCAL",
                "RECIBIDO", "CANCELADO", "CLIENTE", "Me equivoqué"));

        assertEquals(1, webSocket.para(TipoDestinatario.RESTAURANTE).size());
        assertTrue(webSocket.para(TipoDestinatario.CLIENTE).isEmpty());
        assertTrue(email.enviados.isEmpty());
        assertTrue(webSocket.enviados.get(0).cuerpo().contains("Motivo: Me equivoqué."));
    }

    @Test
    void siCancelaElRestauranteSeAvisaSoloAlCliente() {
        pedidoCancelado.ejecutar(Fakes.evento("evt-5", "pedido.cancelado", "LOCAL",
                "EN_PREPARACION", "CANCELADO", "RESTAURANTE", null));

        assertEquals(1, email.enviados.size());
        assertEquals("Tu pedido #15 fue cancelado", email.enviados.get(0).asunto());
        assertEquals(1, webSocket.para(TipoDestinatario.CLIENTE).size());
        assertEquals(1, push.enviados.size());
        assertTrue(webSocket.para(TipoDestinatario.RESTAURANTE).isEmpty());
    }

    @Test
    void siUnCanalFallaSeRegistraElFalloYLosDemasSiguen() {
        email.fallar = true;

        pedidoCreado.ejecutar(Fakes.evento("evt-6", "pedido.creado", "LOCAL", null, "RECIBIDO", null, null));

        assertEquals(1, registro.cantidad(Canal.EMAIL, EstadoEnvio.FALLIDA));
        assertEquals(2, registro.cantidad(Canal.WEBSOCKET, EstadoEnvio.ENVIADA));
        assertEquals(1, webSocket.para(TipoDestinatario.CLIENTE).size());
    }

    @Test
    void unCanalNuevoSeAgregaSinTocarLosCasosDeUso() {
        // OCP: el despachador recibe la lista de canales; aquí se arma con uno solo.
        NotificacionesEnMemoria otroRegistro = new NotificacionesEnMemoria();
        CanalEspia soloWebSocket = new CanalEspia(Canal.WEBSOCKET);
        DespachadorNotificaciones despachador = new DespachadorNotificaciones(List.of(soloWebSocket), otroRegistro);

        new NotificarPedidoCreadoUseCase(new EventosEnMemoria(), despachador)
                .ejecutar(Fakes.evento("evt-7", "pedido.creado", "LOCAL", null, "RECIBIDO", null, null));

        assertEquals(2, soloWebSocket.enviados.size());
        assertEquals(1, otroRegistro.cantidad(Canal.EMAIL, EstadoEnvio.FALLIDA));
    }
}
