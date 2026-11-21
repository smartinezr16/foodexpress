package com.foodexpress.notificaciones.domain.model;

/**
 * A quién va dirigida una notificación. El correo solo se conoce para los clientes;
 * los restaurantes se notifican por su panel en tiempo real.
 */
public record Destinatario(TipoDestinatario tipo, String nombre, String email) {

    public static Destinatario cliente(String nombre, String email) {
        return new Destinatario(TipoDestinatario.CLIENTE, nombre, email);
    }

    public static Destinatario restaurante(String nombre) {
        return new Destinatario(TipoDestinatario.RESTAURANTE, nombre, null);
    }

    public boolean tieneEmail() {
        return email != null && !email.isBlank();
    }
}
