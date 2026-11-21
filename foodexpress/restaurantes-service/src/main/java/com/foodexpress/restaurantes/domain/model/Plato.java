package com.foodexpress.restaurantes.domain.model;

import com.foodexpress.restaurantes.domain.exception.RecursoNoEncontradoException;
import com.foodexpress.restaurantes.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Plato del menú de un restaurante.
 *
 * La disponibilidad tiene dos componentes:
 * - "habilitado": lo decide el restaurante (puede retirar el plato de la carta);
 * - "unidadesDisponibles": inventario opcional del día. Si es null, el plato no
 *   lleva control de unidades.
 * Un plato está disponible cuando está habilitado y le quedan unidades.
 */
public class Plato {

    private final Long id;
    private final Long version;
    private final Long restauranteId;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private CategoriaPlato categoria;
    private boolean habilitado;
    private Integer unidadesDisponibles;
    private final List<Extra> extras;

    public Plato(Long id, Long version, Long restauranteId, String nombre, String descripcion, BigDecimal precio,
                 CategoriaPlato categoria, boolean habilitado, Integer unidadesDisponibles, List<Extra> extras) {
        if (restauranteId == null) {
            throw new ReglaNegocioException("El plato debe pertenecer a un restaurante");
        }
        this.id = id;
        this.version = version;
        this.restauranteId = restauranteId;
        this.extras = new ArrayList<>(extras == null ? List.of() : extras);
        actualizarDatos(nombre, descripcion, precio, categoria);
        cambiarDisponibilidad(habilitado, unidadesDisponibles);
    }

    public static Plato nuevo(Long restauranteId, String nombre, String descripcion, BigDecimal precio,
                              CategoriaPlato categoria, Integer unidadesDisponibles) {
        return new Plato(null, null, restauranteId, nombre, descripcion, precio, categoria, true,
                unidadesDisponibles, List.of());
    }

    public void actualizarDatos(String nombre, String descripcion, BigDecimal precio, CategoriaPlato categoria) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre del plato es obligatorio");
        }
        if (precio == null || precio.signum() <= 0) {
            throw new ReglaNegocioException("El precio del plato debe ser mayor que cero");
        }
        if (categoria == null) {
            throw new ReglaNegocioException("La categoría del plato es obligatoria");
        }
        this.nombre = nombre.trim();
        this.descripcion = descripcion == null ? null : descripcion.trim();
        this.precio = precio;
        this.categoria = categoria;
    }

    public void cambiarDisponibilidad(boolean habilitado, Integer unidadesDisponibles) {
        if (unidadesDisponibles != null && unidadesDisponibles < 0) {
            throw new ReglaNegocioException("Las unidades disponibles no pueden ser negativas");
        }
        this.habilitado = habilitado;
        this.unidadesDisponibles = unidadesDisponibles;
    }

    public boolean estaDisponible() {
        return habilitado && (unidadesDisponibles == null || unidadesDisponibles > 0);
    }

    /** Descuenta unidades al recibirse un pedido. Si no hay control de inventario, no hace nada. */
    public void descontarUnidades(int cantidad) {
        validarCantidad(cantidad);
        if (unidadesDisponibles != null) {
            unidadesDisponibles = Math.max(0, unidadesDisponibles - cantidad);
        }
    }

    /** Devuelve unidades al inventario cuando un pedido se cancela. */
    public void reponerUnidades(int cantidad) {
        validarCantidad(cantidad);
        if (unidadesDisponibles != null) {
            unidadesDisponibles = unidadesDisponibles + cantidad;
        }
    }

    public void agregarExtra(String nombre, BigDecimal precioAdicional) {
        Extra nuevo = new Extra(null, nombre, precioAdicional);
        boolean repetido = extras.stream().anyMatch(extra -> extra.nombre().equalsIgnoreCase(nuevo.nombre()));
        if (repetido) {
            throw new ReglaNegocioException("El plato '" + this.nombre + "' ya tiene un extra llamado '" + nuevo.nombre() + "'");
        }
        extras.add(nuevo);
    }

    public void quitarExtra(Long extraId) {
        boolean quitado = extras.removeIf(extra -> extra.id() != null && extra.id().equals(extraId));
        if (!quitado) {
            throw new RecursoNoEncontradoException("El plato " + id + " no tiene el extra " + extraId);
        }
    }

    private void validarCantidad(int cantidad) {
        if (cantidad < 1) {
            throw new ReglaNegocioException("La cantidad debe ser al menos 1");
        }
    }

    public Long getId() {
        return id;
    }

    /** Versión para control de concurrencia optimista; nula mientras el plato no se ha guardado. */
    public Long getVersion() {
        return version;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public CategoriaPlato getCategoria() {
        return categoria;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public Integer getUnidadesDisponibles() {
        return unidadesDisponibles;
    }

    public List<Extra> getExtras() {
        return Collections.unmodifiableList(extras);
    }
}
