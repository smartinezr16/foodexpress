package com.foodexpress.restaurantes.domain.model;

import com.foodexpress.restaurantes.domain.exception.ReglaNegocioException;

import java.time.LocalTime;

/** Restaurante registrado en la plataforma. Solo recibe pedidos cuando está abierto. */
public class Restaurante {

    private final Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private EstadoRestaurante estado;

    public Restaurante(Long id, String nombre, String direccion, String telefono,
                       LocalTime horaApertura, LocalTime horaCierre, EstadoRestaurante estado) {
        this.id = id;
        this.estado = estado == null ? EstadoRestaurante.CERRADO : estado;
        actualizarDatos(nombre, direccion, telefono, horaApertura, horaCierre);
    }

    /** Un restaurante nuevo empieza cerrado: se abre cuando ya tiene su menú listo. */
    public static Restaurante registrar(String nombre, String direccion, String telefono,
                                        LocalTime horaApertura, LocalTime horaCierre) {
        return new Restaurante(null, nombre, direccion, telefono, horaApertura, horaCierre, EstadoRestaurante.CERRADO);
    }

    public void actualizarDatos(String nombre, String direccion, String telefono,
                                LocalTime horaApertura, LocalTime horaCierre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre del restaurante es obligatorio");
        }
        if (direccion == null || direccion.isBlank()) {
            throw new ReglaNegocioException("La dirección del restaurante es obligatoria");
        }
        if (horaApertura == null || horaCierre == null) {
            throw new ReglaNegocioException("El horario de operación es obligatorio");
        }
        if (horaApertura.equals(horaCierre)) {
            throw new ReglaNegocioException("La hora de apertura y la de cierre no pueden ser iguales");
        }
        this.nombre = nombre.trim();
        this.direccion = direccion.trim();
        this.telefono = telefono == null ? null : telefono.trim();
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
    }

    public void abrir() {
        this.estado = EstadoRestaurante.ABIERTO;
    }

    public void cerrar() {
        this.estado = EstadoRestaurante.CERRADO;
    }

    public boolean estaAbierto() {
        return estado == EstadoRestaurante.ABIERTO;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public EstadoRestaurante getEstado() {
        return estado;
    }
}
