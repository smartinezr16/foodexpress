package com.foodexpress.pedidos.infrastructure.web.dto;

import com.foodexpress.pedidos.application.dto.CrearPedidoCommand;
import com.foodexpress.pedidos.application.dto.CrearPedidoCommand.ItemSolicitado;
import com.foodexpress.pedidos.domain.model.TipoPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CrearPedidoRequest(
        @NotNull(message = "El restaurante es obligatorio") Long restauranteId,
        @NotNull(message = "Los datos del cliente son obligatorios") @Valid ClienteRequest cliente,
        @NotNull(message = "El tipo de pedido es obligatorio (LOCAL o DELIVERY)") TipoPedido tipo,
        @Size(max = 255) String direccionEntrega,
        @NotEmpty(message = "El pedido debe tener al menos un plato") @Valid List<ItemRequest> items) {

    public record ClienteRequest(
            @NotBlank(message = "El nombre del cliente es obligatorio") String nombre,
            @NotBlank(message = "El correo del cliente es obligatorio")
            @Email(message = "El correo del cliente no es válido") String email) {
    }

    public record ItemRequest(
            @NotNull(message = "Cada ítem debe indicar el plato") Long platoId,
            @Min(value = 1, message = "La cantidad debe ser al menos 1") int cantidad,
            List<Long> extraIds,
            @Size(max = 500) String notasEspeciales) {
    }

    public CrearPedidoCommand aCommand() {
        List<ItemSolicitado> solicitados = items.stream()
                .map(item -> new ItemSolicitado(item.platoId(), item.cantidad(), item.extraIds(), item.notasEspeciales()))
                .toList();
        return new CrearPedidoCommand(restauranteId, cliente.nombre(), cliente.email(), tipo, direccionEntrega, solicitados);
    }
}
