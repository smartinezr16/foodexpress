package com.foodexpress.pedidos.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Plato tal como lo publica el menú del restaurante en este momento. */
public record PlatoMenu(Long id, String nombre, BigDecimal precio, boolean disponible, List<ExtraMenu> extras) {

    public PlatoMenu {
        extras = extras == null ? List.of() : List.copyOf(extras);
    }

    public Optional<ExtraMenu> buscarExtra(Long extraId) {
        return extras.stream().filter(extra -> extra.id().equals(extraId)).findFirst();
    }

    public record ExtraMenu(Long id, String nombre, BigDecimal precioAdicional) {
    }
}
