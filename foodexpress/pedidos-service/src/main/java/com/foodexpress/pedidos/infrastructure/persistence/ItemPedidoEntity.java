package com.foodexpress.pedidos.infrastructure.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items_pedido")
public class ItemPedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;

    @Column(name = "plato_id", nullable = false)
    private Long platoId;

    @Column(name = "nombre_plato", nullable = false)
    private String nombrePlato;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "notas_especiales", length = 500)
    private String notasEspeciales;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @ElementCollection
    @CollectionTable(name = "items_pedido_extras", joinColumns = @JoinColumn(name = "item_id"))
    private List<ExtraSeleccionadoEmbeddable> extras = new ArrayList<>();

    protected ItemPedidoEntity() {
    }

    public ItemPedidoEntity(PedidoEntity pedido, Long platoId, String nombrePlato, BigDecimal precioUnitario,
                            int cantidad, String notasEspeciales, BigDecimal subtotal,
                            List<ExtraSeleccionadoEmbeddable> extras) {
        this.pedido = pedido;
        this.platoId = platoId;
        this.nombrePlato = nombrePlato;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.notasEspeciales = notasEspeciales;
        this.subtotal = subtotal;
        this.extras = new ArrayList<>(extras);
    }

    public Long getId() {
        return id;
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

    public String getNotasEspeciales() {
        return notasEspeciales;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public List<ExtraSeleccionadoEmbeddable> getExtras() {
        return extras;
    }
}
