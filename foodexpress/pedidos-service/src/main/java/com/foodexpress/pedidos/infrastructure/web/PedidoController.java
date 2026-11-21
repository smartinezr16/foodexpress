package com.foodexpress.pedidos.infrastructure.web;

import com.foodexpress.pedidos.application.usecase.AvanzarEstadoPedidoUseCase;
import com.foodexpress.pedidos.application.usecase.CancelarPedidoUseCase;
import com.foodexpress.pedidos.application.usecase.ConsultarPedidosUseCase;
import com.foodexpress.pedidos.application.usecase.CrearPedidoUseCase;
import com.foodexpress.pedidos.domain.exception.PedidoInvalidoException;
import com.foodexpress.pedidos.domain.model.Pedido;
import com.foodexpress.pedidos.infrastructure.web.dto.CancelarPedidoRequest;
import com.foodexpress.pedidos.infrastructure.web.dto.CrearPedidoRequest;
import com.foodexpress.pedidos.infrastructure.web.dto.PedidoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada HTTP. SRP: el controlador solo traduce entre HTTP y los
 * casos de uso; no contiene reglas de negocio.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final CrearPedidoUseCase crearPedido;
    private final AvanzarEstadoPedidoUseCase avanzarEstado;
    private final CancelarPedidoUseCase cancelarPedido;
    private final ConsultarPedidosUseCase consultarPedidos;

    public PedidoController(CrearPedidoUseCase crearPedido,
                            AvanzarEstadoPedidoUseCase avanzarEstado,
                            CancelarPedidoUseCase cancelarPedido,
                            ConsultarPedidosUseCase consultarPedidos) {
        this.crearPedido = crearPedido;
        this.avanzarEstado = avanzarEstado;
        this.cancelarPedido = cancelarPedido;
        this.consultarPedidos = consultarPedidos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crear(@Valid @RequestBody CrearPedidoRequest request) {
        return PedidoResponse.de(crearPedido.ejecutar(request.aCommand()));
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable("id") Long id) {
        return PedidoResponse.de(consultarPedidos.porId(id));
    }

    /** Lista pedidos de un restaurante (?restauranteId=) o de un cliente (?clienteEmail=). */
    @GetMapping
    public List<PedidoResponse> listar(
            @RequestParam(name = "restauranteId", required = false) Long restauranteId,
            @RequestParam(name = "clienteEmail", required = false) String clienteEmail) {
        List<Pedido> pedidos;
        if (restauranteId != null) {
            pedidos = consultarPedidos.porRestaurante(restauranteId);
        } else if (clienteEmail != null && !clienteEmail.isBlank()) {
            pedidos = consultarPedidos.porCliente(clienteEmail);
        } else {
            throw new PedidoInvalidoException("Indique restauranteId o clienteEmail para listar pedidos");
        }
        return pedidos.stream().map(PedidoResponse::de).toList();
    }

    /** Pasa el pedido a su siguiente estado: RECIBIDO, EN_PREPARACION, LISTO, ENTREGADO. */
    @PatchMapping("/{id}/avanzar")
    public PedidoResponse avanzar(@PathVariable("id") Long id) {
        return PedidoResponse.de(avanzarEstado.ejecutar(id));
    }

    @PostMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable("id") Long id, @Valid @RequestBody CancelarPedidoRequest request) {
        return PedidoResponse.de(cancelarPedido.ejecutar(id, request.canceladoPor(), request.motivo()));
    }
}
