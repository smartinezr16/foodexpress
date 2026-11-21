package com.foodexpress.restaurantes.application.dto;

import java.time.LocalTime;

public record DatosRestaurante(String nombre, String direccion, String telefono,
                               LocalTime horaApertura, LocalTime horaCierre) {
}
