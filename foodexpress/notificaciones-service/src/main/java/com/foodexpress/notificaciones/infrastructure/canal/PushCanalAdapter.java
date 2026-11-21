package com.foodexpress.notificaciones.infrastructure.canal;

import com.foodexpress.notificaciones.application.port.out.CanalNotificacion;
import com.foodexpress.notificaciones.domain.model.Canal;
import com.foodexpress.notificaciones.domain.model.Mensaje;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * PATRÓN ADAPTER: adapta el SDK del proveedor de push a CanalNotificacion.
 *
 * Traduce el Mensaje del dominio a lo que el proveedor espera: un tema por pedido
 * y una carga de pares clave-valor.
 */
@Component
public class PushCanalAdapter implements CanalNotificacion {

    private final ProveedorPushSimulado proveedor;

    public PushCanalAdapter(ProveedorPushSimulado proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public Canal canal() {
        return Canal.PUSH;
    }

    @Override
    public void enviar(Mensaje mensaje) {
        String tema = "pedido-" + mensaje.pedidoId();
        Map<String, String> carga = Map.of(
                "title", mensaje.asunto(),
                "body", mensaje.cuerpo(),
                "pedidoId", String.valueOf(mensaje.pedidoId()));
        proveedor.publicarEnTema(tema, carga);
    }
}
