package com.foodexpress.pedidos.application.dto;

/** Lo que el servicio de Pedidos necesita saber de un restaurante. */
public record RestauranteInfo(Long id, String nombre, boolean abierto) {
}
