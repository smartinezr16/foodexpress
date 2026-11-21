package com.foodexpress.restaurantes.infrastructure.web;

import com.foodexpress.restaurantes.application.port.in.MenuAdminService;
import com.foodexpress.restaurantes.infrastructure.web.dto.MenuDtos.DisponibilidadRequest;
import com.foodexpress.restaurantes.infrastructure.web.dto.MenuDtos.ExtraRequest;
import com.foodexpress.restaurantes.infrastructure.web.dto.MenuDtos.PlatoRequest;
import com.foodexpress.restaurantes.infrastructure.web.dto.MenuDtos.PlatoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Administración del menú por parte del restaurante. Depende solo de MenuAdminService (ISP). */
@RestController
@RequestMapping("/api")
public class MenuAdminController {

    private final MenuAdminService menuAdminService;

    public MenuAdminController(MenuAdminService menuAdminService) {
        this.menuAdminService = menuAdminService;
    }

    @PostMapping("/restaurantes/{restauranteId}/platos")
    @ResponseStatus(HttpStatus.CREATED)
    public PlatoResponse agregarPlato(@PathVariable("restauranteId") Long restauranteId,
                                      @Valid @RequestBody PlatoRequest request) {
        return PlatoResponse.de(menuAdminService.agregarPlato(restauranteId, request.aDatos()));
    }

    @PutMapping("/platos/{platoId}")
    public PlatoResponse actualizarPlato(@PathVariable("platoId") Long platoId,
                                         @Valid @RequestBody PlatoRequest request) {
        return PlatoResponse.de(menuAdminService.actualizarPlato(platoId, request.aDatos()));
    }

    /** Habilita o retira el plato de la carta y, opcionalmente, fija sus unidades disponibles. */
    @PatchMapping("/platos/{platoId}/disponibilidad")
    public PlatoResponse cambiarDisponibilidad(@PathVariable("platoId") Long platoId,
                                               @Valid @RequestBody DisponibilidadRequest request) {
        return PlatoResponse.de(menuAdminService.cambiarDisponibilidad(
                platoId, request.habilitado(), request.unidadesDisponibles()));
    }

    @DeleteMapping("/platos/{platoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPlato(@PathVariable("platoId") Long platoId) {
        menuAdminService.eliminarPlato(platoId);
    }

    @PostMapping("/platos/{platoId}/extras")
    @ResponseStatus(HttpStatus.CREATED)
    public PlatoResponse agregarExtra(@PathVariable("platoId") Long platoId,
                                      @Valid @RequestBody ExtraRequest request) {
        return PlatoResponse.de(menuAdminService.agregarExtra(platoId, request.nombre(), request.precioAdicional()));
    }

    @DeleteMapping("/platos/{platoId}/extras/{extraId}")
    public PlatoResponse quitarExtra(@PathVariable("platoId") Long platoId, @PathVariable("extraId") Long extraId) {
        return PlatoResponse.de(menuAdminService.quitarExtra(platoId, extraId));
    }
}
