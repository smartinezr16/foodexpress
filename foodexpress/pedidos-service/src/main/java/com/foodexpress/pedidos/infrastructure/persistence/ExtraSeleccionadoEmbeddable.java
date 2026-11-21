package com.foodexpress.pedidos.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class ExtraSeleccionadoEmbeddable {

    @Column(name = "extra_id")
    private Long extraId;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "precio_adicional", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioAdicional;

    protected ExtraSeleccionadoEmbeddable() {
    }

    public ExtraSeleccionadoEmbeddable(Long extraId, String nombre, BigDecimal precioAdicional) {
        this.extraId = extraId;
        this.nombre = nombre;
        this.precioAdicional = precioAdicional;
    }

    public Long getExtraId() {
        return extraId;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecioAdicional() {
        return precioAdicional;
    }
}
