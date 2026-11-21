package com.foodexpress.pedidos.domain.model;

import com.foodexpress.pedidos.domain.model.estado.ActorCancelacion;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedido;
import com.foodexpress.pedidos.domain.model.estado.EstadoPedidoTipo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Raíz del agregado Pedido.
 *
 * LSP: {@link PedidoLocal} y {@link PedidoDelivery} son intercambiables. Los casos de
 * uso, la máquina de estados y la persistencia trabajan contra esta clase sin
 * preguntar de qué tipo concreto se trata; cada subclase solo aporta su costo de envío.
 *
 * Se construye únicamente con {@link PedidoBuilder}.
 */
public abstract class Pedido {

    private static final int ESCALA_MONEDA = 2;

    private final Long id;
    private final Long version;
    private final Cliente cliente;
    private final Long restauranteId;
    private final String restauranteNombre;
    private final List<ItemPedido> items;
    private final BigDecimal tasaImpuesto;
    private final Instant fechaCreacion;

    private EstadoPedido estado;
    private ActorCancelacion canceladoPor;
    private String motivoCancelacion;

    Pedido(PedidoBuilder builder) {
        this.id = builder.id;
        this.version = builder.version;
        this.cliente = builder.cliente;
        this.restauranteId = builder.restauranteId;
        this.restauranteNombre = builder.restauranteNombre;
        this.items = List.copyOf(builder.items);
        this.tasaImpuesto = builder.tasaImpuesto;
        this.fechaCreacion = builder.fechaCreacion;
        this.estado = builder.estado.crearEstado();
        this.canceladoPor = builder.canceladoPor;
        this.motivoCancelacion = builder.motivoCancelacion;
    }

    public static PedidoBuilder builder() {
        return new PedidoBuilder();
    }

    public abstract TipoPedido getTipo();

    /** Valor del domicilio; cero cuando el cliente recoge el pedido. */
    public abstract BigDecimal costoEnvio();

    public Optional<String> direccionEntrega() {
        return Optional.empty();
    }

    // ---------- Cálculo de totales ----------

    public BigDecimal subtotal() {
        return items.stream()
                .map(ItemPedido::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    /** El impuesto se calcula sobre los platos y sus extras, no sobre el domicilio. */
    public BigDecimal impuestos() {
        return subtotal().multiply(tasaImpuesto).setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    public BigDecimal total() {
        return subtotal()
                .add(impuestos())
                .add(costoEnvio())
                .setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    // ---------- Máquina de estados (delegada al patrón State) ----------

    /** Avanza al siguiente estado y devuelve el estado que tenía antes. */
    public EstadoPedidoTipo avanzar() {
        EstadoPedidoTipo anterior = estado.tipo();
        this.estado = estado.siguiente();
        return anterior;
    }

    /** Cancela el pedido si el estado actual se lo permite al actor. Devuelve el estado anterior. */
    public EstadoPedidoTipo cancelar(ActorCancelacion actor, String motivo) {
        EstadoPedidoTipo anterior = estado.tipo();
        this.estado = estado.cancelar(actor);
        this.canceladoPor = actor;
        this.motivoCancelacion = (motivo == null || motivo.isBlank()) ? null : motivo.trim();
        return anterior;
    }

    public EstadoPedidoTipo getEstado() {
        return estado.tipo();
    }

    public boolean estaFinalizado() {
        return estado.esFinal();
    }

    // ---------- Lectura ----------

    public Long getId() {
        return id;
    }

    /** Versión para control de concurrencia optimista; nula mientras el pedido no se ha guardado. */
    public Long getVersion() {
        return version;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public String getRestauranteNombre() {
        return restauranteNombre;
    }

    public List<ItemPedido> getItems() {
        return items;
    }

    public BigDecimal getTasaImpuesto() {
        return tasaImpuesto;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Optional<ActorCancelacion> canceladoPor() {
        return Optional.ofNullable(canceladoPor);
    }

    public Optional<String> motivoCancelacion() {
        return Optional.ofNullable(motivoCancelacion);
    }
}
