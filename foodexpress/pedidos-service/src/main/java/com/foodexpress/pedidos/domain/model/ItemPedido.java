package com.foodexpress.pedidos.domain.model;

import com.foodexpress.pedidos.domain.exception.PedidoInvalidoException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Un plato dentro de un pedido, con su cantidad, extras y notas especiales. */
public final class ItemPedido {

    private final Long platoId;
    private final String nombrePlato;
    private final BigDecimal precioUnitario;
    private final int cantidad;
    private final List<ExtraSeleccionado> extras;
    private final String notasEspeciales;

    private ItemPedido(Builder builder) {
        this.platoId = builder.platoId;
        this.nombrePlato = builder.nombrePlato;
        this.precioUnitario = builder.precioUnitario;
        this.cantidad = builder.cantidad;
        this.extras = List.copyOf(builder.extras);
        this.notasEspeciales = builder.notasEspeciales;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Precio de una unidad del plato sumando los extras elegidos. */
    public BigDecimal precioUnitarioConExtras() {
        BigDecimal totalExtras = extras.stream()
                .map(ExtraSeleccionado::precioAdicional)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return precioUnitario.add(totalExtras);
    }

    public BigDecimal subtotal() {
        return precioUnitarioConExtras().multiply(BigDecimal.valueOf(cantidad));
    }

    public Long getPlatoId() {
        return platoId;
    }

    public String getNombrePlato() {
        return nombrePlato;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public List<ExtraSeleccionado> getExtras() {
        return extras;
    }

    public String getNotasEspeciales() {
        return notasEspeciales;
    }

    /** PATRÓN BUILDER (parte del ítem): extras y notas son opcionales. */
    public static final class Builder {

        private Long platoId;
        private String nombrePlato;
        private BigDecimal precioUnitario;
        private int cantidad = 1;
        private final List<ExtraSeleccionado> extras = new ArrayList<>();
        private String notasEspeciales;

        private Builder() {
        }

        public Builder plato(Long platoId, String nombre, BigDecimal precioUnitario) {
            this.platoId = platoId;
            this.nombrePlato = nombre;
            this.precioUnitario = precioUnitario;
            return this;
        }

        public Builder cantidad(int cantidad) {
            this.cantidad = cantidad;
            return this;
        }

        public Builder conExtra(Long extraId, String nombre, BigDecimal precioAdicional) {
            this.extras.add(new ExtraSeleccionado(extraId, nombre, precioAdicional));
            return this;
        }

        public Builder notas(String notasEspeciales) {
            this.notasEspeciales = (notasEspeciales == null || notasEspeciales.isBlank())
                    ? null
                    : notasEspeciales.trim();
            return this;
        }

        public ItemPedido construir() {
            if (platoId == null || nombrePlato == null || nombrePlato.isBlank()) {
                throw new PedidoInvalidoException("Cada ítem debe indicar el plato");
            }
            if (precioUnitario == null || precioUnitario.signum() < 0) {
                throw new PedidoInvalidoException("El precio del plato '" + nombrePlato + "' no es válido");
            }
            if (cantidad < 1) {
                throw new PedidoInvalidoException("La cantidad de '" + nombrePlato + "' debe ser al menos 1");
            }
            return new ItemPedido(this);
        }
    }
}
