package com.foodexpress.pedidos.infrastructure.menu;

import com.foodexpress.pedidos.application.dto.PlatoMenu;
import com.foodexpress.pedidos.application.dto.PlatoMenu.ExtraMenu;
import com.foodexpress.pedidos.application.dto.RestauranteInfo;
import com.foodexpress.pedidos.application.exception.RestauranteNoDisponibleException;
import com.foodexpress.pedidos.application.exception.ServicioMenuNoDisponibleException;
import com.foodexpress.pedidos.application.port.out.MenuRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador de salida: implementa MenuRepository consultando por HTTP al
 * microservicio de Restaurantes.
 *
 * La consulta es síncrona a propósito: el pedido debe validarse contra el menú
 * vigente "en el momento de la orden". Todo lo demás entre servicios es asíncrono.
 */
@Component
public class MenuRepositoryRestAdapter implements MenuRepository {

    private static final int TIEMPO_CONEXION_MS = 2000;
    private static final int TIEMPO_LECTURA_MS = 3000;

    private final RestClient restClient;

    public MenuRepositoryRestAdapter(RestClient.Builder builder,
                                     @Value("${foodexpress.restaurantes.url}") String restaurantesUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIEMPO_CONEXION_MS);
        requestFactory.setReadTimeout(TIEMPO_LECTURA_MS);
        this.restClient = builder
                .baseUrl(restaurantesUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public RestauranteInfo obtenerRestaurante(Long restauranteId) {
        try {
            RestauranteDto dto = restClient.get()
                    .uri("/api/restaurantes/{id}", restauranteId)
                    .retrieve()
                    .body(RestauranteDto.class);
            if (dto == null) {
                throw new RestauranteNoDisponibleException("No existe el restaurante " + restauranteId);
            }
            return new RestauranteInfo(dto.id(), dto.nombre(), "ABIERTO".equals(dto.estado()));
        } catch (HttpClientErrorException.NotFound e) {
            throw new RestauranteNoDisponibleException("No existe el restaurante " + restauranteId);
        } catch (RestClientException e) {
            throw new ServicioMenuNoDisponibleException(
                    "No fue posible consultar el restaurante. Intente de nuevo en unos segundos", e);
        }
    }

    @Override
    public List<PlatoMenu> obtenerPlatos(Long restauranteId, Collection<Long> platoIds) {
        if (platoIds.isEmpty()) {
            return List.of();
        }
        String ids = platoIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        try {
            List<PlatoDto> platos = restClient.get()
                    .uri("/api/restaurantes/{id}/platos?ids={ids}", restauranteId, ids)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<PlatoDto>>() {
                    });
            if (platos == null) {
                return List.of();
            }
            return platos.stream().map(PlatoDto::aPlatoMenu).toList();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RestauranteNoDisponibleException("No existe el restaurante " + restauranteId);
        } catch (RestClientException e) {
            throw new ServicioMenuNoDisponibleException(
                    "No fue posible consultar el menú. Intente de nuevo en unos segundos", e);
        }
    }

    // Vista parcial de las respuestas del servicio de Restaurantes: solo los campos que Pedidos usa.

    record RestauranteDto(Long id, String nombre, String estado) {
    }

    record PlatoDto(Long id, String nombre, BigDecimal precio, boolean disponible, List<ExtraDto> extras) {

        PlatoMenu aPlatoMenu() {
            List<ExtraMenu> extrasMenu = extras == null
                    ? List.of()
                    : extras.stream().map(e -> new ExtraMenu(e.id(), e.nombre(), e.precioAdicional())).toList();
            return new PlatoMenu(id, nombre, precio, disponible, extrasMenu);
        }
    }

    record ExtraDto(Long id, String nombre, BigDecimal precioAdicional) {
    }
}
