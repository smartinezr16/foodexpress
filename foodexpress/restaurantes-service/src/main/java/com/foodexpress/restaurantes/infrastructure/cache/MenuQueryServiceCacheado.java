package com.foodexpress.restaurantes.infrastructure.cache;

import com.foodexpress.restaurantes.application.port.in.MenuQueryService;
import com.foodexpress.restaurantes.application.usecase.ConsultarMenuUseCase;
import com.foodexpress.restaurantes.domain.model.Plato;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Envuelve la consulta del menú para servirla desde caché.
 *
 * OCP: la caché se agregó sin modificar ConsultarMenuUseCase. Los controladores
 * siguen dependiendo de MenuQueryService y reciben esta implementación (@Primary).
 *
 * Solo se guarda en caché el menú completo. La consulta puntual de platos que hace
 * el servicio de Pedidos va siempre a la base de datos, porque debe validar la
 * disponibilidad real en el momento de la orden.
 */
@Service
@Primary
public class MenuQueryServiceCacheado implements MenuQueryService {

    private final ConsultarMenuUseCase delegado;

    public MenuQueryServiceCacheado(ConsultarMenuUseCase delegado) {
        this.delegado = delegado;
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.CACHE_MENUS, key = "#p0")
    public List<Plato> consultarMenu(Long restauranteId) {
        return delegado.consultarMenu(restauranteId);
    }

    @Override
    public List<Plato> obtenerPlatos(Long restauranteId, Collection<Long> platoIds) {
        return delegado.obtenerPlatos(restauranteId, platoIds);
    }

    @Override
    public Plato obtenerPlato(Long platoId) {
        return delegado.obtenerPlato(platoId);
    }
}
