package com.foodexpress.restaurantes.infrastructure.web;

import com.foodexpress.restaurantes.application.port.in.MenuQueryService;
import com.foodexpress.restaurantes.domain.model.CategoriaPlato;
import com.foodexpress.restaurantes.infrastructure.web.dto.MenuDtos.PlatoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Consulta del menú (solo lectura). ISP: este controlador depende únicamente de
 * MenuQueryService; no puede modificar el menú ni por error.
 */
@RestController
@RequestMapping("/api")
public class MenuController {

    private final MenuQueryService menuQueryService;

    public MenuController(MenuQueryService menuQueryService) {
        this.menuQueryService = menuQueryService;
    }

    /** Menú del restaurante. Filtros opcionales: ?categoria=BEBIDA y ?soloDisponibles=true. */
    @GetMapping("/restaurantes/{restauranteId}/menu")
    public List<PlatoResponse> consultarMenu(
            @PathVariable("restauranteId") Long restauranteId,
            @RequestParam(name = "categoria", required = false) CategoriaPlato categoria,
            @RequestParam(name = "soloDisponibles", defaultValue = "false") boolean soloDisponibles) {
        return menuQueryService.consultarMenu(restauranteId).stream()
                .filter(plato -> categoria == null || plato.getCategoria() == categoria)
                .filter(plato -> !soloDisponibles || plato.estaDisponible())
                .map(PlatoResponse::de)
                .toList();
    }

    /** Platos puntuales con disponibilidad al instante. Lo usa el servicio de Pedidos: ?ids=1,2,3 */
    @GetMapping("/restaurantes/{restauranteId}/platos")
    public List<PlatoResponse> obtenerPlatos(@PathVariable("restauranteId") Long restauranteId,
                                             @RequestParam("ids") List<Long> ids) {
        return menuQueryService.obtenerPlatos(restauranteId, ids).stream().map(PlatoResponse::de).toList();
    }

    @GetMapping("/platos/{platoId}")
    public PlatoResponse obtenerPlato(@PathVariable("platoId") Long platoId) {
        return PlatoResponse.de(menuQueryService.obtenerPlato(platoId));
    }
}
