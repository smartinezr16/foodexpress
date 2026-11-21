package com.foodexpress.pedidos.domain.model;

import com.foodexpress.pedidos.domain.exception.PedidoInvalidoException;
import com.foodexpress.pedidos.domain.model.estado.ActorCancelacion;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedidoTipo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * PATRÓN BUILDER.
 *
 * Un pedido tiene muchos datos y varios son opcionales (extras, notas, dirección de
 * entrega). El builder permite armarlo paso a paso y valida todo en un solo punto
 * ({@link #construir()}), de modo que nunca exista un pedido a medio construir.
 *
 * <pre>
 * Pedido pedido = Pedido.builder()
 *         .cliente("Ana", "ana@correo.com")
 *         .restaurante(1L, "La Brasa")
 *         .tasaImpuesto(new BigDecimal("0.08"))
 *         .agregarItem(item -> item
 *                 .plato(10L, "Hamburguesa", new BigDecimal("25000"))
 *                 .cantidad(2)
 *                 .conExtra(3L, "Queso", new BigDecimal("3000"))
 *                 .notas("Sin cebolla"))
 *         .conDomicilio("Calle 10 # 5-20", new BigDecimal("5000"))
 *         .construir();
 * </pre>
 */
public final class PedidoBuilder {

    Long id;
    Long version;
    Cliente cliente;
    Long restauranteId;
    String restauranteNombre;
    final List<ItemPedido> items = new ArrayList<>();
    BigDecimal tasaImpuesto = BigDecimal.ZERO;
    Instant fechaCreacion;
    EstadoPedidoTipo estado = EstadoPedidoTipo.RECIBIDO;
    ActorCancelacion canceladoPor;
    String motivoCancelacion;

    private TipoPedido tipo = TipoPedido.LOCAL;
    String direccionEntrega;
    BigDecimal costoEnvio;

    PedidoBuilder() {
    }

    public PedidoBuilder cliente(String nombre, String email) {
        this.cliente = new Cliente(nombre, email);
        return this;
    }

    public PedidoBuilder restaurante(Long restauranteId, String nombre) {
        this.restauranteId = restauranteId;
        this.restauranteNombre = nombre;
        return this;
    }

    public PedidoBuilder tasaImpuesto(BigDecimal tasaImpuesto) {
        this.tasaImpuesto = tasaImpuesto;
        return this;
    }

    public PedidoBuilder agregarItem(ItemPedido item) {
        this.items.add(item);
        return this;
    }

    /** Arma el ítem con su propio builder y lo agrega al pedido. */
    public PedidoBuilder agregarItem(Consumer<ItemPedido.Builder> configuracion) {
        ItemPedido.Builder itemBuilder = ItemPedido.builder();
        configuracion.accept(itemBuilder);
        return agregarItem(itemBuilder.construir());
    }

    public PedidoBuilder paraRecoger() {
        this.tipo = TipoPedido.LOCAL;
        this.direccionEntrega = null;
        this.costoEnvio = null;
        return this;
    }

    public PedidoBuilder conDomicilio(String direccionEntrega, BigDecimal costoEnvio) {
        this.tipo = TipoPedido.DELIVERY;
        this.direccionEntrega = direccionEntrega;
        this.costoEnvio = costoEnvio;
        return this;
    }

    // ---------- Datos que solo se usan al reconstruir un pedido ya guardado ----------

    public PedidoBuilder conId(Long id, Long version) {
        this.id = id;
        this.version = version;
        return this;
    }

    public PedidoBuilder enEstado(EstadoPedidoTipo estado) {
        this.estado = estado;
        return this;
    }

    public PedidoBuilder creadoEn(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
        return this;
    }

    public PedidoBuilder canceladoPor(ActorCancelacion actor, String motivo) {
        this.canceladoPor = actor;
        this.motivoCancelacion = motivo;
        return this;
    }

    public Pedido construir() {
        validar();
        if (fechaCreacion == null) {
            fechaCreacion = Instant.now();
        }
        return tipo == TipoPedido.DELIVERY ? new PedidoDelivery(this) : new PedidoLocal(this);
    }

    private void validar() {
        if (cliente == null) {
            throw new PedidoInvalidoException("El pedido debe tener un cliente");
        }
        if (restauranteId == null) {
            throw new PedidoInvalidoException("El pedido debe indicar el restaurante");
        }
        if (items.isEmpty()) {
            throw new PedidoInvalidoException("El pedido debe tener al menos un plato");
        }
        if (tasaImpuesto == null || tasaImpuesto.signum() < 0) {
            throw new PedidoInvalidoException("La tasa de impuesto no es válida");
        }
        if (estado == null) {
            throw new PedidoInvalidoException("El pedido debe tener un estado");
        }
        if (tipo == TipoPedido.DELIVERY) {
            if (direccionEntrega == null || direccionEntrega.isBlank()) {
                throw new PedidoInvalidoException("Un pedido a domicilio necesita dirección de entrega");
            }
            if (costoEnvio == null || costoEnvio.signum() < 0) {
                throw new PedidoInvalidoException("El costo de envío no es válido");
            }
        }
    }
}
