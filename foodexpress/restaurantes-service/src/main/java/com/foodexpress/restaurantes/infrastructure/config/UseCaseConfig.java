package com.foodexpress.restaurantes.infrastructure.config;

import com.foodexpress.restaurantes.application.port.out.EventoProcesadoRepository;
import com.foodexpress.restaurantes.application.port.out.MenuCache;
import com.foodexpress.restaurantes.application.port.out.PlatoRepository;
import com.foodexpress.restaurantes.application.port.out.RestauranteRepository;
import com.foodexpress.restaurantes.application.usecase.AjustarInventarioUseCase;
import com.foodexpress.restaurantes.application.usecase.ConsultarMenuUseCase;
import com.foodexpress.restaurantes.application.usecase.GestionarMenuUseCase;
import com.foodexpress.restaurantes.application.usecase.GestionarRestaurantesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Punto de ensamblaje: conecta los casos de uso (Java puro) con sus adaptadores.
 * Spring solo aparece en la capa de infraestructura.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public GestionarRestaurantesUseCase gestionarRestaurantesUseCase(RestauranteRepository restauranteRepository) {
        return new GestionarRestaurantesUseCase(restauranteRepository);
    }

    @Bean
    public ConsultarMenuUseCase consultarMenuUseCase(RestauranteRepository restauranteRepository,
                                                     PlatoRepository platoRepository) {
        return new ConsultarMenuUseCase(restauranteRepository, platoRepository);
    }

    @Bean
    public GestionarMenuUseCase gestionarMenuUseCase(RestauranteRepository restauranteRepository,
                                                     PlatoRepository platoRepository,
                                                     MenuCache menuCache) {
        return new GestionarMenuUseCase(restauranteRepository, platoRepository, menuCache);
    }

    @Bean
    public AjustarInventarioUseCase ajustarInventarioUseCase(PlatoRepository platoRepository,
                                                             EventoProcesadoRepository eventosProcesados,
                                                             MenuCache menuCache) {
        return new AjustarInventarioUseCase(platoRepository, eventosProcesados, menuCache);
    }
}
