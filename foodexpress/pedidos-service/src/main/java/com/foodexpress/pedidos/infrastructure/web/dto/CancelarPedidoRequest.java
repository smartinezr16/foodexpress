package com.foodexpress.pedidos.infrastructure.web.dto;

import com.foodexpress.pedidos.domain.model.estado.ActorCancelacion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CancelarPedidoRequest(
        @NotNull(message = "Debe indicar quién cancela (CLIENTE o RESTAURANTE)") ActorCancelacion canceladoPor,
        @Size(max = 500) String motivo) {
}
