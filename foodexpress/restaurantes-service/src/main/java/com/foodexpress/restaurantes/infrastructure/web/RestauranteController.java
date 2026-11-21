package com.foodexpress.restaurantes.infrastructure.web;

import com.foodexpress.restaurantes.application.port.in.RestauranteService;
import com.foodexpress.restaurantes.infrastructure.web.dto.RestauranteDtos.CambiarEstadoRequest;
import com.foodexpress.restaurantes.infrastructure.web.dto.RestauranteDtos.RestauranteRequest;
import com.foodexpress.restaurantes.infrastructure.web.dto.RestauranteDtos.RestauranteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestauranteResponse registrar(@Valid @RequestBody RestauranteRequest request) {
        return RestauranteResponse.de(restauranteService.registrar(request.aDatos()));
    }

    @GetMapping
    public List<RestauranteResponse> listar() {
        return restauranteService.listar().stream().map(RestauranteResponse::de).toList();
    }

    @GetMapping("/{id}")
    public RestauranteResponse obtener(@PathVariable("id") Long id) {
        return RestauranteResponse.de(restauranteService.obtener(id));
    }

    @PutMapping("/{id}")
    public RestauranteResponse actualizar(@PathVariable("id") Long id, @Valid @RequestBody RestauranteRequest request) {
        return RestauranteResponse.de(restauranteService.actualizar(id, request.aDatos()));
    }

    /** Abre o cierra el restaurante. Solo un restaurante abierto recibe pedidos. */
    @PatchMapping("/{id}/estado")
    public RestauranteResponse cambiarEstado(@PathVariable("id") Long id,
                                             @Valid @RequestBody CambiarEstadoRequest request) {
        return RestauranteResponse.de(restauranteService.cambiarEstado(id, request.estado()));
    }
}
